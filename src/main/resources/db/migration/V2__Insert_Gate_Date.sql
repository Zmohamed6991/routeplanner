INSERT INTO gate (id, name, connections) VALUES
    ('SOL', 'Sol', '[{ "id": "RAN", "hu": "100" }, { "id": "PRX", "hu": "90" }, { "id": "SIR", "hu": "100" }, { "id": "ARC", "hu": "200" }, { "id": "ALD", "hu": "250" }]'),
    ('PRX', 'Proxima', '[{ "id": "SOL", "hu": "90" }, { "id": "SIR", "hu": "100" }, { "id": "ALT", "hu": "150" }]'),
    ('SIR', 'Sirius', '[{ "id": "SOL", "hu": "80" }, { "id": "PRX", "hu": "10" }, { "id": "CAS", "hu": "200" }]'),
    ('CAS', 'Castor', '[{ "id": "SIR", "hu": "200" }, { "id": "PRO", "hu": "120" }]'),
    ('PRO', 'Procyon', '[{ "id": "CAS", "hu": "80" }]'),
    ('DEN', 'Denebula', '[{ "id": "PRO", "hu": "5" }, { "id": "ARC", "hu": "2" }, { "id": "FOM", "hu": "8" }, { "id": "RAN", "hu": "100" }, { "id": "ALD", "hu": "3" }]'),
    ('RAN', 'Ran', '[{ "id": "SOL", "hu": "100" }]'),
    ('ARC', 'Arcturus', '[{ "id": "SOL", "hu": "500" }, { "id": "DEN", "hu": "120" }]'),
    ('FOM', 'Fomalhaut', '[{ "id": "PRX", "hu": "10" }, { "id": "DEN", "hu": "20" }, { "id": "ALS", "hu": "9" }]'),
    ('ALT', 'Altair', '[{ "id": "FOM", "hu": "140" }, { "id": "VEG", "hu": "220" }]'),
    ('VEG', 'Vega', '[{ "id": "ARC", "hu": "220" }, { "id": "ALD", "hu": "580" }]'),
    ('ALD', 'Aldermain', '[{ "id": "SOL", "hu": "200" }, { "id": "ALS", "hu": "160" }, { "id": "VEG", "hu": "320" }]'),
    ('ALS', 'Alshain', '[{ "id": "ALT", "hu": "1" }, { "id": "ALD", "hu": "1" }]');

