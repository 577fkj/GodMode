package tiiehenry.viewcontroller.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class CommandUtil {


    /**
     * @param: [command]
     * @return: java.lang.String
     */
    public static Process su() throws IOException {
            //等待命令执行完成
        return Runtime.getRuntime().exec("su");
    }

    public static void run(Process process, String command) throws IOException {
        PrintStream outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
        outputStream.println(command);
        outputStream.flush();
        outputStream.close();
    }

}