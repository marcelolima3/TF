package scheduler.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Interfaces.Scheduler;

public class StateRep implements CatalystSerializable {
    public Scheduler scheduler;

    public StateRep(){ }

    public StateRep(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    @Override
    public void writeObject(BufferOutput<?> buffer, Serializer serializer) {
        serializer.writeObject(this.scheduler, buffer);
    }

    @Override
    public void readObject(BufferInput<?> buffer, Serializer serializer) {
        this.scheduler = serializer.readObject(buffer);
    }
}
