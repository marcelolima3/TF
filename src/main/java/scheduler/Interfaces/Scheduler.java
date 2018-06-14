/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Interfaces;

import scheduler.Impl.Task;

public interface Scheduler {
    // Create a new task
    boolean newTask(String url);
    // Get next task to be processed
    Task getTask(String client_id);
    // End a task
    boolean endTask(Task t);
}
