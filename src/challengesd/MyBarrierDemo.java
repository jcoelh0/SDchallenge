/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package challengesd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jcoel
 */
public class MyBarrierDemo {

	//private CyclicBarrier cyclicBarrier;
	private MyBarrier myBarrier;
	private Thread[] workers;
	private List<List<Integer>> partialResults = Collections.synchronizedList(new ArrayList<>());
	private Random random = new Random();
	private int NUM_PARTIAL_RESULTS;
	private int NUM_WORKERS;

	// logic of each of the worker threads:
	class NumberCruncherThread implements Runnable {

		@Override
		public void run() {
			String thisThreadName = Thread.currentThread().getName();
			
			List<Integer> partialResult = new ArrayList<>();

			// Crunch some numbers and store the partial result
			for (int i = 0; i < NUM_PARTIAL_RESULTS; i++) {
				Integer num = random.nextInt(10);
				System.out.println(thisThreadName
					+ ": Crunching some numbers! Final result - " + num);
				partialResult.add(num);
			}

			partialResults.add(partialResult);
			System.out.println(thisThreadName
				+ " waiting for others to reach barrier."); // ...
			
			myBarrier.block();
			
			/*try {
				// ...
				cyclicBarrier.await();
				//
			} catch (InterruptedException ex) {
				Logger.getLogger(MyBarrierDemo.class.getName()).log(Level.SEVERE, null, ex);
			} catch (BrokenBarrierException ex) {
				Logger.getLogger(MyBarrierDemo.class.getName()).log(Level.SEVERE, null, ex);
			}*/
		}
	}

	// logic that runs when the barrier has been tripped.
	class AggregatorThread implements Runnable {

		@Override
		public void run() {
			
			
			String thisThreadName = Thread.currentThread().getName();

			System.out.println(
				thisThreadName + ": Computing sum of " + NUM_WORKERS
				+ " workers, having " + NUM_PARTIAL_RESULTS + " results each.");
			int sum = 0;

			for (List<Integer> threadResult : partialResults) {
				System.out.print("Adding ");
				for (Integer partialResult : threadResult) {
					System.out.print(partialResult + " ");
					sum += partialResult;
				}
				System.out.println();
			}
			System.out.println(thisThreadName + ": Final result = " + sum);
		}
	}

	public void runSimulation(int numWorkers, int numberOfPartialResults) {
		NUM_PARTIAL_RESULTS = numberOfPartialResults;
		NUM_WORKERS = numWorkers;
		workers = new Thread[NUM_WORKERS];
		
		
		//cyclicBarrier = new CyclicBarrier(NUM_WORKERS, new AggregatorThread());
		myBarrier = new MyBarrier(NUM_WORKERS, new AggregatorThread());
		
		System.out.println("Spawning " + NUM_WORKERS
			+ " workers threads to compute "
			+ NUM_PARTIAL_RESULTS + " partial results each");

		for (int i = 0; i < NUM_WORKERS; i++) {
			Thread worker = new Thread(new NumberCruncherThread());
			worker.setName("Thread " + i);
			worker.start();
		}
	}

	public static void main(String[] args) {
		MyBarrierDemo demo = new MyBarrierDemo();
		demo.runSimulation(5000, 100);
		
	}
}
