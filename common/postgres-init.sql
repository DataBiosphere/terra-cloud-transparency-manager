CREATE ROLE ctm_user WITH LOGIN ENCRYPTED PASSWORD 'ctm_pass';
CREATE DATABASE cloudtransparency_db OWNER ctm_user;
