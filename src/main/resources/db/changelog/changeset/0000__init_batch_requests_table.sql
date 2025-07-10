CREATE TABLE IF NOT EXISTS batch_requests (
    id BIGSERIAL PRIMARY KEY,
    hash VARCHAR(64) NOT NULL,
    previous_attempt TIMESTAMP,
    next_attempt TIMESTAMP,
    request_time TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    total_requests INT NOT NULL,
    status VARCHAR(20) NOT NULL
);