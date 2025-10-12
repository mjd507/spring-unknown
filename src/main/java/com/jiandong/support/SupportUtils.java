package com.jiandong.support;

import java.util.concurrent.CountDownLatch;

public class SupportUtils {

	public static void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void latchAwait(CountDownLatch latch) {
		try {
			latch.await();
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
