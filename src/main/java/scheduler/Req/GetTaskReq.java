package scheduler.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class GetTaskReq implements CatalystSerializable{
    public int id;

    public GetTaskReq(){ }

    public GetTaskReq(int id){
        this.id = id;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.id = bufferInput.readInt();
    }
}
