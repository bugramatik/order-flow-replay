package com.ewolff.microservice.replay;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Keeps every record seen on the order topic and puts a stored record back on
 * the topic when it is asked to. The replay uses the same key the order service
 * used, so the record lands on the same partition.
 */
@Service
public class ReplayService {

	public static final String ORDER_TOPIC = "order";

	private static final Logger log = LoggerFactory.getLogger(ReplayService.class);

	private final ReplayRecordRepository replayRecordRepository;

	private final KafkaTemplate<String, String> kafkaTemplate;

	public ReplayService(ReplayRecordRepository replayRecordRepository, KafkaTemplate<String, String> kafkaTemplate) {
		super();
		this.replayRecordRepository = replayRecordRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	@KafkaListener(topics = ORDER_TOPIC, groupId = "replay")
	@Transactional
	public void record(String payload, Acknowledgment acknowledgment) {
		if (payload == null || payload.isBlank()) {
			log.warn("Ignoring empty record on topic {}", ORDER_TOPIC);
			acknowledgment.acknowledge();
			return;
		}
		long orderId = OrderPayload.orderId(payload);
		ReplayRecord record = new ReplayRecord(orderId, payload, new Date());
		replayRecordRepository.save(record);
		log.info("Stored order {} for replay", orderId);
		acknowledgment.acknowledge();
	}

	@Transactional
	public Optional<ReplayRecord> replay(long id) {
		Optional<ReplayRecord> found = replayRecordRepository.findById(id);
		if (found.isEmpty()) {
			return Optional.empty();
		}
		ReplayRecord record = found.get();
		kafkaTemplate.send(ORDER_TOPIC, record.getOrderId() + "created", record.getPayload());
		record.markReplayed(new Date());
		replayRecordRepository.save(record);
		log.info("Replayed order {} (record {}, replay {})", record.getOrderId(), record.getId(),
				record.getReplayCount());
		return Optional.of(record);
	}

	public Iterable<ReplayRecord> all() {
		return replayRecordRepository.findAll();
	}

	public long count() {
		return replayRecordRepository.count();
	}
}
