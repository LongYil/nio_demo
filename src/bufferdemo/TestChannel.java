package bufferdemo;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * 1.通道（Channel）:用于源节点与目标节点的连接，在Java NIO中负责缓冲区中数据的传输，Channel本身不存储数据，因此需要配合缓冲区进行数据传输。
 *
 * 2.通道的主要实现类
 * java.nio.channels.Channel 接口：
 *  FileChannel
 *  SocketChannel
 *  ServerSocketChannel
 *  DaatagramChannel
 * 3.获取通道
 *  1).Java针对支持通道的类提供了getChannel()方法
 *      本地IO:
 *      FileInputStream/FileOutputStream
 *      RandomAccessFile
 *      网络IO:
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 *  2). 在JDK1.7中的NIO.2 针对各个通道提供了静态方法open()
 *  3).在JDK1.7中的NIO.2的Files工具类 newByteChannel()
 *
 * 4.通道之间的数据传输
 *  transferFrom()
 *  transferTo()
 *
 * 5.分散(Scatter)与聚集(Gather)
 * 分散读取(Scattering Reads)：将通道中的数据分散到多个缓冲区中
 * 聚集写入(Gathering Writes)：将多个缓冲区中的数据聚集到一个通道中
 *
 * 4.字符集：Charset
 *  编码：字符串—>字节数组
 *  解码：字节数据—>字符串
 */
public class TestChannel {

    //1.利用通道完成文件的赋值(非直接缓冲区) 52077
    @Test
    public void test1() throws Exception{

        long start = System.currentTimeMillis();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            fis = new FileInputStream("D:\\BaiduNetdiskDownload\\docker.zip");
            fos = new FileOutputStream("D:\\BaiduNetdiskDownload\\docker1.zip");

            //1.获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);

            //3.将通道中的数据存入缓冲区中
            while (inChannel.read(buf) != -1){
                buf.flip();//切换成读取数据的模式

                //4.将缓冲区中的数据写入通道中
                outChannel.write(buf);
                buf.clear();//清空缓冲区
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outChannel != null){
                outChannel.close();
            }

            if(inChannel != null){
                inChannel.close();
            }

            if(fos != null){
                fos.close();
            }

            if(fis != null){
                fis.close();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);

    }

    //2.使用直接缓冲区完成文件的赋值（内存映射文件）
    @Test
    public void test2() throws Exception{
        long start = System.currentTimeMillis();

        FileChannel inChannel = FileChannel.open(Paths.get("D:\\BaiduNetdiskDownload\\docker.zip"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("D:\\BaiduNetdiskDownload\\docker2.zip"),StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);


        //内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY,0,inChannel.size());

        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE,0,inChannel.size());

        //直接对缓冲区进行数据的读写操作
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void test3() throws Exception{
        FileChannel inChannel = FileChannel.open(Paths.get("D:\\BaiduNetdiskDownload\\docker.zip"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("D:\\BaiduNetdiskDownload\\docker2.zip"),StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);

        inChannel.transferTo(0,inChannel.size(),outChannel);

        inChannel.close();
        outChannel.close();
    }

    @Test
    public void test4() throws Exception{
        RandomAccessFile file = new RandomAccessFile("E:\\in.txt","rw");

        //1.获取通道
        FileChannel channel1 = file.getChannel();

        //2.分配指定大小的缓冲区
        ByteBuffer buffer1 = ByteBuffer.allocate(100);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);

        //3.分散读取
        ByteBuffer[] bufs = {buffer1,buffer2};
        channel1.read(bufs);

        for (ByteBuffer b : bufs){
            b.flip();
        }

        System.out.println(new String(bufs[0].array(),0,bufs[0].limit()));
        System.out.println("-------------------------");
        System.out.println(new String(bufs[1].array(),0,bufs[1].limit()));

        //4.聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("2.txt","rw");
        FileChannel fileChannel2 = raf2.getChannel();

        fileChannel2.write(bufs);

    }

    @Test
    public void test5() throws Exception{
        Map<String, Charset> maps = Charset.availableCharsets();

        maps.forEach((k,v)->{
            System.out.println(k +":" + v);
        });

    }


    @Test
    public void test6() throws Exception{
        Charset cs1 = Charset.forName("GBK");
        //获取编码器
        CharsetEncoder encoder = cs1.newEncoder();
        //获取解码器
        CharsetDecoder decoder = cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("龙达科技");
        cBuf.flip();

        //编码
        ByteBuffer bBuf = encoder.encode(cBuf);

        for (int i = 0; i < 8; i++){
            System.out.println(bBuf.get());
        }

        //解码
        bBuf.flip();
        CharBuffer cBuf2 = decoder.decode(bBuf);
        System.out.println(cBuf2);

    }












}