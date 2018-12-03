/**
 * @Date 2018/12/01
 * @Author LZD
 *
 * 客户端
 */
package base.pkt02tcp.chatDemo.demo03;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("127.0.0.1", 9999);

        new Thread(new SendToServer(client)).start();
        new Thread(new ReceiveFromServer(client)).start();
    }
}
