CREATE TYPE batch_status AS ENUM (
    'RECEIVED',
    'PROCESSING',
    'COMPLETED',
    'FAILED'
);

CREATE TABLE IF NOT EXISTS batch_requests (
    id SERIAL PRIMARY KEY,
    request_time TIMESTAMP NOT NULL,
    name VARCHAR(255),
    total_requests INT NOT NULL,
    status batch_status NOT NULL
);