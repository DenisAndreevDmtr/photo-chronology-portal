CREATE DATABASE photo_chronology_db;
CREATE USER chronologist WITH ENCRYPTED PASSWORD '123chronologist123' NOINHERIT;
GRANT ALL PRIVILEGES ON DATABASE photo_chronology_db TO chronologist;