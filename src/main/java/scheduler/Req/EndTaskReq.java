package scheduler.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Impl.Task;

public class EndTaskReq implements CatalystSerializable {
    public int id;
    public Task t;

    public EndTaskReq() {}

    public EndTaskReq(int id, Task t) {
        this.id = id;
        this.t = t;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
        serializer.writeObject(t, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.id = bufferInput.readInt();
        this.t = serializer.readObject(bufferInput);
    }
}
