package Stations;

import Operations.Task;
import Operations.Type1;
import Operations.Type2;
import Operations.Type3;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.util.Random;

public class Station3 extends ProcessingStation {

    private final Random random = new Random();

    public Station3(String name) {
        super(name);
    }

    @Override
    public void process(Task task) {
        if(task instanceof Type1)
            taskProcessingTime = new NormalDistribution(75, 4).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type2)
            taskProcessingTime = new NormalDistribution(60, 5).inverseCumulativeProbability(random.nextDouble());
        else if(task instanceof Type3)
            taskProcessingTime = new NormalDistribution(50, 8).inverseCumulativeProbability(random.nextDouble());
        else
            System.out.println("This operation cannot be processed by " + this.getClass().getSimpleName());
    }
}
