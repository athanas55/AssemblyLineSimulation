package Operations;

import Stations.ProcessingStation;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Type2 extends Task {

    public Type2(int index,
                 UUID id,
                 double arrival,
                 ProcessingStation[] stations,
                 AtomicInteger completed) {
        super(index,  id, arrival, stations, completed);
    }
}
