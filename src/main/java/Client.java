import pt.haslab.ekit.Spread;
import scheduler.Interfaces.Scheduler;
import scheduler.Remote.RemoteScheduler;

import java.util.Random;

public class Client {
    public static void main(String args[]) throws Exception {
        int id = Integer.parseInt(args[0]);
        Scheduler scheduler = new RemoteScheduler(id);
        Spread s = new Spread("usr"+id, false);

        // do something

    }
}
