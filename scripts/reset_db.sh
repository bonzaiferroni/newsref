#!/bin/bash

echo "Resetting the database..."

# Drop the database
sudo -i -u postgres psql -c "DROP DATABASE IF EXISTS newsrefdb;"

# Create the database
sudo -i -u postgres psql -c "CREATE DATABASE newsrefdb;"

# Assign ownership to the user 'newsref'
sudo -i -u postgres psql -c "ALTER DATABASE newsrefdb OWNER TO newsref;"

echo "Database reset complete!"
