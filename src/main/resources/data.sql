CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR 'org.h2gis.functions.factory.H2GISFunctions.load';
CALL H2GIS_SPATIAL();

INSERT INTO category (created_at, updated_at, name)
VALUES (NOW(), NOW(), 'SPOT'),
       (NOW(), NOW(), 'GENERAL'),
       (NOW(), NOW(), 'PROMOTION');