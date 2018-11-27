/**
 * @Date 2018/11/27
 * @Author LZD
 *
 * 服务器端 --- 可接收多个客户端的连接：
 *  1.创建服务器 + 端口
 *  2.接收客户端连接(阻塞式)
 *  3.接收 + 发送数据
 */
package base.pkt02tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiLinkServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket server = new ServerSocket(8989);

        // 死循环处理每个客户端 --> 不合理, 应该用多线程
        while(true) {
            Socket socket = server.accept();
            System.out.println("一个客户端已建立连接");

            String msg = "欢迎使用";

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(msg);
            dos.flush();

            // 当前客户端还未处理结束不能处理下一个连接的客户端
            Thread.sleep(10000);
        }
    }
}
