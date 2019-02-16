import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Lesson5Concurrent {
	
	private static final String numThreadsArg = "--num-threads";
	private static final String lockArg = "--ReentrantLock";
	private static final String longArg = "--AtomicLong";
	
	private static boolean lockBool = false;
	private static boolean longBool = false;
	
	public static long count = 0;
	public static AtomicLong countAtom = new AtomicLong(0);
	public static Lock lock = new ReentrantLock();
	

	public static void main(String[] args) {
		//Lazy way of determining which method to use.
		int numThreads = 1;
		for(int x = 0; x < args.length; x++) {
			if(args[x].equals(numThreadsArg)) {
				numThreads = Integer.parseInt(args[++x]);
			}
			if(args[x].contentEquals(lockArg)) {
				lockBool = true;
			}
			if(args[x].contentEquals(longArg)) {
				longBool = true;
			}
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		executor.execute(new defaultCountClass());
		executor.execute(new defaultCountJava());
		
		//Shutdown the executor gracefully
		try {
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("Error executing threads");
		}
		finally {
			executor.shutdownNow();
		}
		
		//Display Results
		if(longBool) {
			System.out.println(countAtom.get());
		}
		else {
			System.out.println(count);
		}
		
	}
	
	//Count .class files
	static class defaultCountClass implements Runnable  {
		public void run() {
			try {
				File[] classFiles = new File("bin").listFiles();
				for(File file : classFiles) {
					Scanner in = new Scanner(file);
					while(in.hasNext()) {
						String line = in.nextLine();
						
						if(lockBool) {
							lock.lock();
						}
						if(longBool) {
							countAtom.addAndGet(line.length());
						} else {
							count += line.length();
						}
						if(lockBool) {
							lock.unlock();
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Bin Files could not be found.");
			}
		}
	}
	
	//Count .java files
	static class defaultCountJava implements Runnable  {
		public void run() {
			try {
				File[] javaFiles = new File("src").listFiles();
				for(File file : javaFiles) {
					Scanner in = new Scanner(file);
					while(in.hasNext()) {
						String line = in.nextLine();
						
						if(lockBool) {
							lock.lock();
						}
						if(longBool) {
							countAtom.addAndGet(line.length());
						} else {
							count += line.length();
						}
						if(lockBool) {
							lock.unlock();
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Src Files could not be found.");
			}
		}
	}

}
