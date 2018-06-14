package scheduler.Impl;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Interfaces.Scheduler;

public class SchedulerImp implements Scheduler, CatalystSerializable {

    public SchedulerImp() {

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

    @Override
    public void writeObject(BufferOutput<?> buffer, Serializer serializer) {

    }

    @Override
    public void readObject(BufferInput<?> buffer, Serializer serializer) {

    }
}
