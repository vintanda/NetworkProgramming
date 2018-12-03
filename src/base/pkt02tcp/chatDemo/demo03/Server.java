/**
 * @Date 2018/12/01
 * @Author LZD
 *
 * 服务端
 *  实现转发功能, 对 私聊/世界/系统消息 做不同处理
 *  私聊信息格式：
 *      发送信息：@username:msg
 *      收到信息：【username】对你说:msg
 *  世界信息格式：
 *      发送信息：msg
 *      收到信息：【username】说：msg
 *  系统信息格式：
 *      【username】已上线/已下线
 */
package base.pkt02tcp.chatDemo.demo03;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    // 管理在线成员
    private List<User> members = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        new Server().serverManage();
    }

    public void serverManage() throws IOException {
        // 指定服务端端口号
        ServerSocket server = new ServerSocket(9999);

        // 使用线程池对连接的客户端进行管理
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("等待客户连接中...");
        for (int i = 0;i < 20;i++) {
            Socket client = server.accept();
            User user = new User(client);
            executorService.execute(user);
            System.out.println("用户" + user.getUsername() + "已连接");
            System.out.println(client.getInetAddress() + " -- " + client.getPort());
        }
        executorService.shutdown();
        Utils.closeAll(server);
    }

    // 服务端对客户端的管理
    private class User implements Runnable {
        // 用户名
        private String username;

        // 输入输出流
        private DataInputStream dis;
        private DataOutputStream dos;

        // 线程运行标志
        private boolean isRunning = true;

        // 用户初始化
        public User(Socket client) {
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                setUsername("");
            } catch (IOException e) {
//                e.printStackTrace();
                Utils.closeAll(dis, dos);
                isRunning = false;
            }
        }

        // 接收数据
        public String receive() {
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
//                e.printStackTrace();
                Utils.closeAll(dis);
                isRunning = false;
            }
            return msg;
        }

        // 分析并发送数据
        public void analysisAndSend(String msg) {
            // 用户登陆
            if (msg.startsWith("user")) {
                String username = msg.split(":")[1];
                login(username);
            }else if("exit".equals(msg)) {
                sendSys("【" + username + "】已下线");
            }else if (msg.startsWith("@")) {
                // 私聊
                String sendToWho = msg.split("@")[1];
                String[] tmp = sendToWho.split(":");
                sendToWho = tmp[0];
                msg = tmp[1];
                sendToWhichOne(msg, this.getUsername(), sendToWho);
            }else {
                sendToAll(msg, this.getUsername());
            }
        }

        // 用户登陆
        public void login(String username) {
            members.add(this);
            setUsername(username);

            // 系统消息：【username】已上线
            sendSys("【" + username + "】已上线");
        }

        // 发送系统消息
        public void sendSys(String msg) {
            for (User user : members) {
                user.send("系统消息：" + msg);
            }
        }

        // 发送世界消息
        public void sendToAll(String msg, String fromWhichOne) {
            for (User user : members) {
                user.send("【" + fromWhichOne + "】说：" + msg);
            }
        }

        // 发送私聊消息
        public void sendToWhichOne(String msg, String fromWhichOne, String toWhichOne) {
            for (User user : members) {
                if(toWhichOne.equals(user.getUsername())) {
                    user.send("【" + fromWhichOne + "】对你说：" + msg);
                    break;
                }
            }
        }

        // 发送给自己
        public void send(String msg) {
            if(null == msg || "".equals(msg)) {
                return;
            }
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
//                e.printStackTrace();
                Utils.closeAll(dos);
                isRunning = false;
            }

        }

        // 线程体
        @Override
        public void run() {
            while (isRunning) {
                String msg = receive();
                analysisAndSend(msg);
            }
        }

        // SettersAndGetters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
