package scheduler.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class EndTaskRep implements CatalystSerializable {
    public boolean isValid;
    public int id;

    public EndTaskRep() {}

    public EndTaskRep(boolean isValid, int id){
        this.isValid = isValid;
        this.id = id;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeBoolean(isValid);
        bufferOutput.writeInt(id);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.isValid = bufferInput.readBoolean();
        this.id = bufferInput.readInt();
    }
}
