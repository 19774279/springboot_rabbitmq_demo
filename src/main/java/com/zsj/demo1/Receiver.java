package com.zsj.demo1;

import java.util.concurrent.CountDownLatch;

/**
 * Created by zhusj on 2017/3/24.
 */
//@Component
public class Receiver {
	private CountDownLatch latch = new CountDownLatch(1);

	public void receiveMessage(String message) {
		System.out.println("Received <" + message + ">");
		latch.countDown();
	}

	public CountDownLatch getLatch() {
		return latch;
	}
}
