/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Interfaces;

import scheduler.Impl.Task;

import java.util.LinkedList;
import java.util.List;

public interface Scheduler {
    public void newTask(String url);
    public Task getTask();
    public void endTask(Task t);
}
