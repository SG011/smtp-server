CREATE KEYSPACE IF NOT EXISTS smtp
  WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS smtp.delivery_events (
  message_id TEXT,
  recorded_at TIMESTAMP,
  status TEXT,
  PRIMARY KEY (message_id, recorded_at)
) WITH CLUSTERING ORDER BY (recorded_at DESC);
