package com.ewolff.microservice.replay.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ewolff.microservice.replay.ReplayRecord;
import com.ewolff.microservice.replay.ReplayService;

@RestController
@RequestMapping("/replay")
public class ReplayController {

	private final ReplayService replayService;

	public ReplayController(ReplayService replayService) {
		super();
		this.replayService = replayService;
	}

	@GetMapping
	public List<ReplayRecordView> list() {
		List<ReplayRecordView> views = new ArrayList<>();
		for (ReplayRecord record : replayService.all()) {
			views.add(ReplayRecordView.of(record));
		}
		return views;
	}

	@PostMapping("/{id}")
	public ResponseEntity<ReplayRecordView> replay(@PathVariable("id") long id) {
		Optional<ReplayRecord> replayed = replayService.replay(id);
		if (replayed.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		return ResponseEntity.ok(ReplayRecordView.of(replayed.get()));
	}
}
