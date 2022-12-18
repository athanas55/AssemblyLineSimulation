package Operations;

import Stations.ProcessingStation;
import Stations.Worker;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task implements Runnable {

    protected final double arrivalTime;
    protected final int index;
    protected final UUID id;

    protected final ProcessingStation[] stations;
    protected final AtomicInteger finished;
    private static boolean isDone = false;
    protected double processingTime;

    protected Task(int index,
                   UUID id,
                   double arrival,
                   ProcessingStation[] stations,
                   AtomicInteger completed) {
        this.index = index;
        this.id = id;
        this.arrivalTime = arrival;
        this.stations = stations;
        this.finished = completed;
    }

    public void run() {
        if(this instanceof Type1) {

            ProcessingStation[] processingStations = {stations[0], stations[1], stations[2], stations[3]};
            processingTime = processTask(arrivalTime, processingStations, finished) - arrivalTime;
        }
        else if (this instanceof Type2) {

            ProcessingStation[] processingStations =  {stations[0], stations[2], stations[3]};
            processingTime = processTask(arrivalTime, processingStations, finished) - arrivalTime;
        }
        else if (this instanceof Type3) {

            ProcessingStation[] processingStations =  {stations[1], stations[3], stations[2]};
            processingTime = processTask(arrivalTime, processingStations, finished) - arrivalTime;
        }
        else {

            ProcessingStation[] processingStations =  {stations[0], stations[3]};
            processingTime = processTask(arrivalTime, processingStations, finished) - arrivalTime;
        }
    }

    public UUID getID() { return id; }

    public int getIndex() { return index; }

    public double getProcessingTime() {
        return processingTime;
    }

    public double processTask(double arrivalTime, ProcessingStation[] stations, AtomicInteger completed) {
        double currentTimeStamp = arrivalTime;
        for (ProcessingStation station : stations) {
            try {
                Worker worker = station.getAllWorkers().take();
                station.process(this);
                System.out.printf("%s%d%s%s%n", "Task #", index, " started being processed at ", station.getName());
                double time = station.getTaskProcessingTime();
                if (worker.getPreviousTaskFinish() > arrivalTime)
                    currentTimeStamp = worker.getPreviousTaskFinish() + time;
                else
                    currentTimeStamp += time;
                worker.setPreviousTaskFinish(currentTimeStamp);
                station.getAllWorkers().put(worker);
                if (currentTimeStamp < 200) {
                    System.out.println("Task #" + index + " is being processed during system training period");
                } else if (currentTimeStamp > 1000.0) {
                    System.out.println("Timeout reached. Task#" + index + " didn't complete processing.");

                } else {
                    System.out.printf("%s%d%s%s%s%.2f%n", "Task #", index, " left ", station.getName(), " at ", currentTimeStamp);
                    station.incrementProcessingTime(time);
                    station.addCompletedTasks(this);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(currentTimeStamp > 200 && currentTimeStamp <1001) {
            System.out.printf("%s%d%s%.2f%n", "Task #", index, " FINISHED at ", currentTimeStamp);
            isDone = true;
            completed.incrementAndGet();
        }
        return currentTimeStamp;
    }

    public boolean isFinished() {
        return isDone;
    }
}

