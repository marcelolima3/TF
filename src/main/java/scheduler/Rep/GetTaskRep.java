package scheduler.Rep;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class GetTaskRep implements CatalystSerializable {
    // TODO
    public double value;
    public int id;

    public GetTaskRep() {}

    public GetTaskRep(double value, int id){
        this.value = value;
        this.id = id;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeDouble(value);
        bufferOutput.writeInt(id);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.value = bufferInput.readDouble();
        this.id = bufferInput.readInt();
    }
}
