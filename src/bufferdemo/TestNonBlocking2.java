package bufferdemo;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 *
 */

public class TestNonBlocking2 {

    //客户端
    @Test
    public void test1() throws Exception{
        DatagramChannel datagramChannel = DatagramChannel.open();

        datagramChannel.configureBlocking(false);

        ByteBuffer buf = ByteBuffer.allocate(1024);

        buf.put("hello,world".getBytes());
        buf.flip();

        datagramChannel.send(buf,new InetSocketAddress("localhost",8888));

        datagramChannel.close();
    }

    //服务端
    @Test
    public void test2() throws Exception{
        DatagramChannel datagramChannel = DatagramChannel.open();

        datagramChannel.configureBlocking(false);

        datagramChannel.bind(new InetSocketAddress(8888));

        Selector selector = Selector.open();

        datagramChannel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0){
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()){
                SelectionKey sk = it.next();

                if(sk.isReadable()){
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    datagramChannel.receive(buf);
                    buf.flip();
                    System.out.println(new String(buf.array(),0,buf.limit()));
                }
            }
            it.remove();
        }
    }

}
