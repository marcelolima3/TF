package scheduler.Impl;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Interfaces.Scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class SchedulerImp implements Scheduler, CatalystSerializable {
    private LinkedList<Task> waiting_tasks;
    private LinkedList<Task> processing_tasks;

    public SchedulerImp() {
        // we should prob use Collections.synchronizedList(new LinkedList(...));
        this.waiting_tasks = new LinkedList<Task>();
        this.processing_tasks = new LinkedList<Task>();
    }

    public List<Task> getWaitingTasks() {
        return waiting_tasks;
    }

    public void setWaitingTasks(LinkedList<Task> waiting_tasks) {
        this.waiting_tasks = waiting_tasks;
    }

    public List<Task> getProcessingTasks() {
        return processing_tasks;
    }

    public void setProcessingTasks(LinkedList<Task> processing_tasks) {
        this.processing_tasks = processing_tasks;
    }

    // Add a new task to process
    @Override
    public synchronized void newTask(String url) {
        Task task = new Task(url);
        this.waiting_tasks.add(task);
    }

    // Get next task to be processed
    @Override
    public synchronized void getTask() {
        try{
            Task next_task = this.waiting_tasks.removeFirst();
            this.processing_tasks.add(next_task);
        }
        catch(NoSuchElementException exception){
            exception.getStackTrace();
        }
    }

    // End next task on processing_tasks
    @Override
    public synchronized void endTask() {
        try{
            Task task = this.processing_tasks.removeFirst();
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

        buffer.writeInt(processing_tasks.size());
        for(Task t: processing_tasks)
            serializer.writeObject(t, buffer);
    }

    @Override
    public void readObject(BufferInput<?> buffer, Serializer serializer) {
        int n_tasks = buffer.readInt();
        for(int i = 0; i < n_tasks; i++) {
            Task t = serializer.readObject(buffer);
            waiting_tasks.add(t);
        }

        n_tasks = buffer.readInt();
        for(int i = 0; i < n_tasks; i++) {
            Task t = serializer.readObject(buffer);
            processing_tasks.add(t);
        }
    }


    /*
    public static void main(String[] args){

        SchedulerImp scheduler = new SchedulerImp();

        // Adding tasks
        for(int i = 0; i < 5; i++)
            scheduler.newTask("id"+i);

        System.out.println();
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task) -> System.out.println(task.getUrl()) );

        // Processing tasks
        for(int i = 0; i < 3; i++)
            scheduler.getTask();

        System.out.println();
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task) -> System.out.println(task.getUrl()) );

        // Ending tasks
        for(int i = 0; i < 2; i++)
            scheduler.endTask();

        System.out.println("");
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task) -> System.out.println(task.getUrl()) );

        // Testing task shift whenever a client fails
        Task task_0 = scheduler.getProcessingTasks().get(0);
        scheduler.shiftTask(task_0);

        System.out.println("");
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task) -> System.out.println(task.getUrl()) );

    }*/
}
