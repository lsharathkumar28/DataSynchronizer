CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    user_id       UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255),
    first_name    VARCHAR(255),
    middle_name   VARCHAR(255),
    last_name     VARCHAR(255),
    email_id      VARCHAR(255),
    phone_number  VARCHAR(50),
    attributes    JSONB
);

