INSERT INTO accounts (name)
VALUES ('demo-account')
ON CONFLICT DO NOTHING;