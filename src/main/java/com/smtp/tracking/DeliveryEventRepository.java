package com.smtp.tracking;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface DeliveryEventRepository extends CassandraRepository<DeliveryEvent, String> {
    List<DeliveryEvent> findByMessageId(String messageId);
}
