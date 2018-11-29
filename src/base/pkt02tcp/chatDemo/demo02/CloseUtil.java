/**
 * @Date 2018/11/27
 * @Author LZD
 */
package base.pkt02tcp.chatDemo.demo02;

import java.io.Closeable;
import java.io.IOException;

/*
    关闭流的方法
 */
public class CloseUtil {
    public static void CloseAll(Closeable... io) {
        for (Closeable tmp : io) {
            try {
                if(null != tmp) {
                    tmp.close();
                }
            } catch (IOException e) {
//                    e.printStackTrace();
            }
        }
    }
}
