CREATE TABLE IF NOT EXISTS batch_requests (
    id BIGSERIAL PRIMARY KEY,
    request_time TIMESTAMP NOT NULL,
    name VARCHAR(255),
    total_requests INT NOT NULL,
    status VARCHAR(20) NOT NULL
);