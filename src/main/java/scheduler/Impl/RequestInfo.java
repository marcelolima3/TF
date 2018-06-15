package scheduler.Impl;

import scheduler.Req.NewTaskReq;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class RequestInfo {
    private SpreadGroup sender;
    private Object msg;

    public RequestInfo(SpreadGroup sender, Object msg) {
        this.sender = sender;
        this.msg = msg;
    }

    public SpreadGroup getSender() {
        return sender;
    }

    public void setSender(SpreadGroup sender) {
        this.sender = sender;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
