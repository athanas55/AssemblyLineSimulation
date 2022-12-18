
import Operations.*;
import Stations.*;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulate {
    private static final double RATE = 2.0 / 8.0;
    private static final int SIM_TIME = 1000;
    private static final int TRANS_PHASE = 200;
    private static final ArrayList<Task> allTasks = new ArrayList<>();
    private static final SplittableRandom random = new SplittableRandom();
    private static final AtomicInteger fullyProcessedTasks = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        // create processing stations
        ProcessingStation station1 = new Station1("Station1");
        ProcessingStation station2 = new Station2("Station2");
        ProcessingStation station3 = new Station3("Station3");
        ProcessingStation station4 = new Station4("Station4");
        // add workers
        station1.addWorkers(8);
        station2.addWorkers(8);
        station3.addWorkers(20);
        station4.addWorkers(7);

        ProcessingStation[] allStations = new ProcessingStation[]{station1, station2, station3, station4};

        ExecutorService executor = Executors.newCachedThreadPool();

        /*
        | start accepting tasks based on the provided distribution
        | http://www.columbia.edu/~ks20/stochastic-I/stochastic-I-PP.pdf
        | Definition 1.2 A Poisson process at rate 0 < λ < ∞ is a renewal point process
        | in which the inter-arrival time distribution is exponential with rate λ:
        ---------------------------------------------------------------------------
        | Ρουμελιώτης: Η εκθετική κατανομή είναι η κατανομή που ακολουθούν
        | οι χρόνοι ανάμεσα σε τυχαίες αφίξεις ενός άπειρου πληθυσμού.
        | Αντίστροφος μετασχηματισμός εκθετικής κατανομής : x = -ln(1 - U)/λ
        */
        double clock = 0;
        int taskNum = 0;
        while (clock <= SIM_TIME) {
            double interArrival = - Math.log(1 - random.nextDouble()) / RATE;
            if((clock + interArrival) < SIM_TIME) {
                clock += interArrival;
                taskNum += 1;
                Task task = newTask(taskNum, clock, allStations, fullyProcessedTasks);
                System.out.printf("%s%d%s%s%s%s%s%.2f%n","Task #", task.getIndex(),
                        " ID: ", task.getID(),
                        " Type: ", task.getClass().getSimpleName(),
                        " arrived in queue at ", clock);
                executor.execute(task);
                allTasks.add(task);
            }
            else {
                executor.shutdown();
                break;
            }
        }
        try {
            if(!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }catch (InterruptedException e) {
            executor.shutdownNow();
        }

        printResults(allStations);
    }

    public static Task newTask(int index,
                               double arrival,
                               ProcessingStation[] stations,
                               AtomicInteger completed) {
        int[] types = {1, 2, 3, 4};
        double[] p = {0.4, 0.3, 0.2, 0.1};

        EnumeratedIntegerDistribution dist = new EnumeratedIntegerDistribution(types, p);
        UUID id = UUID.randomUUID();
        int type = dist.sample();
        if(type == 1)
            return new Type1(index, id, arrival, stations, completed);
        else if (type == 2)
            return new Type2(index, id, arrival, stations, completed);
        else if (type == 3)
            return new Type3(index, id, arrival, stations, completed);
        else
            return new Type4(index, id, arrival, stations, completed);
    }

    public static double calcAverageTimePerStation (ProcessingStation station) {
        return station.getTotalProcessingTime()/station.getCompletedTasks().size();
    }

    public static double calcAvgWorkerPerformance(ProcessingStation station) {
        return (station.getCompletedTasks().size() / (double) (SIM_TIME - TRANS_PHASE)) / station.getStationWorkers().size();
    }

    public static double calcAvgStationPerf(ProcessingStation station) {
        return station.getCompletedTasks().size() / (double) (SIM_TIME - TRANS_PHASE);
    }

    public static void printResults(ProcessingStation[] allStations ) {
        System.out.println("\n\n************************* Statistics *************************");

        System.out.println("\nTasks processed in each station.");
        for(ProcessingStation station: allStations)
            System.out.println("\t" + station.getName() +
                    ": " + station.getCompletedTasks().size());

        System.out.println("\nFully completed tasks in assembly line (normal phase): " + fullyProcessedTasks);

        System.out.println("Average processing time per station:");
        for(ProcessingStation station: allStations)
            System.out.printf("%s%s%.2f%n", "\t" + station.getName(),
                    ": ", calcAverageTimePerStation(station));

        System.out.println("\nAverage worker performance per station:");
        for(ProcessingStation station: allStations)
            System.out.printf("%s%s%.4f%n", "\t" + station.getName(),
                    ": ", calcAvgWorkerPerformance(station));

        System.out.println("\nAverage station performance:");
        for(ProcessingStation station: allStations)
            System.out.printf("%s%s%.4f%n", "\t" + station.getName(),
                    ": ", calcAvgStationPerf(station));

        double totalTaskPTime = 0.0;
        int type1Finished = 0, type2Finished = 0,
                type3Finished = 0, type4Finished = 0;
        double type1totalTime = 0, type2totalTime = 0,
                type3totalTime = 0, type4totalTime = 0;
        for(Task task: allTasks)
            if(task.isFinished()) {
                totalTaskPTime += task.getProcessingTime();
                if (task instanceof Type1) {
                    type1Finished++;
                    type1totalTime += task.getProcessingTime();
                } else if (task instanceof Type2) {
                    type2Finished++;
                    type2totalTime += task.getProcessingTime();
                } else if (task instanceof Type3) {
                    type3Finished++;
                    type3totalTime += task.getProcessingTime();
                }
                else {
                    type4Finished++;
                    type4totalTime += task.getProcessingTime();
                }
            }

        System.out.printf("%n%s%n%s%.2f%n%s%.2f%n%s%.2f%n%s%.2f%n",
                "Average processing time per task type: ",
                "\tType 1: ", type1totalTime / type1Finished,
                "\tType 2: ", type2totalTime / type2Finished,
                "\tType 3: ", type3totalTime / type3Finished,
                "\tType 4: ", type4totalTime / type4Finished);

        System.out.printf("%n%s%.2f%n", "Average task processing time: ",
                totalTaskPTime / fullyProcessedTasks.get());
    }
}
