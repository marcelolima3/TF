package scheduler.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class EndTaskRep implements CatalystSerializable {
    public int id;
    public boolean res;

    public EndTaskRep() {}

    public EndTaskRep(int id, boolean res){
        this.id = id;
        this.res = res;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeBoolean(res);
        bufferOutput.writeInt(id);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.res = bufferInput.readBoolean();
        this.id = bufferInput.readInt();
    }
}
