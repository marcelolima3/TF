import pt.haslab.ekit.Spread;
import scheduler.Impl.Task;
import scheduler.Interfaces.Scheduler;
import scheduler.Remote.RemoteScheduler;

import java.util.Random;

public class Client {
    public static void main(String args[]) throws Exception {
        int id = Integer.parseInt(args[0]);
        Scheduler scheduler = new RemoteScheduler(id);

        try {
            switch (id) {
                case 1:
                    scheduler.newTask("task_0");
                    break;
                case 2:
                    Task t = scheduler.getTask();
                    System.out.println("URL: " + t.getUrl());
                    scheduler.endTask(t);
                    break;
                case 3:
                    Task t2 = scheduler.getTask();
                    System.out.println("URL: " + t2.getUrl());
                    break;
                default:
                    break;
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println("My job is done.");
    }
}
