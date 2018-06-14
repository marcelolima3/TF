package scheduler.Req;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;
import spread.SpreadGroup;

public class ClientFailure implements CatalystSerializable {
    public String client;

    public ClientFailure(){}

    public ClientFailure(String left) {
        this.client = left;
    }

    @Override
    public void writeObject(BufferOutput<?> buffer, Serializer serializer) {
        buffer.writeString(client);
    }

    @Override
    public void readObject(BufferInput<?> buffer, Serializer serializer) {
        this.client = buffer.readString();
    }
}
