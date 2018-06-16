/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Interfaces;

import exceptions.RepeatedTaskException;
import scheduler.Impl.Task;

import java.util.NoSuchElementException;


public interface Scheduler {
    // Create a new task
    void newTask(String url) throws RepeatedTaskException;
    // Get next task to be processed
    Task getTask() throws NoSuchElementException;
    // End a task
    void endTask(Task t) throws NoSuchElementException;
}
