-- Initialize StateFin database
-- This script runs when PostgreSQL container starts for the first time

-- Create additional databases if needed
CREATE DATABASE statefin_test;

-- Create extension for UUID generation (optional)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE statefin_dev TO postgres;
GRANT ALL PRIVILEGES ON DATABASE statefin_test TO postgres;

-- Grant schema permissions
\c statefin_dev;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;

\c statefin_test;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;