CREATE TABLE IF NOT EXISTS batch_queue_items (
    id BIGSERIAL PRIMARY KEY,
    batch_item_id BIGINT NOT NULL,
    next_processing_time TIMESTAMP NOT NULL,
    retry_count INT NOT NULL,
    priority SMALLINT NOT NULL,

    CONSTRAINT fk_batch_queue_items_batch_item
    FOREIGN KEY (batch_item_id)
    REFERENCES batch_request_items(id)
    ON DELETE CASCADE
);