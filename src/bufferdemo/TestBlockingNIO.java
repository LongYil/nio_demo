package bufferdemo;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * 一、使用NIO完成网络通信的三个核心：
 *  1.通道（Channel):负责连接
 *
 *      java.nio.channels.Channel 接口：
 *         SelectableChannel
 *              SocketChannel
 *              ServerSocketChannel
 *              DatagramChannel
 *
 *              Pipe.SinkChannel
 *              Pipe.SourceChannel
 *
 *  2.缓冲区(Buffer)：负责数据的存取
 *  3.选择器(Selector)：是SelectableChannel的多路复用器。用于监控SelectableChannel的IO状况
 */

public class TestBlockingNIO {

    //客户端
    @Test
    public void test1() throws Exception{
        //1.获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost",8888));

        FileChannel inChannel = FileChannel.open(Paths.get("E:\\1.png"), StandardOpenOption.READ);
        //2.分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //3.读取本地文件，并发送到服务端
        while (inChannel.read(buf) != -1){
            buf.flip();
            socketChannel.write(buf);
            buf.clear();
        }

        //4.关闭通道
        inChannel.close();
        socketChannel.close();

    }

    //服务端
    @Test
    public void test2() throws Exception{
        //1.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        FileChannel outChannel = FileChannel.open(Paths.get("2.png"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);

        //2.绑定连接
        serverSocketChannel.bind(new InetSocketAddress(8888));

        //3.获取客户端连接的通道
        System.out.println("开始接收连接");
        SocketChannel socketChannel = serverSocketChannel.accept();
        System.out.println("成功接收到连接");
        //4.分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //5.接收客户端的数据，并保存到本地
        while (socketChannel.read(buf) != -1){
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        //6.关闭通道
        socketChannel.close();
        outChannel.close();
        serverSocketChannel.close();

    }

}
