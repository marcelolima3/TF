package scheduler.Remote;

import scheduler.Interfaces.Scheduler;
import scheduler.Rep.GetTaskRep;
import scheduler.Rep.NewTaskRep;
import scheduler.Req.GetTaskReq;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.concurrent.ThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import pt.haslab.ekit.Spread;
import spread.SpreadMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;


public class RemoteScheduler implements Scheduler {
    private final ThreadContext tc;
    private final Spread s;
    private AtomicInteger req_id = new AtomicInteger(0);
    private CompletableFuture f;
    private int id;
    
    public RemoteScheduler(int id) throws Exception {
        Transport t = new NettyTransport();
        this.id = id;
        tc = new SingleThreadContext("srv-%d", new Serializer());
        s = new Spread("user-"+this.id, false);

        registeMsg();
        registHandlers();
    }

    private void registHandlers() {
        tc.execute(() -> {
            s.open().thenRun(() -> {
                System.out.println("Starting...");
                s.join("u"+this.id);
            });
            s.handler(String.class, (sender, msg) ->  {
                System.out.println("Recebi: "+ msg + " de " + sender.getSender());
                if(msg.contains("join"))
                    broadcast("OK");
            });
            s.handler(GetTaskRep.class, (sender, msg) -> {
                System.out.println("GetTask received");
                if(msg.id == req_id.intValue())
                    f.complete(msg);
            });
            s.handler(NewTaskRep.class, (sender, msg) -> {
                System.out.println("NewTask received");
                if(msg.id == req_id.intValue())
                    f.complete(msg);
            });
        });
    }

    private void broadcast(Object msg){
        send_msg(s, "g", msg);
    }

    public static void send_msg(Spread s, String group, Object msg){
        SpreadMessage sm = new SpreadMessage();
        sm.addGroup(group);
        s.multicast(sm, msg);
    }

    private void registeMsg(){
        tc.serializer().register(Address.class);
        tc.serializer().register(GetTaskRep.class);
        tc.serializer().register(GetTaskReq.class);
        tc.serializer().register(NewTaskRep.class);
    }

    public double balance() {
        tc.execute(() -> {
            int req_id = this.req_id.incrementAndGet();
            // fazer cenas
            f = new CompletableFuture();
        }).join();

        try {
            GetTaskRep r = (GetTaskRep) f.get();
            return r.value;
        } catch (Exception e) { e.printStackTrace(); }

        return 0;
    }

    @Override
    public void newTask() {

    }

    @Override
    public void getTask() {

    }

    @Override
    public void endTask() {

    }
}
