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

    private void registerHandlers() {
        tc.execute(() -> {
            s.handler(MembershipInfo.class, (sender, msg) -> {
                if(msg.isCausedByDisconnect() || msg.isCausedByLeave()){
                    System.out.println("Client failure - " + msg.getLeft().toString());
                    sendMsg(server_group, new ClientFailure(msg.getLeft().toString()));
                }
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
            s.open().thenRun(() -> {
                System.out.println("Starting...");
                s.join(this.client_group);
            });
        });
    }

    public void sendMsg(String group, Object msg){
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
        tc.serializer().register(ClientFailure.class);
    }


    @Override
    public void newTask(String url) {
        try {
            cf = new CompletableFuture();
            int id_req = req_id.incrementAndGet();
            sendMsg(this.server_group, new NewTaskReq(id_req, url));

            NewTaskRep ntr = (NewTaskRep) cf.get();
            if(!ntr.res)
                throw new RepeatedTaskException("Repeated task" + url);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public Task getTask() throws NoSuchElementException{
        try {
            cf = new CompletableFuture();
            int id_req = req_id.incrementAndGet();
            sendMsg(this.server_group, new GetTaskReq(id_req));
            GetTaskRep gtr = (GetTaskRep) cf.get();
            if(gtr.status)
                return gtr.res;
            else throw new NoSuchElementException();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void endTask(Task t) {
        try {
            cf = new CompletableFuture();
            int id_req = req_id.incrementAndGet();
            sendMsg(this.server_group, new EndTaskReq(id_req, t));

            EndTaskRep etr = (EndTaskRep) cf.get();
            if(!etr.res)
                throw new NoSuchElementException();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
