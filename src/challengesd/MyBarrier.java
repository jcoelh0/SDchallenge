package challengesd;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author jcoel
 */
public class MyBarrier {
	
	private final int number;
	private final Runnable barrier;
	private int count;
	private AtomicInteger counter = new AtomicInteger();
	private Thread[] threads;
	
	
	
	public MyBarrier(int number, Runnable barrier) {
		if (number <= 0) {
			throw new IllegalArgumentException();
		}
		this.number = number;
		counter.set(number);
		count = number;
		this.barrier = barrier;
		threads = new Thread[number];
	}

	
	public void block(Object blocker){
		threads[counter.decrementAndGet()] = Thread.currentThread();
		//threads[--count] = Thread.currentThread();
		//if(count==0){
		if(counter.get()==0){
			UnblockAll();
			return;
		}
		if(blocker==null)
			LockSupport.park(Thread.currentThread());
		else
			LockSupport.park(blocker);
	}
	
	public void block(){
		this.block(null);
	}
	
	private void UnblockAll() {
		final Runnable command = barrier;
		if (command != null) {
			command.run();
		}
		for (int i = 0; i < number; i++) {
			LockSupport.unpark(threads[i]);
		}
		
	}
}
