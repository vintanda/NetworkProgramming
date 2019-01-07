package nio.pkt03aioTest;

/**
 * @author lzd
 * @date 2019/01/05
 *
 * 客户端运行代码入口
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        try {
            port = Integer.valueOf(args[0]);
        } catch (Exception e) {
            port = 8080;
        }

        new Thread(new AsyncTimeClientHandler("127.0.0.1", port),
                "AIO-AsyncTimeClientHandler-001").start();
    }
}
