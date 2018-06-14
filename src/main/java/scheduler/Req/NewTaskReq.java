package scheduler.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class NewTaskReq implements CatalystSerializable {
    public int id;
    public String url;

    public NewTaskReq() {}

    public NewTaskReq(int id, String url) {
        this.id = id;
        this.url = url;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(id);
        bufferOutput.writeString(url);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.id = bufferInput.readInt();
        this.url = bufferInput.readString();
    }
}
