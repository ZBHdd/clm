package com.cubead.clm.io.sogou;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubead.clm.IProcessor;
import com.cubead.clm.io.service.Service;

public class Executor implements IProcessor<Object, Boolean>{
	private static final Logger log = LoggerFactory.getLogger(Service.class);
	private PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<Task>();
	private final ScheduledExecutorService executor;
	private final Boolean self;
	private volatile int thread;
	private volatile IProcessor<Object, Object> close;
	
	private class Task implements Comparable<Task> {
		private final Runnable task;
		private final Long time;
		
		public Task(Runnable task, Long time) {
			this.task = task;
			this.time = time;
		}

		@Override
		public int compareTo(Task o) {
			if (time  > o.time) return 1;
			else if (time  == o.time) return 0;
			else return -1;
		}
	}
	
	private class Runner implements Runnable {
		private Task task;
			
		public Runner(Task task) {
			this.task = task;
		}

		@Override
		public void run() {
			if (task != null) try {
				task.task.run();
			} catch (Throwable e) {
				log.error("Task perform error.", e);
			}
			while (!queue.isEmpty()) {
				Task task = queue.poll();
				if (task == null) synchronized(Executor.this) {
					if (thread-- <= 0) Executor.this.finalize();
					break;
				} else {
					Long now = System.currentTimeMillis();
					if (now < task.time) {
						this.task = task;
						executor.schedule(this, task.time - now, TimeUnit.MILLISECONDS);
						break;
					} else try {
						task.task.run();
					} catch (Throwable e) {
						log.error("Task perform error.", e);
					}
				}
			}
		}
	}
	
	public Executor(ScheduledExecutorService executor, Integer thread) {
		this.executor = executor;
		this.thread = thread == null || thread < 1 ? Runtime.getRuntime().availableProcessors() : thread;
		this.self = false;
	}

	public Executor(Integer thread) {
		this.executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
		this.thread = thread == null || thread < 1 ? Runtime.getRuntime().availableProcessors() : thread;
		self = true;
	}
	
	public Executor() {
		this.executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
		this.thread = Runtime.getRuntime().availableProcessors();
		self = true;
	}
	
	public void setClose(IProcessor<Object, Object> close) {
		this.close = close;
	}

	@Override
	public Boolean process(Object... parameter) {
		if (parameter != null && parameter.length > 0 && parameter[0] instanceof Runnable) {
			queue.offer(new Task((Runnable) parameter[0], parameter.length > 1 && parameter[1] instanceof Long ? (Long) parameter[1] : System.currentTimeMillis()));
			return true;
		} else return false;
	}
	
	public void start() {
		for (int i = 0; i < thread; i++) executor.execute(new Runner(queue.poll()));
	}
	
	@Override
	public void finalize() {
		try {
			if (self) executor.shutdown();
		} finally {
			if (close != null) synchronized(this){
				if (close != null) {
					close.process();
					close = null;
				}
			}
		}
	}
}