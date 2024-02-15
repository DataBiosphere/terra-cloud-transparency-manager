CREATE DATABASE cloudtransparency_db;
CREATE ROLE ctm_user WITH LOGIN ENCRYPTED PASSWORD 'ctm_pass';
GRANT CREATE ON DATABASE cloudtransparency_db to ctm_user;
