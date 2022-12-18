package Stations;

import Operations.*;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

public class Station4 extends ProcessingStation {

    private final Random random = new Random();

    public Station4(String name) {
        super(name);
    }

    @Override
    public void process(Task task) {
        if(task instanceof Type1)
            taskProcessingTime = new NormalDistribution(20, 3).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type2)
            taskProcessingTime = new NormalDistribution(10, 1).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type3)
            taskProcessingTime = new NormalDistribution(10, 1).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type4)
            taskProcessingTime = new NormalDistribution(15, 2).inverseCumulativeProbability(random.nextDouble());
        else
            System.out.println("This operation cannot be processed by " + this.getClass().getSimpleName());
    }
}
