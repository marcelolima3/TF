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

    @Override
    public void writeObject(BufferOutput<?> buffer, Serializer serializer) {

    }

    @Override
    public void readObject(BufferInput<?> buffer, Serializer serializer) {

    }

    public static void main(String[] args){

        SchedulerImp scheduler = new SchedulerImp();

        for(int i = 0; i < 5; i++)
            scheduler.newTask("id"+i);

        System.out.println();
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task) -> System.out.println(task.getUrl()) );

        for(int i = 0; i < 3; i++)
            scheduler.getTask();

        System.out.println();
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task) -> System.out.println(task.getUrl()) );

        for(int i = 0; i < 2; i++)
            scheduler.endTask();

        System.out.println("");
        System.out.println("Waiting tasks");
        scheduler.getWaitingTasks().forEach( (task) -> System.out.println(task.getUrl()) );
        System.out.println("Processing tasks");
        scheduler.getProcessingTasks().forEach( (task) -> System.out.println(task.getUrl()) );

    }
}
