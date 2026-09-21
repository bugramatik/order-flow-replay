package com.ewolff.microservice.replay;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * One order record the replay service has seen on the order topic. The raw
 * payload is kept as it arrived so a replay sends exactly what the shipping and
 * invoicing services failed on.
 */
@Entity
@Table(name = "REPLAY_RECORD")
public class ReplayRecord {

	@Id
	@GeneratedValue
	private long id;

	@Column(name = "ORDER_ID")
	private long orderId;

	@Lob
	@Column(name = "PAYLOAD", nullable = false)
	private String payload;

	@Column(name = "SEEN_AT", nullable = false)
	private Date seenAt;

	@Column(name = "REPLAY_COUNT", nullable = false)
	private int replayCount;

	@Column(name = "LAST_REPLAYED_AT")
	private Date lastReplayedAt;

	public ReplayRecord() {
		super();
	}

	public ReplayRecord(long orderId, String payload, Date seenAt) {
		super();
		this.orderId = orderId;
		this.payload = payload;
		this.seenAt = seenAt;
		this.replayCount = 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Date getSeenAt() {
		return seenAt;
	}

	public void setSeenAt(Date seenAt) {
		this.seenAt = seenAt;
	}

	public int getReplayCount() {
		return replayCount;
	}

	public void setReplayCount(int replayCount) {
		this.replayCount = replayCount;
	}

	public Date getLastReplayedAt() {
		return lastReplayedAt;
	}

	public void setLastReplayedAt(Date lastReplayedAt) {
		this.lastReplayedAt = lastReplayedAt;
	}

	public void markReplayed(Date when) {
		this.replayCount = this.replayCount + 1;
		this.lastReplayedAt = when;
	}
}
