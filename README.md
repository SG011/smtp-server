# smtp-server

High-throughput SMTP Mail Transfer Agent — Java 25, Netty, Kafka, Redis, Cassandra. Handles 10M+ concurrent connections via non-blocking Netty I/O.

## Architecture

```
Inbound: TCP → Netty → SmtpServerHandler → AuthValidator → MailQueue (Kafka)
Delivery: MailQueue → DeliveryWorker → SmtpClient → Recipient SMTP server
Tracking: DeliveryWorker → DeliveryTracker → Cassandra
```

## Features
- Full SMTP protocol (EHLO, MAIL FROM, RCPT TO, DATA, QUIT)
- SPF, DKIM, DMARC email authentication
- Redis token-bucket rate limiting (1000/min per sender domain)
- Kafka-backed durable mail queue
- Soft bounce retry, hard bounce dead-letter
- Delivery tracking in Cassandra

## Running Locally

```bash
docker-compose up -d
mvn spring-boot:run   # SMTP on port 2525
# Test with: echo -e "EHLO test\nMAIL FROM:<you@test.com>\nRCPT TO:<recv@test.com>\nDATA\nSubject: Hi\n\nHello\n.\nQUIT" | nc localhost 2525
```

## Tech Stack
Java 25 · Netty 4.1 · Spring Boot 3.4 · Apache Kafka · Redis 7 · Apache Cassandra 5 · Testcontainers
