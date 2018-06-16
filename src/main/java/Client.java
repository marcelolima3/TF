import pt.haslab.ekit.Spread;
import scheduler.Impl.Task;
import scheduler.Interfaces.Scheduler;
import scheduler.Remote.RemoteScheduler;

import java.util.Random;

public class Client {
    public static void main(String args[]) throws Exception {
        int id = Integer.parseInt(args[0]);
        Scheduler scheduler = new RemoteScheduler(id);

        if(id == 1)
            scheduler.newTask("task_0");
        else if(id == 2){
            Task t = scheduler.getTask();
            System.out.println("URL: " + t.getUrl());
            scheduler.endTask(t);
        }
        System.out.println("My job is done.");
    }
}
