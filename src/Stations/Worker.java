package Stations;

public class Worker {
    private final String name;
    private double previousTaskFinish;

    Worker(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPreviousTaskFinish(double previousTaskFinish) {
        this.previousTaskFinish = previousTaskFinish;
    }

    public double getPreviousTaskFinish() {
        return previousTaskFinish;
    }
}
