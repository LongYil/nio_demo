package bufferdemo;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 1,缓冲区(Buffer):在Java NIO 中负责数据的存取。缓冲区就是数组
 * 用于存储不同数据类型的数据
 *
 * 根据数据类型的不同(boolean 除外)，提供了相应类型的缓冲区：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 *
 * 上述缓冲区的管理方式几乎一致，通过allocate() 获取缓冲区
 *
 * 2.缓冲区存取数据的两个核心方法：
 * put():存入数据到缓冲区中
 * get():获取缓冲区中的数据
 *
 * 3.缓冲区中的四个核心属性：
 * capacity:容量，表示缓冲区最大存储数据的容量。一旦声明不能改变。
 * limit:界限，表示缓冲区中可以操作数据的大小。（limit后面的数据是不能进行读写的）
 * position：位置，表示缓冲区中正在操作的数据的位置。
 *
 * mark:标记，表示记录当前position的位置。可以通过reset()恢复到mark的位置
 * 0 <= mark <= position <= limit <= capacity
 *
 * 4.直接缓冲区和非直接缓冲区
 * 非直接缓冲区：通过allocate()方法分配缓冲区，将缓冲区建立在JVM的内存中，效率低
 * 直接缓冲区（System.gc可以加快垃圾回收机制）：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在物理内存中。可以提高效率，但消耗很大
 *
 *
 *
 */
public class TestBuffer {

    @Test
    public void test1(){
        //1.分配一个指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        System.out.println("----------------allocate---------------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        //2.利用put()存入数据到缓冲区
        String str = "hello,world";
        buffer.put(str.getBytes());

        System.out.println("----------------put()---------------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        //3.切换读取数据模式
        buffer.flip();
        System.out.println("----------------flip()---------------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        //4.利用get()读取缓冲区中的数据
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst);
        System.out.println(new String(dst,0,dst.length));

        System.out.println("----------------get()---------------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        //5.rewind() 回到读模式，可以重新读数据
        buffer.rewind();
        System.out.println("----------------rewind()---------------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        //6.clear()：清空缓冲区,但是，缓冲区中的数据依然存在，但是处于"被遗忘状态"
        buffer.clear();

        System.out.println("----------------clear()---------------------");
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());

        System.out.println((char) buffer.get(1));
    }

    @Test
    public void test2() throws Exception{
        String str = "hello,world";

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put(str.getBytes());

        buffer.flip();

        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst,0,2);

        System.out.println(new String(dst,0,2));

        System.out.println(buffer.position());
        //mark():标记
        buffer.mark();

        buffer.get(dst,2,2);
        System.out.println(new String(dst,2,2));
        System.out.println(buffer.position());

        //reset():恢复到mark的位置
        buffer.reset();

        //判断缓冲区中是否还有剩余的数据
        System.out.println(buffer.position());
        if(buffer.hasRemaining()){
            //获取缓冲区中可以操作的数量
            System.out.println(buffer.remaining());
        }

    }

    @Test
    public void test3() throws Exception{
        //分配直接缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        System.out.println(buffer.isDirect());

    }







}
