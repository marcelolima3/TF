package scheduler;

import scheduler.Impl.RequestInfo;
import scheduler.Impl.SchedulerImp;
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
                if(msg.getMembers().length == 1)
                    registerMainHandlers();

                if(msg.isCausedByJoin() && s.getPrivateGroup().equals(msg.getJoined())){
                    requestState();
                }
            });
            s.handler(StateRep.class, (sender, msg) -> {
                if(this.state_sender == null) {
                    this.state_sender = sender.getSender();
                    System.out.println("StateRep received");
                    this.scheduler = getState(msg);
                    processBuffer();
                    registerMainHandlers();
                }
            });
            s.handler(NewTaskReq.class, (sender, msg) -> buffer.add(new RequestInfo(sender, msg)));
            s.handler(GetTaskReq.class, (sender, msg) -> buffer.add(new RequestInfo(sender, msg)));
            s.handler(EndTaskReq.class, (sender, msg) -> buffer.add(new RequestInfo(sender, msg)));
            s.open().thenRun(() -> {
                System.out.println("Starting...");
                s.join(this.group);
            });
        });
    }

    private void processBuffer() {
        for(RequestInfo ri: buffer){
            if(ri.getMsg() instanceof NewTaskReq){
                System.out.println("NewTask received");
            }
            else if(ri.getMsg() instanceof GetTaskReq){
                System.out.println("GetTask received");
            }
            else if(ri.getMsg() instanceof EndTaskReq){
                System.out.println("EndTask received");
            }
        }
    }

    private void registerMainHandlers(){
        tcspread.execute(() -> {
            s.handler(NewTaskReq.class, (sender, msg) -> {
                System.out.println("NewTask received");
            });
            s.handler(GetTaskReq.class, (sender, msg) -> {
                System.out.println("GetTask received");
            });
            s.handler(EndTaskReq.class, (sender, msg) -> {
                System.out.println("EndTask received");
            });
            s.handler(StateReq.class, (sender, msg) -> {
                System.out.println("StateReq received");
                stateTransfer(sender.getSender());
            });
        });
    }

    private Scheduler getState(StateRep msg) {
        return null;
    }

    private void requestState(){

    }

    private void stateTransfer(SpreadGroup joined){

    }

    public void sendMsg(SpreadGroup group, Object msg){
        SpreadMessage sm = new SpreadMessage();
        sm.addGroup(group);
        sm.setAgreed();
        this.s.multicast(sm, msg);
    }

    private void registerMoreMsg(){
        tcspread.serializer().register(Address.class);
        tcspread.serializer().register(EndTaskReq.class);
        tcspread.serializer().register(EndTaskRep.class);
        tcspread.serializer().register(GetTaskRep.class);
        tcspread.serializer().register(GetTaskReq.class);
        tcspread.serializer().register(NewTaskRep.class);
        tcspread.serializer().register(NewTaskReq.class);
        tcspread.serializer().register(StateRep.class);
        tcspread.serializer().register(StateReq.class);
    }
}
