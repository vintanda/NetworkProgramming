/**
 * @Date 2018/11/27
 * @Author LZD
 */
package base.pkt02tcp.chatDemo.demo02;

import java.io.*;
import java.net.Socket;

/*
    发送线程
 */
public class Send implements Runnable {
    // 控制台输入流
    private BufferedReader console;

    // 管道输出流
    private DataOutputStream dos;

    // 线程运行标志
    private boolean isRunning = true;

    // 构造进行初始化
    public Send() {
        console = new BufferedReader(new InputStreamReader(System.in));
    }

    public Send(Socket socket) {
        this();
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
//            e.printStackTrace();
            // 初始化中出现异常, 将是否正在执行线程的标志isRunning置为false, 并关闭所有需要关闭的流
            isRunning = false;

            // 调用工具类关闭所有的流
            CloseUtil.CloseAll(dos, console);
        }
    }

    /*
    发送数据功能：
        1.从控制台获取发送内容
        2.将数据发送出去
     */
    // 1.从控制台获取发送内容
    public String getMsgFromConsole() {
        try {
            return console.readLine();
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return "";
    }

    // 2.将数据发送出去
    public void send() {
        String msg = getMsgFromConsole();
        try {
            if(null != msg && !"".equals(msg)) {
                dos.writeUTF(msg);
                dos.flush();
            }
        } catch (IOException e) {
//            e.printStackTrace();
            isRunning = false;
            CloseUtil.CloseAll(dos, console);
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
