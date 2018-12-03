/**
 * @Date 2018//11/30
 * @Author LZD
 *
 * 发送给服务器的线程
 */
package base.pkt02tcp.chatDemo.demo03;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SendToServer implements Runnable {
    // 控制台流
    private BufferedReader console;

    // 向通道发送数据的流
    private DataOutputStream dos;

    // 线程运行标志
    private boolean isRunning = true;

    // 发送的消息
    private String msg = "";

    // 构造初始化流
    public SendToServer(Socket client) {
        console = new BufferedReader(new InputStreamReader(System.in));
        try {
            dos = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
//            e.printStackTrace();
            isRunning = false;
            Utils.closeAll(dos, console);
        }
    }

    // 发送数据
    private void send() {
        msg = Utils.getInputFromConsole(console);
        if(null == msg || "".equals(msg)) {
            return;
        }
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
//            e.printStackTrace();
            isRunning = false;
            Utils.closeAll(dos, console);
        }
    }

    // 线程体
    @Override
    public void run() {
        while (isRunning) {
            send();
        }
    }
}
