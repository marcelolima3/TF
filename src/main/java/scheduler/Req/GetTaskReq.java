package scheduler.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class GetTaskReq implements CatalystSerializable{
    public double value;
    public int id;
    public int user_id;

    public GetTaskReq(){ }

    public GetTaskReq(double value, int id, int user_id){
        this.value = value;
        this.id = id;
        this.user_id = user_id;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeDouble(value);
        bufferOutput.writeInt(id);
        bufferOutput.writeInt(user_id);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.value = bufferInput.readDouble();
        this.id = bufferInput.readInt();
        this.user_id = bufferInput.readInt();
    }
}
