package scheduler.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class NewTaskRep implements CatalystSerializable {
    public int id;
    public boolean res;

    public NewTaskRep() {}

    public NewTaskRep(int id, boolean res){
        this.res = res;
        this.id = id;
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
