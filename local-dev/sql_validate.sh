#!/bin/bash

# validate postgres
echo "sleeping for 5 seconds during postgres boot..."
sleep 5
PGPASSWORD=ctm_pass psql --username ctm_user -d cloudtransparency_db -c "SELECT VERSION();SELECT NOW()"
