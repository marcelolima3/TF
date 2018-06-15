package scheduler.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Impl.Task;

public class GetTaskRep implements CatalystSerializable {
    public int id;
    public Task res;
    public boolean status;

    public GetTaskRep() {}

    public GetTaskRep(int id, Task res, boolean status){
        this.id = id;
        this.res = res;
        this.status = status;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
        serializer.writeObject(res, bufferOutput);
        bufferOutput.writeBoolean(status);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.id = bufferInput.readInt();
        this.res = serializer.readObject(bufferInput);
        this.status = bufferInput.readBoolean();
    }
}
