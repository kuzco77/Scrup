package com.company;

import com.company.entity.DefaultConfig;
import com.company.entity.CommandLog;
import com.company.entity.Terminal;
import com.company.logger.Logger;


import java.io.BufferedReader;
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
        ArrayList<Terminal> list = Terminal.getTerminal(device_name);
        for (Terminal ter : list) {
            String[] cmd = {"/bin/sh", "-c", ter.command};
            String result = executeTerminal(cmd);
            if (!result.isEmpty()) {
                ArrayList<CommandLog> lcl = new ArrayList<>();
                CommandLog cl = new CommandLog();
                cl.content = result;
                cl.user_agent = device_name;
                lcl.add(cl);
                CommandLog.add2SQL(lcl);
            }
        }
    }

    private static void turnOnVPN(String device_name) throws Exception {
        ArrayList<DefaultConfig> listConf = DefaultConfig.getVPNConfig(device_name, DefaultConfig.DEFAULT_CONFIG.VPN);
        boolean status = false;
        String service_name = "GHDC VPN";
        for (DefaultConfig conf : listConf) {
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

    private static void updateIP4Mac(String device_name) throws Exception {
        ArrayList<DefaultConfig> dcs = DefaultConfig.getVPNConfig(device_name, DefaultConfig.DEFAULT_CONFIG.netConfig);
        String[] cmd = new String[0];
        if (dcs.size() == 0) {
            cmd = new String[] {"/bin/sh", "-c", "/sbin/ifconfig"};
        } else {
            for (DefaultConfig dc: dcs) {
                String realCommand = dc.command;
                cmd = new String[] {"/bin/sh", "-c", realCommand};
            }
        }

        String result = executeTerminal(cmd);

        CommandLog config = new CommandLog();
        config.user_agent = device_name;
        config.content = result;
        ArrayList<CommandLog> list = new ArrayList<>();
        list.add(config);
        CommandLog.add2SQL(list);

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
