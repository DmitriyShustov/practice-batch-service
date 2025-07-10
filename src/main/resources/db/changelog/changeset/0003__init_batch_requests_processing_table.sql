CREATE TABLE IF NOT EXISTS batch_requests_processing (
    id BIGSERIAL PRIMARY KEY,
    batch_request_id BIGINT NOT NULL,
    processed_percentage INT NOT NULL,
    successful_count INT NOT NULL,
    failed_count INT NOT NULL,

    CONSTRAINT fk_batch_request
    FOREIGN KEY (batch_request_id)
    REFERENCES batch_requests(id)
    ON DELETE CASCADE
);