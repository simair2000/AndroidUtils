package com.simair.android.androidutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by simair on 17. 6. 12.
 */

public class AdbUtil {

    private volatile static AdbUtil instance;
    private final Runtime runtime;

    public static AdbUtil getInstance() {
        if(instance == null) {
            synchronized (AdbUtil.class) {
                instance = new AdbUtil();
            }
        }
        return instance;
    }

    private AdbUtil() {
        runtime = Runtime.getRuntime();
    }

    public String execute(String command) {
        StringBuffer output = new StringBuffer();
        Process p = null;
        try {
            p = runtime.exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(p != null) {
                p.destroy();
            }
        }
        return output.toString();
    }
}
