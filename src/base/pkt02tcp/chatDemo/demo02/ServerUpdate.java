/**
 * @Date 2018/11/28
 * @Author LZD
 *
 * 服务端
 *  为每个用户建立一个线程, 使得用户间不相互影响
 *  实现客户端消息发送后被其他多个客户端所接收到
 *  使用集合类对象对连接上服务器的客户端进行管理
 */
package base.pkt02tcp.chatDemo.demo02;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerUpdate {
    private List<MyChannel> all = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        new ServerUpdate().start();
    }

    public void start() throws IOException {
        ServerSocket server = new ServerSocket(9999);

        while (true) {
            Socket client = server.accept();
            MyChannel channel = new MyChannel(client);
            all.add(channel);
            new Thread(channel).start();
        }
    }

    /**
     * 一个客户端一条道路
     */
    private class MyChannel implements Runnable {
        private DataInputStream dis;
        private DataOutputStream dos;
        private boolean isRunning = true;

        public MyChannel() {
        }

        public MyChannel(Socket client) {
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
            } catch (IOException e) {
//                e.printStackTrace();
                CloseUtil.CloseAll();
                isRunning = false;
            }
        }

        // 接受数据
        public String recieve() {
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
//                e.printStackTrace();
                CloseUtil.CloseAll(dis);
                isRunning = false;
                all.remove(this);
            }
            return msg;
        }

        // 发送数据 --- 原路径发送...
        public void send(String msg) {
            if(null == msg || "".equals(msg)) {
                return;
            }
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
//                e.printStackTrace();
                CloseUtil.CloseAll(dos);
                isRunning = false;
            }

        }

        // 发送数据 --- 发给别人
        public void sendToOthers(String msg) {
            if (null == msg || "".equals(msg)) {
                return;
            }
            for (MyChannel other : all) {
                if (other == this) {
                    continue;
                }
                other.send(msg);
            }
        }

        @Override
        public void run() {
            while (isRunning) {
                String msg = recieve();
                sendToOthers(msg);
            }
        }
    }
}
