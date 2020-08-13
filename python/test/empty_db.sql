
CREATE TABLE customers (
    internalId        INT,
    externalId        VARCHAR(10),
    masterExternalId        VARCHAR(10),
    name        VARCHAR(100),
    customerType INT,
    companyNumber VARCHAR(12),
    addressId INT,
    PRIMARY KEY (internalId),
    FOREIGN KEY (addressId) REFERENCES addresses(addressId)
);

CREATE TABLE addresses (
        addressId        INT,
        street VARCHAR(100),
        city   VARCHAR(100),
        postalCode VARCHAR(10),
        PRIMARY KEY (addressId)
);

CREATE TABLE shoppinglists (
        shoppinglistId INT,
        products  VARCHAR(500),
        PRIMARY KEY (shoppinglistId)
);

CREATE TABLE customer_shoppinglists (
        customerId INT,
        shoppinglistId INT,
        FOREIGN KEY (customerId) REFERENCES customers(internalId),
        FOREIGN KEY (shoppinglistId) REFERENCES shoppinglists(shoppinglistId)
);
