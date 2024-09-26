#!/bin/bash

echo "Resetting the database..."

# Disconnect all existing connections to the database
sudo -i -u postgres psql -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'newsrefdb' AND pid <> pg_backend_pid();"

# Drop the database
sudo -i -u postgres psql -c "DROP DATABASE IF EXISTS newsrefdb;"

# Create the database
sudo -i -u postgres psql -c "CREATE DATABASE newsrefdb;"

# Assign ownership to the user 'newsref'
sudo -i -u postgres psql -c "ALTER DATABASE newsrefdb OWNER TO newsref;"

echo "Database reset complete!"
