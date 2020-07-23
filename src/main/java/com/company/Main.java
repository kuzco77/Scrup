package com.company;

import com.company.entity.Config;
import com.company.entity.IFConfig;
import com.company.entity.Terminal;
import com.company.logger.Logger;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();

            System.out.println(hostname);

            turnOnVPN(hostname);
            updateIP4Mac(hostname);
            otherCommand(hostname);
        } catch (Exception e) {
            Logger.error(e);
        }

    }

    private static void otherCommand(String device_name) throws Exception {
        ArrayList<Terminal> list = new ArrayList<>();
        list = Terminal.getTerminal(device_name);
        for (Terminal ter: list) {
            String[] cmd = {"/bin/sh", "-c", ter.command};
            String result = executeTerminal(cmd);
            if (result.isEmpty()) {

            }
        }
    }

    private static void turnOnVPN(String device_name) throws Exception {
        ArrayList<Config> listConf = new ArrayList<>();
        listConf = Config.getConfig(device_name);
        boolean status = false;
        String service_name = "GHDC VPN";
        for (Config conf: listConf) {
            status = conf.status;
            service_name = conf.service_name;
        }

        if (status == true) {
            String[] cmd = {"/bin/sh", "-c", "/usr/sbin/networksetup -connectpppoeservice \'" + service_name + "\'"};
            executeTerminal(cmd);
        } else {
            String[] cmd = {"/bin/sh", "-c", "/usr/sbin/networksetup -disconnectpppoeservice \'" + service_name + "\'"};
            executeTerminal(cmd);
        }



    }

    private static void updateIP4Mac(String device_name) {
        try {
            String[] cmd = {"/bin/sh", "-c", "/sbin/ifconfig"};
            String result = executeTerminal(cmd);

            IFConfig config = new IFConfig();
            config.user_agent = device_name;
            config.content = result;
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
    }

    private static String executeTerminal(String[] command) throws Exception {
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = reader.readLine();
        String result = "";
        while (line != null) {
            result = result + line + System.lineSeparator();
            line = reader.readLine();
        }

        return result;
    }

}
