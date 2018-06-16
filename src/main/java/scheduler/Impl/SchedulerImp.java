package scheduler.Impl;

import exceptions.RepeatedTaskException;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Interfaces.Scheduler;

import java.sql.SQLOutput;
import java.util.*;

public class SchedulerImp implements Scheduler, CatalystSerializable {

    private LinkedList<Task> waiting_tasks;
    private Map<Task, String> processing_tasks;

    public SchedulerImp() {
        this.waiting_tasks = new LinkedList<>();
        this.processing_tasks = new HashMap<>();
    }

    // Add a new task to process
    @Override
    public synchronized void newTask(String url) throws RepeatedTaskException {
        if (!waitingTasksContains(url) && !processingTasksContains(url)) {
            Task task = new Task(url);
            this.waiting_tasks.add(task);
        }
        else throw new RepeatedTaskException("Repeated url: " + url);
    }

    // Get next task to be processed
    @Override
    public synchronized Task getTask() throws NoSuchElementException {
        Task next_task = this.waiting_tasks.removeFirst();
        return next_task;
    }

    public synchronized void processTask(String client, Task task){
        this.processing_tasks.put(task, client);
    }

    // End next task on processing_tasks
    @Override
    public synchronized void endTask(Task t) throws NoSuchElementException {
        String res = processing_tasks.remove(t);
    }

    // Shift client tasks from processing to waiting
    public synchronized void shiftTasksFromClient(String client){
        for(Map.Entry<Task, String> entry : processing_tasks.entrySet()){
            if(entry.getValue().equals(client)){
                waiting_tasks.addFirst(entry.getKey());
                processing_tasks.remove(entry.getKey());
                break;
            }
        }
    }

    @Override
    public void writeObject(BufferOutput<?> buffer, Serializer serializer) {
        buffer.writeInt(waiting_tasks.size());
        for(Task t: waiting_tasks)
            serializer.writeObject(t, buffer);

        buffer.writeInt(processing_tasks.size());
        for(Map.Entry<Task, String> e: processing_tasks.entrySet()){
            serializer.writeObject(e.getKey(), buffer);
            buffer.writeString(e.getValue());
        }
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
            String client = buffer.readString();
            processing_tasks.put(t, client);
        }
    }

    public boolean waitingTasksContains(String url){
        for(Task task: this.waiting_tasks)
            if(task.getUrl().equals(url)) return true;
        return false;
    }

    public boolean processingTasksContains(String url){
        for(Map.Entry<Task, String> entry: this.processing_tasks.entrySet())
            if(entry.getKey().getUrl().equals(url)) return true;
        return false;
    }

    public void printQueue(){
        System.out.println("-------------------------");
        for(Task t : waiting_tasks){
            System.out.println("Task: " + t.getUrl());
        }
        System.out.println("-------------------------");
    }
}
