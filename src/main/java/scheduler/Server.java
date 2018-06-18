package scheduler;

import io.atomix.catalyst.concurrent.SingleThreadContext;
import io.atomix.catalyst.serializer.Serializer;
import io.atomix.catalyst.transport.Transport;
import io.atomix.catalyst.transport.netty.NettyTransport;
import pt.haslab.ekit.Spread;

public class Server {
    public static void main(String[] args) throws Exception {
        int id = Integer.parseInt(args[0]);
        String group = "servers";

        SingleThreadContext tcspread = new SingleThreadContext("srv-%d", new Serializer());
        Spread s = new Spread("srv"+id, true);

        ServerHandlers b = new ServerHandlers(s, tcspread, id, group);
        b.exe();
    }
}
