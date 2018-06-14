package scheduler.Remote;

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
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import pt.haslab.ekit.Spread;
import scheduler.Req.NewTaskReq;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;


public class RemoteScheduler implements Scheduler {
    private final ThreadContext tc;
    private final Spread s;
    private AtomicInteger req_id;
    private CompletableFuture cf;
    private int id;
    
    public RemoteScheduler(int id) throws Exception {
        this.id = id;
        tc = new SingleThreadContext("srv-%d", new Serializer());
        s = new Spread("user-" + this.id, false);
        req_id = new AtomicInteger(0);

        registerMsg();
        registerHandlers();
    }

    private void registerHandlers() {
        tc.execute(() -> {
            s.open().thenRun(() -> {
                System.out.println("Starting...");
                s.join("users" + this.id);
            });
            s.handler(GetTaskRep.class, (sender, msg) -> {
                System.out.println("GetTask received");
                if(msg.id == req_id.intValue() && cf!=null)
                    cf.complete(msg);
            });
            s.handler(NewTaskRep.class, (sender, msg) -> {
                System.out.println("NewTask received");
                if(msg.id == req_id.intValue() && cf!=null)
                    cf.complete(msg);
            });
            s.handler(EndTaskRep.class, (sender, msg) -> {
                System.out.println("EndTask received");
                if(msg.id == req_id.intValue() && cf!=null)
                    cf.complete(msg);
            });
        });
    }

    public void sendMsg(SpreadGroup group, Object msg){
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
    }


    @Override
    public void newTask(String url) {
        cf = new CompletableFuture();

        try {
            cf.get();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public Task getTask() {
        cf = new CompletableFuture();

        try {
            cf.get();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void endTask(Task t) {
        cf = new CompletableFuture();

        try {
            cf.get();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
