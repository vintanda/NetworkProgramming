/**
 * @Date 2018//11/30
 * @Author LZD
 *
 * 从服务器接收消息的线程
 */
package base.pkt02tcp.chatDemo.demo03;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveFromServer implements Runnable {
    // 读取消息输入流
    private DataInputStream dis;

    // 消息
    private String msg = "";

    // 线程运行标志
    private boolean isRunning = true;

    // 构造初始化流
    public ReceiveFromServer(Socket client) {
        try {
            dis = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
//            e.printStackTrace();
            Utils.closeAll(dis);
        }
    }

    // 接收数据
    private String recieve() {
        try {
            msg = dis.readUTF();
        } catch (IOException e) {
//            e.printStackTrace();
            isRunning = false;
            Utils.closeAll(dis);
        }
        if (null != msg && !"".equals(msg)) {
            return msg;
        }
        return "";
    }

    @Override
    public void run() {
        while (isRunning) {
            System.out.println(recieve());
        }
    }
}
