package scheduler.Remote;

import io.atomix.catalyst.transport.Client;
import scheduler.Req.ClientFailure;
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
import spread.SpreadGroup;
import spread.SpreadMessage;


public class RemoteScheduler implements Scheduler {
    private final ThreadContext tc;
    private final Spread s;
    private final String client_group;
    private final String server_group;
    private int id;

    public RemoteScheduler(int id) throws Exception {
        this.id = id;
        tc = new SingleThreadContext("srv-%d", new Serializer());
        s = new Spread("user-" + this.id, true);
        this.server_group = "servers";
        this.client_group = "users";

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
            });
            s.handler(NewTaskRep.class, (sender, msg) -> {
                System.out.println("NewTask received");
            });
            s.handler(EndTaskRep.class, (sender, msg) -> {
                System.out.println("EndTask received");
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
    public void newTask() {

    }

    @Override
    public void getTask() {

    }

    @Override
    public void endTask() {

    }
}
