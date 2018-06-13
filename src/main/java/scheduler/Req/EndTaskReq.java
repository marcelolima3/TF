package scheduler.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class EndTaskReq implements CatalystSerializable {
    public int id;
    public int user_id;

    public EndTaskReq() {}

    public EndTaskReq(int id, int user_id) {
        this.id = id;
        this.user_id = user_id;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
        bufferOutput.writeInt(user_id);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.id = bufferInput.readInt();
        this.user_id = bufferInput.readInt();
    }
}
