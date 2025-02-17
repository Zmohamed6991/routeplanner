CREATE TABLE IF NOT EXISTS gate (
    id VARCHAR(3) PRIMARY KEY,
    name VARCHAR(20),
    connections JSONB
);