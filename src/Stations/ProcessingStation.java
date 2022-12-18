package Stations;

import Operations.Task;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ProcessingStation {

    protected final String name;
    protected BlockingQueue<Worker> allWorkers = new LinkedBlockingQueue<>();
    protected ArrayList<Task> completedTasks = new ArrayList<>();

    ArrayList<Worker> stationWorkers = new ArrayList<>();
    protected double taskProcessingTime;

    private static double totalProcessingTime;


    public abstract void process(Task task);

    protected ProcessingStation(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void addCompletedTasks(Task task) {
        completedTasks.add(task);
    }

    public ArrayList<Task> getCompletedTasks() {
        return completedTasks;
    }

    public void incrementProcessingTime(double time) {
        totalProcessingTime += time;
    }

    public double getTaskProcessingTime() {
        return taskProcessingTime;
    }

    public double getTotalProcessingTime() {
        return totalProcessingTime;
    }

    public ArrayList<Worker> getStationWorkers() {
        return stationWorkers;
    }

    public BlockingQueue<Worker> getAllWorkers() {
        return allWorkers;
    }

    public void addWorkers(int workers) throws InterruptedException {
        for(int i = 0; i < workers; i++) {
            String name = this.name + ".worker_" + (i + 1);
            Worker worker = new Worker(name);
            allWorkers.put(worker);
            stationWorkers.add(worker);
        }
    }
}
