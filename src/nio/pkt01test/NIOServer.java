/**
 * @Date 2018/12/05
 * @Author LZD
 *
 * NIO 服务端 firstTest
 */
package nio.pkt01test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;

public class NIOServer implements Runnable {
    // 多路复用器
    private Selector selector;

    // 两个缓冲流, 分别用于读和写, 初始化空间大小与定义的类相关
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);

    // 程序入口
    public static void main(String[] args) {
        new Thread(new NIOServer(9999)).start();
    }

    // 初始化构造
    public NIOServer(int port) {
        init(port);
    }

    // init方法
    public void init(int port) {
        try {
            System.out.println("服务器已打开, 端口号是：" + port);

            // 打开多路复用器
            this.selector = Selector.open();

            // 开启服务通道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

            // 设置服务是否阻塞, true -> 阻塞  false -> 非阻塞
            serverSocketChannel.configureBlocking(false);

            // 绑定服务端端口
            serverSocketChannel.bind(new InetSocketAddress(port));

            // 注册并标记当前通道状态
            /*
                register(Selector sel, int ops)
                  Selector:注册到的多路复用器
                  ops:注册的通道状态
                      OP_READ(=1<<0):一个有数据可读的通道可以说是"读就绪"
                      OP_WRITE(=1<<2):一个等待写数据的通道可以说是"写就绪"
                      OP_CONNECT(=1<<3):一个channel成功连接到另一个服务器称为"连接就绪"
                      OP_ACCEPT(=1<<4):一个server socket channel准备号接收新进入的连接称为"接收就绪"
             */
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server started...");
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    // 线程体
    @Override
    public void run() {
        while (true) {
            try {
                /*
                【从jdk1.8源码中复制的注释】
                 Selects a set of keys whose corresponding channels are ready for I/O operations.
                 This method performs a blocking selection operation.
                 It returns only after at least one channel is selected,
                 this selector's {@link #wakeup wakeup} method is invoked,
                 or the current thread is interrupted, whichever comes first.
                  */
                this.selector.select();

                // 返回通道标记集合, 集合中保存的是通道的标记, 相当于通道的ID【???这东西相当于id??】
                Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();

                // 遍历通道标记
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();

                    // 将已处理过的(就是当前的)通道标记移除
                    keys.remove();

                    // 通道是否有效, 通道有效则做处理
                    // jdk1.8中对isValid()的解释：
                    //  A key is valid upon creation and remains so until it is cancelled,
                    //  its channel is closed, or its selector is closed.
                    if (key.isValid()) {
                        try {
                            // 阻塞状态 直到有客户端连接上, 执行accept方法
                            if (key.isAcceptable()) {
                                accept(key);
                            }
                        }catch (CancelledKeyException cke) {
                            key.cancel();
                        }
                        try {
                            // 阻塞状态 直到有客户端连接上, 执行read方法
                            if (key.isReadable()) {
                                read(key);
                            }
                        }catch (CancelledKeyException cke) {
                            key.cancel();
                        }
                        try {
                            // 阻塞状态 直到有客户端连接上, 执行write方法
                            if (key.isWritable()) {
                                write(key);
                            }
                        }catch (CancelledKeyException cke) {
                            key.cancel();
                        }

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // init方法中注册到Selector上的ServerSocketChannel需要执行的【此时是等待到连接进行处理的状态
    public void accept(SelectionKey key) {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

            // 阻塞方法, 当客户端发起请求后返回, 此通道和客户端一一对应
            SocketChannel channel = serverSocketChannel.accept();

            // 将该对应通道设置为非阻塞
            channel.configureBlocking(false);

            // 设置并注册对应客户端的通道标记状态, 此通道为读取数据使用
            channel.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取缓冲区数据并设置并注册通道为写
    public void read(SelectionKey key) {
        try {
            // 清空缓存
            this.readBuffer.clear();

            // 获取通道
            SocketChannel channel = (SocketChannel) key.channel();

            // 将通道中的数据读到缓存, readLength为通道中数据的长度
            int readLength = channel.read(readBuffer);

            // 判断通道中是否有数据, 并进行相应处理
            if (readLength == -1) {
                // 关闭通道
                key.channel().close();

                // 关闭连接
                key.cancel();
                return;
            }

            /*
            NIO中最难的是Buffer的控制
            Buffer中有一个游标, 游标信息在操作之后不会自动归零, 直接访问Buffer可能导致数据不一致
            flip():重置标记的常用方法
             */
            this.readBuffer.flip();

            // 创建字节数组, 用于保存具体数据
            // remaining():返回有效数据的长度
            byte[] data = new byte[readBuffer.remaining()];

            // 将Buffer中的有效数据保存到data数组中
            readBuffer.get(data);

            System.out.println("from:" + channel.getRemoteAddress() + " client:"
                                + new String(data, "UTF-8"));

            // 注册通道, 标记为写操作
            channel.register(this.selector, SelectionKey.OP_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                key.channel().close();
                key.cancel();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // 读写交替进行...
    public void write(SelectionKey key) {
        // 清空缓存
        this.writeBuffer.clear();

        SocketChannel channel = (SocketChannel) key.channel();

        // 从控制台输入数据
        Scanner console = new Scanner(System.in);

        // 将控制台输入的字符串写入到Buffer中, 写入的数据是一个字节数组
        try {
            System.out.println("put message for send to client...");
            String line = console.nextLine();

            // 将从控制台读入的数据写入缓冲
            writeBuffer.put(line.getBytes("UTF-8"));

            // 缓冲标记重置
            writeBuffer.flip();

            // 将缓冲中数据写入通道
            channel.write(writeBuffer);

            // 将通道设置为读并重新注册
            channel.register(this.selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
