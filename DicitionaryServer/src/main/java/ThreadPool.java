/**
 * This file has created by
 * Author: Kaixin JI
 * Student ID: 1112259
 */

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class ThreadPool {
    private ArrayBlockingQueue<Task> threadPool;
    private HashMap<String, String> resultList;

    public ThreadPool(int poolSize) {
        this.threadPool = new ArrayBlockingQueue<Task>(poolSize);
        this.resultList = new HashMap<>(poolSize);
    }

    public HashMap<String, String> getResultList() {
        return resultList;
    }

    public void execute(Task task){
        allocate(task);

        synchronized (threadPool){
            while (!threadPool.isEmpty()){
                task = threadPool.poll();
                try{
                    task.run();
                    System.out.println("Running result: " + task.getResult());
                    resultList.put(task.getClientPort(), task.getResult());
                }catch (RuntimeException e) {
                    System.out.println("Thread pool is interrupted due to an issue: " + e.getMessage());
                    System.exit(0);
                }
            }
        }
    }

    public void shutdown(){
        System.out.println("Shutting down thread pool");
        threadPool.clear();
        resultList.clear();
    }

    private void allocate(Task task){
        synchronized (threadPool){
            threadPool.add(task);
            threadPool.notify();
        }
    }

}
