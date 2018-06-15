import pt.haslab.ekit.Spread;
import scheduler.Impl.Task;
import scheduler.Interfaces.Scheduler;
import scheduler.Remote.RemoteScheduler;

import java.util.Random;

public class Client {
    public static void main(String args[]) throws Exception {
        int id = Integer.parseInt(args[0]);
        Scheduler scheduler = new RemoteScheduler(id);
        scheduler.newTask("task_0");
        Task task = scheduler.getTask();
        task.run();
        scheduler.endTask(task);
    }
}
