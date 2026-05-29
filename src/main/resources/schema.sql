CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    customer_id    BIGINT       NOT NULL,
    customer_name  VARCHAR(100) NOT NULL,
    amount         DOUBLE       NOT NULL,
    transaction_date DATE       NOT NULL
);
