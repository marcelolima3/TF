package scheduler.Impl;

import scheduler.Req.NewTaskReq;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class RequestInfo {
    private SpreadMessage sender;
    private Object msg;

    public RequestInfo(SpreadMessage sender, Object msg) {
        this.sender = sender;
        this.msg = msg;
    }

    public SpreadMessage getSender() {
        return sender;
    }

    public void setSender(SpreadMessage sender) {
        this.sender = sender;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
