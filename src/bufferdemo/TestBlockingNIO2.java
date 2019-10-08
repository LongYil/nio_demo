package bufferdemo;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.WeakHashMap;

public class TestBlockingNIO2 {

    //客户端
    @Test
    public void test1() throws Exception{
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost",8888));

        FileChannel inChannel = FileChannel.open(Paths.get("E:\\1.png"),StandardOpenOption.READ);
        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (inChannel.read(buf) != -1){
            buf.flip();
            socketChannel.write(buf);
            buf.clear();
        }

        socketChannel.shutdownOutput();

        //接收服务端的反馈
        int len = 0;
        while ((len = socketChannel.read(buf)) != -1){
            buf.flip();
            System.out.println(new String(buf.array(),0,len));
            buf.clear();
        }

        inChannel.close();
        socketChannel.close();
    }

    //服务端
    @Test
    public void test2() throws Exception{
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        FileChannel outChannel = FileChannel.open(Paths.get("2.png"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        serverSocketChannel.bind(new InetSocketAddress(8888));

        SocketChannel socketChannel = serverSocketChannel.accept();

        ByteBuffer buf = ByteBuffer.allocate(1024);
        while (socketChannel.read(buf) != -1){
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        //发送反馈给客户端
        buf.put("服务端接收数据成功".getBytes());
        buf.flip();
        socketChannel.write(buf);

        socketChannel.close();
        outChannel.close();
        serverSocketChannel.close();
    }

}
