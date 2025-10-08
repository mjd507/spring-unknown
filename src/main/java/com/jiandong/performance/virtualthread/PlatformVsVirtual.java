package com.jiandong.performance.virtualthread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.jiandong.support.ThirtyPartyService;

import org.springframework.util.StopWatch;

public class PlatformVsVirtual {

	private final ThirtyPartyService thirtyPartyService;

	Executor platFormThreadExecutor = Executors.newFixedThreadPool(2);

	Executor virtualThreadPerTaskExecutor = Executors.newVirtualThreadPerTaskExecutor();

	public PlatformVsVirtual(ThirtyPartyService thirtyPartyService) {
		this.thirtyPartyService = thirtyPartyService;
	}

	public double platformExecutionTime(int taskCount) {
		return calcExecutionTime(platFormThreadExecutor, executor -> task(executor, taskCount));
	}

	public double virtualExecutionTime(int taskCount) {
		return calcExecutionTime(virtualThreadPerTaskExecutor, executor -> task(executor, taskCount));
	}

	private double calcExecutionTime(Executor executor, Consumer<Executor> executorConsumer) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		executorConsumer.accept(executor);
		stopWatch.stop();
		return stopWatch.getTotalTimeSeconds();
	}

	private void task(Executor taskExecutor, int taskCount) {
		CountDownLatch latch = new CountDownLatch(taskCount);
		for (int i = 0; i < taskCount; i++) {
			taskExecutor.execute(() -> {
				thirtyPartyService.slowMethod();
				latch.countDown();
			});
		}
		try {
			latch.await();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
