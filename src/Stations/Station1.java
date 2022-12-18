package Stations;

import Operations.Task;
import Operations.Type1;
import Operations.Type2;
import Operations.Type4;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

public class Station1 extends ProcessingStation {

    private final Random random = new Random();

    public Station1(String name) {
        super(name);
    }

    @Override
    public void process(Task task) {
        if(task instanceof Type1)
            taskProcessingTime = new NormalDistribution(20, 3).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type2)
            taskProcessingTime = new NormalDistribution(18, 2).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type4)
            taskProcessingTime = new NormalDistribution(30, 5).inverseCumulativeProbability(random.nextDouble());
        else
            System.out.println("This operation cannot be processed by " + this.getClass().getSimpleName());
    }
}
