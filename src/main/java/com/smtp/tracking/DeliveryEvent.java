package com.smtp.tracking;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("delivery_events")
public class DeliveryEvent {

    @PrimaryKeyColumn(name = "message_id", type = PrimaryKeyType.PARTITIONED)
    private String messageId;

    @PrimaryKeyColumn(name = "recorded_at", type = PrimaryKeyType.CLUSTERED,
            ordering = org.springframework.data.cassandra.core.cql.Ordering.DESCENDING)
    private Instant recordedAt;

    @Column("status")
    private String status;

    public String getMessageId() { return messageId; }
    public void setMessageId(String v) { this.messageId = v; }
    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant v) { this.recordedAt = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}
