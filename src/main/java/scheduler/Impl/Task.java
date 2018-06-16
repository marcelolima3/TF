package scheduler.Impl;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class Task implements CatalystSerializable {
    private String url;

    public Task(){ }

    public Task(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void run() throws InterruptedException {
        Thread.sleep((long)(Math.random() * 5000));
    }

    @Override
    public void writeObject(BufferOutput<?> buffer, Serializer serializer) {
        buffer.writeString(this.url);
    }

    @Override
    public void readObject(BufferInput<?> buffer, Serializer serializer) {
        this.url = buffer.readString();
    }
}
