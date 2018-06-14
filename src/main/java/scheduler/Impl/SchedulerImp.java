package scheduler.Impl;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Interfaces.Scheduler;

import java.util.*;

public class SchedulerImp implements Scheduler, CatalystSerializable {

    private LinkedList<Task> waiting_tasks;
    private Map<Task,String> processing_tasks;

    public SchedulerImp() {
        // we should prob use Collections.synchronizedList(new LinkedList(...));
        this.waiting_tasks = new LinkedList<Task>();
        this.processing_tasks = new HashMap<Task,String>();
    }

    // Add a new task to process
    @Override
    public synchronized void newTask(String url) {
        Task task = new Task(url);
        this.waiting_tasks.add(task);
    }

    // Get next task to be processed
    @Override
    public synchronized Task getTask(String client_id) {
        try{
            Task next_task = this.waiting_tasks.removeFirst();
            this.processing_tasks.put(next_task, client_id);
            return next_task;
        }
        catch(NoSuchElementException exception){
            exception.getStackTrace();
        }
        return null;
    }

    // End next task on processing_tasks
    @Override
    public synchronized void endTask(Task t) {

        try{
            processing_tasks.remove(t);
        }
        catch(NoSuchElementException exception){
            exception.getStackTrace();
        }
    }

    // Shift task from processing to waiting (if client fails)
    public synchronized void shiftTask(Task task){
        this.processing_tasks.remove(task);
        this.waiting_tasks.addFirst(task);
    }

    @Override
    public void writeObject(BufferOutput<?> buffer, Serializer serializer) {
        buffer.writeInt(waiting_tasks.size());
        for(Task t: waiting_tasks)
            serializer.writeObject(t, buffer);

        //buffer.writeInt(processing_tasks.size());
        //for(Task t: processing_tasks)
        //    serializer.writeObject(t, buffer);
    }

    @Override
    public void readObject(BufferInput<?> buffer, Serializer serializer) {
        int n_tasks = buffer.readInt();
        for(int i = 0; i < n_tasks; i++) {
            Task t = serializer.readObject(buffer);
            waiting_tasks.add(t);
        }

        n_tasks = buffer.readInt();
        //for(int i = 0; i < n_tasks; i++) {
        //    Task t = serializer.readObject(buffer);
        //    processing_tasks.add(t);
        //}
    }

    public List<Task> getWaitingTasks() {
        return waiting_tasks;
    }

    public void setWaitingTasks(LinkedList<Task> waiting_tasks) {
        this.waiting_tasks = waiting_tasks;
    }

    public Map<Task, String> getProcessingTasks() {
        return processing_tasks;
    }

    public void setProcessingTasks(Map<Task, String> processing_tasks) {
        this.processing_tasks = processing_tasks;
    }


    public static void main(String[] args){

        SchedulerImp scheduler = new SchedulerImp();
        Task task1 = null;

        // Adding tasks
        for(int i = 0; i < 5; i++)
            scheduler.newTask("id"+i);

        System.out.println();
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task, client) -> System.out.println(task.getUrl()) );

        // Processing tasks
        for(int i = 0; i < 3; i++)
            task1 = scheduler.getTask("client_1");

        System.out.println();
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task, client) -> System.out.println(task.getUrl()) );

        // Ending tasks
        scheduler.endTask(task1);

        System.out.println("");
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task, client) -> System.out.println(task.getUrl()) );

        // Testing task shift whenever a client fails
        Map.Entry<Task,String> entry = scheduler.getProcessingTasks().entrySet().iterator().next();
        Task task2 = entry.getKey();
        scheduler.shiftTask(task2);

        System.out.println("");
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task, client) -> System.out.println(task.getUrl()) );

    }
}
