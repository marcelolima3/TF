package scheduler;

import scheduler.Impl.RequestInfo;
import scheduler.Impl.SchedulerImp;
import scheduler.Impl.Task;
import scheduler.Interfaces.Scheduler;
import scheduler.Rep.*;
import scheduler.Req.*;
import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.Transport;
import pt.haslab.ekit.Spread;
import spread.MembershipInfo;
import spread.SpreadGroup;
import spread.SpreadMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ServerHandlers {
    public int id;
    public Transport t;
    public SingleThreadContext tcspread;
    public Scheduler scheduler;
    public Spread s;
    public String group;
    public SpreadGroup state_sender;
    public List<RequestInfo> buffer;


    public ServerHandlers(Transport t, Spread s, SingleThreadContext tcspread, int id, String group) {
        this.t = t;
        this.tcspread = tcspread;
        this.id = id;
        this.scheduler = new SchedulerImp();
        this.s = s;
        this.group = group;
        this.buffer = new ArrayList<>();
        this.state_sender = null;
    }

    public void exe(){
        registerMoreMsg();
        registerSpreadHandlers();
        System.out.println("Server running...");
    }

    private void registerSpreadHandlers(){
        tcspread.execute(() -> {
            s.handler(MembershipInfo.class, (sender, msg) ->  {
                if(msg.getMembers().length == 1) {
                    System.out.println("-- I'm the first server");
                    registerMainHandlers();
                }
                else if(msg.isCausedByJoin() && s.getPrivateGroup().equals(msg.getJoined())) {
                    System.out.println("-- Other server");
                    sendMsg(this.group, new  StateReq());
                }
            });
            s.handler(StateRep.class, (sender, msg) -> {
                if(this.state_sender == null) {
                    System.out.println("StateRep received");
                    this.state_sender = sender.getSender();
                    this.scheduler = getState(msg);
                    processBuffer();
                    registerMainHandlers();
                }
            });
            s.handler(NewTaskReq.class, (sender, msg) ->
                    buffer.add(new RequestInfo(sender.getSender(), msg)));
            s.handler(GetTaskReq.class, (sender, msg) ->
                    buffer.add(new RequestInfo(sender.getSender(), msg)));
            s.handler(EndTaskReq.class, (sender, msg) ->
                    buffer.add(new RequestInfo(sender.getSender(), msg)));
            s.open().thenRun(() -> {
                System.out.println("Starting...");
                s.join(this.group);
            });
        });
    }

    private void processBuffer() {
        for(RequestInfo ri: buffer){
            if(ri.getMsg() instanceof NewTaskReq){
                System.out.println("NewTask received-Buffer");

                NewTaskReq ntr = (NewTaskReq) ri.getMsg();
                boolean res = scheduler.newTask(ntr.url);
                sendMsg(ri.getSender(), new NewTaskRep(ntr.id, res));
            }
            else if(ri.getMsg() instanceof GetTaskReq){
                System.out.println("GetTask received-Buffer");

                GetTaskReq gtr = (GetTaskReq) ri.getMsg();
                Task t = scheduler.getTask(ri.getSender().toString());
                sendMsg(ri.getSender(), new GetTaskRep(gtr.id, t));
            }
            else if(ri.getMsg() instanceof EndTaskReq){
                System.out.println("EndTask received-Buffer");

                EndTaskReq etr = (EndTaskReq) ri.getMsg();
                boolean res = scheduler.endTask(etr.t);
                sendMsg(ri.getSender(), new EndTaskRep(etr.id, res));
            }
        }
    }

    private void registerMainHandlers(){
        tcspread.execute(() -> {
            s.handler(NewTaskReq.class, (sender, msg) -> {
                System.out.println("NewTask received-Main");

                boolean res = scheduler.newTask(msg.url);
                sendMsg(sender.getSender(), new NewTaskRep(msg.id, res));
            });
            s.handler(GetTaskReq.class, (sender, msg) -> {
                System.out.println("GetTask received-Main");

                Task t = scheduler.getTask(sender.getSender().toString());
                sendMsg(sender.getSender(), new GetTaskRep(msg.id, t));
            });
            s.handler(EndTaskReq.class, (sender, msg) -> {
                System.out.println("EndTask received-Main");

                boolean res = scheduler.endTask(msg.t);
                sendMsg(sender.getSender(), new EndTaskRep(msg.id, res));
            });
            s.handler(ClientFailure.class, (sender, msg) -> {
                System.out.println("ClientFailure received");

                ((SchedulerImp)scheduler).shiftTasksFromClient(sender.getSender().toString());
            });
            s.handler(StateReq.class, (sender, msg) -> {
                System.out.println("StateReq received-Main");
                stateTransfer(sender.getSender());
            });
        });
    }

    private Scheduler getState(StateRep msg) {
        // TODO implement state transfer for big data
        return msg.scheduler;
    }

    private void stateTransfer(SpreadGroup joined){
        // TODO implement state transfer for big data
        sendMsg(joined, new StateRep(scheduler));
    }

    public void sendMsg(SpreadGroup group, Object msg){
        SpreadMessage sm = new SpreadMessage();
        sm.addGroup(group);
        sm.setAgreed();
        this.s.multicast(sm, msg);
    }

    public void sendMsg(String group, Object msg){
        SpreadMessage sm = new SpreadMessage();
        sm.addGroup(group);
        sm.setAgreed();
        this.s.multicast(sm, msg);
    }

    private void registerMoreMsg(){
        tcspread.serializer().register(EndTaskReq.class);
        tcspread.serializer().register(EndTaskRep.class);
        tcspread.serializer().register(GetTaskRep.class);
        tcspread.serializer().register(GetTaskReq.class);
        tcspread.serializer().register(NewTaskRep.class);
        tcspread.serializer().register(NewTaskReq.class);
        tcspread.serializer().register(StateRep.class);
        tcspread.serializer().register(StateReq.class);
        tcspread.serializer().register(SchedulerImp.class);
        tcspread.serializer().register(Task.class);
        tcspread.serializer().register(ClientFailure.class);
    }
}
