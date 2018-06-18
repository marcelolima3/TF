import exceptions.RepeatedTaskException;
import menu.Menu;
import pt.haslab.ekit.Spread;
import scheduler.Impl.Task;
import scheduler.Interfaces.Scheduler;
import scheduler.Remote.RemoteScheduler;

import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Client_UI {
    public static void main(String args[]) throws Exception {

        int id = Integer.parseInt(args[0]);
        Menu main_menu = new Menu(new String[]{"New task", "Get Task", "Sair"});
        Menu task_menu = new Menu(new String[]{"End task"});
        Scheduler scheduler = new RemoteScheduler(id);

        showMainMenu(main_menu, task_menu, scheduler);

        System.out.println("Good bye!");
        System.exit(0);
    }

    private static void showMainMenu(Menu main_menu, Menu task_menu, Scheduler scheduler){
        int running = 1;
        do {
            main_menu.executa();
            switch(main_menu.getOpcao()) {
                case 1:
                    newTask(scheduler);
                    break;
                case 2:
                    getTask(scheduler, task_menu);
                    break;
                case 3:
                    running = 0;
                case 0:
                    running = 0;
            }
        }
        while(running == 1);
    }

    private static void showTaskMenu(Menu task_menu, Scheduler scheduler, Task task){
        int running = 1;
        do {
            task_menu.executa();
            switch(task_menu.getOpcao()){
                case 1: endTask(scheduler, task);
                        running = 0;
                case 0: running = 0;
            }
        }
        while(running == 1);
    }

    private static void newTask(Scheduler scheduler){
        Scanner in = new Scanner(System.in);
        System.out.print("Task url: ");
        String task_url = in.nextLine();
        System.out.println("New task: " + task_url);
        try {
            scheduler.newTask(task_url);
        } catch (RepeatedTaskException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void getTask(Scheduler scheduler, Menu task_menu){
        try {
            Task task = scheduler.getTask();
            System.out.println("URL: " + task.getUrl());
            showTaskMenu(task_menu, scheduler, task);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static void endTask(Scheduler scheduler, Task task){
        System.out.println("Processing task...");
        try {
            task.run();
            System.out.println("Task done.");
            scheduler.endTask(task);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
