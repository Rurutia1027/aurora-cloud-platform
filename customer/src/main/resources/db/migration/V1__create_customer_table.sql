-- V1__create_customer_table.sql
CREATE SEQUENCE customer_id_sequence START 1;

CREATE TABLE customer (
                          id BIGINT DEFAULT nextval('customer_id_sequence') PRIMARY KEY,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          email VARCHAR(100) NOT NULL
);
