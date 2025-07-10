CREATE TABLE IF NOT EXISTS batch_request_items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    xml_content TEXT,
    status SMALLINT NOT NULL,
    batch_request_id BIGINT NOT NULL,

    CONSTRAINT fk_batch_request_items_batch_requests
    FOREIGN KEY (batch_request_id)
    REFERENCES batch_requests(id)
    ON DELETE CASCADE
);