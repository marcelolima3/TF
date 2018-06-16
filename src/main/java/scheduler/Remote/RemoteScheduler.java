package scheduler.Remote;

import exceptions.RepeatedTaskException;
import scheduler.Req.ClientFailure;
import scheduler.Impl.Task;
import scheduler.Interfaces.Scheduler;
import scheduler.Rep.EndTaskRep;
import scheduler.Rep.GetTaskRep;
import scheduler.Rep.NewTaskRep;
import scheduler.Req.EndTaskReq;
import scheduler.Req.GetTaskReq;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import pt.haslab.ekit.Spread;
import scheduler.Req.NewTaskReq;
import spread.MembershipInfo;
import spread.SpreadMessage;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


public class RemoteScheduler implements Scheduler {
    private final ThreadContext tc;
    private final Spread s;
    private final String client_group;
    private final String server_group;
    private AtomicInteger req_id;
    private CompletableFuture cf;
    private int id;

    public RemoteScheduler(int id) throws Exception {
        this.id = id;
        tc = new SingleThreadContext("srv-%d", new Serializer());
        s = new Spread("user-" + this.id, true);
        this.server_group = "servers";
        this.client_group = "users";
        req_id = new AtomicInteger(0);

        registerMsg();
        registerHandlers();
    }

    private void registerHandlers() throws ExecutionException, InterruptedException {
        tc.execute(() -> {
            s.handler(MembershipInfo.class, (sender, msg) -> {
                if(msg.isCausedByDisconnect()){
                    System.out.println("Client failure - " + msg.getDisconnected().toString());
                    broadcast(server_group, new ClientFailure(msg.getDisconnected().toString()));
                }
                else if(msg.isCausedByLeave()){
                    System.out.println("Client failure - " + msg.getLeft().toString());
                    broadcast(server_group, new ClientFailure(msg.getLeft().toString()));
                }
            });
            s.handler(GetTaskRep.class, (sender, msg) -> {
                if(msg.id == req_id.intValue() && cf!=null){
                    System.out.println("GetTask received");
                    cf.complete(msg);
                }
            });
            s.handler(NewTaskRep.class, (sender, msg) -> {
                if(msg.id == req_id.intValue() && cf!=null){
                    System.out.println("NewTask received");
                    cf.complete(msg);
                }
            });
            s.handler(EndTaskRep.class, (sender, msg) -> {
                if(msg.id == req_id.intValue() && cf!=null) {
                    System.out.println("EndTask received");
                    cf.complete(msg);
                }
            });
            try {
                s.open().thenRun(() -> {
                    System.out.println("Starting...");
                    s.join(this.client_group);
                }).get();
            }
            catch (Exception e) { e.printStackTrace(); }
        }).get();
    }

    public void broadcast(String group, Object msg){
        SpreadMessage sm = new SpreadMessage();
        sm.addGroup(group);
        sm.setAgreed();
        this.s.multicast(sm, msg);
    }

    private void registerMsg(){
        tc.serializer().register(GetTaskRep.class);
        tc.serializer().register(GetTaskReq.class);
        tc.serializer().register(NewTaskRep.class);
        tc.serializer().register(NewTaskReq.class);
        tc.serializer().register(EndTaskRep.class);
        tc.serializer().register(EndTaskReq.class);
        tc.serializer().register(Task.class);
        tc.serializer().register(ClientFailure.class);
    }


    @Override
    public void newTask(String url) throws RepeatedTaskException{
        try {
            cf = new CompletableFuture();
            int id_req = req_id.incrementAndGet();
            broadcast(this.server_group, new NewTaskReq(id_req, url));
            NewTaskRep ntr = (NewTaskRep) cf.get();

            if(!ntr.res)
                throw new RepeatedTaskException("Repeated task" + url +".");
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public Task getTask() throws NoSuchElementException{
        try {
            cf = new CompletableFuture();
            int id_req = req_id.incrementAndGet();
            broadcast(this.server_group, new GetTaskReq(id_req));
            GetTaskRep gtr = (GetTaskRep) cf.get();

            if(gtr.status)
                return gtr.res;
            else throw new NoSuchElementException("Empty Queue.");
        }
        catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    @Override
    public void endTask(Task t) throws NoSuchElementException{
        try {
            cf = new CompletableFuture();
            int id_req = req_id.incrementAndGet();
            broadcast(this.server_group, new EndTaskReq(id_req, t));
            EndTaskRep etr = (EndTaskRep) cf.get();

            if(!etr.res)
                throw new NoSuchElementException("The task isn't marked as pending.");
        }
        catch (Exception e) { e.printStackTrace(); }
    }
}
