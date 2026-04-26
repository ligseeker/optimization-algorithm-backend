ALTER TABLE flow_graph
    ADD COLUMN IF NOT EXISTS graph_version BIGINT NOT NULL DEFAULT 1 AFTER graph_status;

CREATE INDEX idx_flow_graph_graph_version ON flow_graph (graph_version);
