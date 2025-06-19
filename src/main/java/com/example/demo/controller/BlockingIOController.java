package com.example.demo.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.BlockingIoService;

@RestController
@RequestMapping("/api/blocking-io")
public class BlockingIOController {

	private static final Logger logger = LoggerFactory.getLogger(BlockingIOController.class);

	@Autowired
	private BlockingIoService blockingIoService;

	@GetMapping("/external-api/{requestId}")
	public Map<String, Object> externalApiCall(@PathVariable String requestId) {
		String currentThread = Thread.currentThread().getName();
		boolean isVirtual = Thread.currentThread().isVirtual();

		logger.info("🚀 [EXTERNAL-API] Starting request: {} | Thread: {} | Virtual: {}", requestId, currentThread,
				isVirtual);

		long startTime = System.currentTimeMillis();

		try {
			Map<String, Object> result = blockingIoService.simulateExternalApiCall(requestId);
			long totalTime = System.currentTimeMillis() - startTime;

			logger.info("✅ [EXTERNAL-API] Completed request: {} | Thread: {} | Duration: {}ms | Virtual: {}", requestId,
					currentThread, totalTime, isVirtual);

			return result;

		} catch (Exception e) {
			long totalTime = System.currentTimeMillis() - startTime;
			logger.error("❌ [EXTERNAL-API] Failed request: {} | Thread: {} | Duration: {}ms | Error: {}", requestId,
					currentThread, totalTime, e.getMessage());
			throw e;
		}
	}

	@GetMapping("/external-api-multiple/{requestId}")
	public Map<String, Object> multipleExternalApiCalls(@PathVariable String requestId,
			@RequestParam(defaultValue = "3") int count) {
		String currentThread = Thread.currentThread().getName();
		boolean isVirtual = Thread.currentThread().isVirtual();

		logger.info("🚀 [MULTIPLE-API] Starting {} API calls for request: {} | Thread: {} | Virtual: {}", count,
				requestId, currentThread, isVirtual);

		long startTime = System.currentTimeMillis();

		try {
			Map<String, Object> result = blockingIoService.simulateMultipleApiCalls(requestId, count);
			long totalTime = System.currentTimeMillis() - startTime;

			logger.info(
					"✅ [MULTIPLE-API] Completed {} API calls for request: {} | Thread: {} | Duration: {}ms | Virtual: {}",
					count, requestId, currentThread, totalTime, isVirtual);

			return result;

		} catch (Exception e) {
			long totalTime = System.currentTimeMillis() - startTime;
			logger.error(
					"❌ [MULTIPLE-API] Failed {} API calls for request: {} | Thread: {} | Duration: {}ms | Error: {}",
					count, requestId, currentThread, totalTime, e.getMessage());
			throw e;
		}
	}

	@PostMapping("/database/insert")
	public Map<String, Object> slowDatabaseInsert(@RequestParam String name, @RequestParam String value) {
		String currentThread = Thread.currentThread().getName();
		boolean isVirtual = Thread.currentThread().isVirtual();

		logger.info("🚀 [DB-INSERT] Starting database insert: name={}, value={} | Thread: {} | Virtual: {}", name,
				value, currentThread, isVirtual);

		long startTime = System.currentTimeMillis();

		try {
			Map<String, Object> result = blockingIoService.slowDatabaseInsert(name, value);
			long totalTime = System.currentTimeMillis() - startTime;

			logger.info("✅ [DB-INSERT] Completed database insert: name={} | Thread: {} | Duration: {}ms | Virtual: {}",
					name, currentThread, totalTime, isVirtual);

			return result;

		} catch (Exception e) {
			long totalTime = System.currentTimeMillis() - startTime;
			logger.error("❌ [DB-INSERT] Failed database insert: name={} | Thread: {} | Duration: {}ms | Error: {}",
					name, currentThread, totalTime, e.getMessage());
			throw e;
		}
	}

	@PostMapping("/file/write")
	public Map<String, Object> fileWriteOperation(@RequestParam String filename, @RequestParam String content) {
		String currentThread = Thread.currentThread().getName();
		boolean isVirtual = Thread.currentThread().isVirtual();

		logger.info("🚀 [FILE-WRITE] Starting file write: filename={}, content length={} | Thread: {} | Virtual: {}",
				filename, content.length(), currentThread, isVirtual);

		long startTime = System.currentTimeMillis();

		try {
			Map<String, Object> result = blockingIoService.slowFileWrite(filename, content);
			long totalTime = System.currentTimeMillis() - startTime;

			logger.info("✅ [FILE-WRITE] Completed file write: filename={} | Thread: {} | Duration: {}ms | Virtual: {}",
					filename, currentThread, totalTime, isVirtual);

			return result;

		} catch (Exception e) {
			long totalTime = System.currentTimeMillis() - startTime;
			logger.error("❌ [FILE-WRITE] Failed file write: filename={} | Thread: {} | Duration: {}ms | Error: {}",
					filename, currentThread, totalTime, e.getMessage());
			throw e;
		}
	}

	@PostMapping("/combined")
	public Map<String, Object> combinedOperations(@RequestParam String requestId, @RequestParam String name,
			@RequestParam String filename) {
		String currentThread = Thread.currentThread().getName();
		boolean isVirtual = Thread.currentThread().isVirtual();

		logger.info(
				"🚀 [COMBINED] Starting combined operations: requestId={}, name={}, filename={} | Thread: {} | Virtual: {}",
				requestId, name, filename, currentThread, isVirtual);

		long startTime = System.currentTimeMillis();

		try {
			Map<String, Object> result = blockingIoService.combinedOperations(requestId, name, filename);
			long totalTime = System.currentTimeMillis() - startTime;

			logger.info(
					"✅ [COMBINED] Completed combined operations: requestId={} | Thread: {} | Duration: {}ms | Virtual: {}",
					requestId, currentThread, totalTime, isVirtual);

			return result;

		} catch (Exception e) {
			long totalTime = System.currentTimeMillis() - startTime;
			logger.error(
					"❌ [COMBINED] Failed combined operations: requestId={} | Thread: {} | Duration: {}ms | Error: {}",
					requestId, currentThread, totalTime, e.getMessage());
			throw e;
		}
	}
}