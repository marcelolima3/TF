package scheduler.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import scheduler.Impl.Task;

public class GetTaskRep implements CatalystSerializable {
    public int id;
    public Task res;

    public GetTaskRep() {}

    public GetTaskRep(int id, Task res){
        this.id = id;
        this.res = res;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
        serializer.writeObject(res, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.id = bufferInput.readInt();
        this.res = serializer.readObject(bufferInput);
    }
}
