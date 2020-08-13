CREATE DATABASE IF NOT EXISTS legacy;

USE legacy;

CREATE TABLE IF NOT EXISTS legacy.customers (
    internalId        VARCHAR(10),
    externalId        VARCHAR(10),
    masterExternalId        VARCHAR(10),
    name        VARCHAR(100),
    customerType INT,
    companyNumber VARCHAR(12),
    PRIMARY KEY (internalId)
);

DELETE FROM legacy.customers;

INSERT INTO legacy.customers VALUES ('45435', '12345', NULL, NULL, 2, '32423-342');
