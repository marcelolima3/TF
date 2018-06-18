import exceptions.RepeatedTaskException;
import menu.Menu;
import scheduler.Impl.Task;
import scheduler.Interfaces.Scheduler;
import scheduler.Remote.RemoteScheduler;

import java.util.Scanner;

public class Client_Simple {
    public static void main(String args[]) throws Exception {
        int id = Integer.parseInt(args[0]);
        Scheduler scheduler = new RemoteScheduler(id);

        try {
            switch (args[1]) {
                case "new":
                    System.out.println("New task: " + args[2]);
                    scheduler.newTask(args[2]);
                    break;
                case "get":
                    Task t = scheduler.getTask();
                    System.out.println("URL: " + t.getUrl());
                    t.run();
                    System.out.println("Task done.");
                    scheduler.endTask(t);
                    break;
                case "end":
                    Task t2 = scheduler.getTask();
                    System.out.println("URL: " + t2.getUrl());
                    break;
                default:
                    break;
            }
        }
        catch (Exception e){ System.out.println(e.getMessage()); }

    }
}
