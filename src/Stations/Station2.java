package Stations;

import Operations.Task;
import Operations.Type1;
import Operations.Type3;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

public class Station2 extends ProcessingStation {

    private final Random random = new Random();

    public Station2(String name) {
        super(name);
    }

    @Override
    public void process(Task task) {
        if(task instanceof Type1)
            taskProcessingTime = new NormalDistribution(30, 5).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type3)
            taskProcessingTime = new NormalDistribution(20, 2).inverseCumulativeProbability(random.nextDouble());
        else
            System.out.println("This operation cannot be processed by " + this.getClass().getSimpleName());
    }
}
