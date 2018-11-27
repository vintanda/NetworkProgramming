/**
 * @Date 2018/11/27
 * @Author LZD
 *
 * 服务器端：
 *  1.创建服务器 + 端口
 *  2.接收客户端连接(阻塞式)
 *  3.接收 + 发送数据
 */
package base.pkt02tcp;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        // 创建服务器 + 端口
        ServerSocket server = new ServerSocket(8989);

        // 接收客户端连接
        Socket socket = server.accept();
        System.out.println("一个客户端已建立连接");

        // 发送数据
        String msg = "欢迎使用";

        // 获取输出流
        // 使用BufferedXX在读取时使用的是readLine, 需要添加换行符否则无法读到
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//        bw.write(msg);
//        bw.newLine();    // 添加换行
//        bw.flush();

        // 使用DataOutputStream
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(msg);
        dos.flush();
    }
}
