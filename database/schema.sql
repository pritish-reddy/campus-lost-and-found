-- Campus Lost and Found Management System: database schema
-- The application runs this file automatically on startup
-- (a copy lives in src/main/resources/schema.sql). Running it twice is safe.

CREATE TABLE IF NOT EXISTS found_items (
    id          BIGSERIAL PRIMARY KEY,
    item_name   VARCHAR(100) NOT NULL,
    description TEXT,
    location    VARCHAR(150),
    status      VARCHAR(20) DEFAULT 'AVAILABLE'
);

CREATE TABLE IF NOT EXISTS lost_items (
    id          BIGSERIAL PRIMARY KEY,
    item_name   VARCHAR(100) NOT NULL,
    description TEXT,
    location    VARCHAR(150),
    status      VARCHAR(20) DEFAULT 'REPORTED'
);

CREATE TABLE IF NOT EXISTS claims (
    id             BIGSERIAL PRIMARY KEY,
    found_item_id  BIGINT REFERENCES found_items(id),
    claimant_name  VARCHAR(100) NOT NULL,
    claim_reason   TEXT,
    status         VARCHAR(20) DEFAULT 'PENDING'
);
