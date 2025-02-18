# main.tf
terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  required_version = ">= 1.0"

  backend "s3" {
    bucket         = "route-planner-terraform-bucket"
    key            = "prod/terraform.tfstate"
    region         = "eu-west-2"
    encrypt        = true
    dynamodb_table = "terraform-lock"
  }
}

provider "aws" {
  region = "eu-west-2"
}

# VPC and Networking
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "route-planner-vpc"
  }
}

resource "aws_subnet" "public_1" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.1.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "eu-west-2a"

  tags = {
    Name = "route-planner-public-1"
  }
}

resource "aws_subnet" "public_2" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.2.0/24"
  map_public_ip_on_launch = true
  availability_zone       = "eu-west-2b"

  tags = {
    Name = "route-planner-public-2"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "route-planner-igw"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "route-planner-public-rt"
  }
}

resource "aws_route_table_association" "public_1" {
  subnet_id      = aws_subnet.public_1.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "public_2" {
  subnet_id      = aws_subnet.public_2.id
  route_table_id = aws_route_table.public.id
}

# Security Groups
resource "aws_security_group" "app" {
  name        = "route-planner-app-sg"
  description = "Security group for route planner application"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "route-planner-app-sg"
  }
}

# RDS Instance
resource "aws_db_subnet_group" "rds" {
  name       = "route-planner-rds"
  subnet_ids = [aws_subnet.public_1.id, aws_subnet.public_2.id]

  tags = {
    Name = "route-planner-rds-subnet-group"
  }
}

resource "aws_db_instance" "postgres" {
  identifier             = "route-planner-db"
  allocated_storage      = 20
  storage_type          = "gp2"
  engine                = "postgres"
  engine_version        = "16.3"
  instance_class        = "db.t3.micro"
  db_name               = "hstc"
  username              = "postgres"
  password              = var.db_password
  skip_final_snapshot   = true
  publicly_accessible   = true
  db_subnet_group_name  = aws_db_subnet_group.rds.name
  vpc_security_group_ids = [aws_security_group.app.id]

  tags = {
    Name = "route-planner-db"
  }
}

# IAM
resource "aws_iam_role" "ec2_role" {
  name = "route-planner-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "route-planner-profile"
  role = aws_iam_role.ec2_role.name
}

# EC2 Instance
resource "aws_instance" "app" {
  # ami-0505148b3591e4c07 was Amazon Linux, change to Ubuntu 22.04
  ami                    = "ami-0505c450286024b66" # Ubuntu 22.04 LTS in eu-west-2
  instance_type          = "t2.micro"
  subnet_id              = aws_subnet.public_1.id
  vpc_security_group_ids = [aws_security_group.app.id]
  key_name              = var.key_name
  iam_instance_profile  = aws_iam_instance_profile.ec2_profile.name

  user_data = <<-EOF
              #!/bin/bash
              # Update package list and install dependencies
              apt-get update
              apt-get install -y openjdk-17-jdk awscli

              # Create application directory
              mkdir -p /opt/route-planner

              # Wait for instance profile to propagate
              sleep 10

              # Copy application from S3
              aws s3 cp s3://${var.artifact_bucket}/route-planner-latest.jar /opt/route-planner/app.jar

              # Create service file
              cat > /etc/systemd/system/route-planner.service <<EOL
              [Unit]
              Description=Route Planner Application
              After=network.target

              [Service]
              Environment="SPRING_DATASOURCE_URL=jdbc:postgresql://${aws_db_instance.postgres.endpoint}/${aws_db_instance.postgres.db_name}"
              Environment="SPRING_DATASOURCE_USERNAME=${aws_db_instance.postgres.username}"
              Environment="SPRING_DATASOURCE_PASSWORD=${var.db_password}"
              Type=simple
              User=root
              ExecStart=/usr/bin/java -jar /opt/route-planner/app.jar
              Restart=on-failure

              [Install]
              WantedBy=multi-user.target
              EOL

              # Enable and start service
              systemctl daemon-reload
              systemctl enable route-planner
              systemctl start route-planner
              EOF

  tags = {
    Name = "route-planner-app"
  }

  # Add this to ensure user_data script completes
  user_data_replace_on_change = true
}

resource "aws_iam_role_policy" "s3_access" {
  name = "route-planner-s3-access"
  role = aws_iam_role.ec2_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ]
        Resource = [
          "arn:aws:s3:::route-planner-artifacts",
          "arn:aws:s3:::route-planner-artifacts/*"
        ]
      }
    ]
  })
}


# Variables
variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

variable "key_name" {
  description = "Name of the EC2 key pair"
  type        = string
}

variable "artifact_bucket" {
  description = "S3 bucket for artifacts"
  type        = string
}

# Outputs
output "rds_endpoint" {
  value = aws_db_instance.postgres.endpoint
}

output "app_public_ip" {
  value = aws_instance.app.public_ip
}