package com.company;

import com.company.entity.IFConfig;
import com.company.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
	// write your code here
        try {
            System.out.println("start");
            Process p = Runtime.getRuntime().exec("/sbin/ifconfig");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = reader.readLine();
            String content = "";
            while (line != null) {
                content = content + line + System.lineSeparator();
                line = reader.readLine();
                Logger.log(line);
            }

            System.out.println(reader.toString());

            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();

            System.out.println(hostname);

            IFConfig config = new IFConfig();
            config.user_agent = hostname;
            config.content = content;
            ArrayList<IFConfig> list = new ArrayList<>();
            list.add(config);
            IFConfig.add2SQL(list);
        }
        catch(IOException e1) {
            Logger.error(e1);
        }

        catch(InterruptedException e2) {
            Logger.error(e2);
        }

        catch (Exception e3) {
            Logger.error(e3);
        }

        System.out.println("finished.");
    }
}
