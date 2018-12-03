/**
 * @Date 2018/11/30
 * @Author LZD
 *
 * 工具类
 */
package base.pkt02tcp.chatDemo.demo03;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;

public class Utils {
    // 关闭指定流
    public static void closeAll(Closeable... io) {
        for (Closeable tmp : io) {
            try {
                tmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 从控制台获取信息
    public static String getInputFromConsole(BufferedReader console) {
        String msg = "";
        try {
            msg = console.readLine();
        } catch (IOException e) {
//            e.printStackTrace();
            closeAll(console);
            System.out.println("输入流出现异常");
        }

        if(null == msg || "".equals(msg))
            return null;
        return msg;
    }
}
