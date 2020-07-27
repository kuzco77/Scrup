package com.company.entity;

import com.company.facade.DBConnect;
import com.company.logger.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;



public class DefaultConfig {
    public int id;
    public String device_name, setting, service_name, command;
    public boolean status;

    public enum DEFAULT_CONFIG {
        VPN,
        netConfig
    }

    public static ArrayList<DefaultConfig> getVPNConfig(String device_name, DEFAULT_CONFIG setting) {
        ArrayList<DefaultConfig> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            if (!DBConnect.getConnection().isClosed()) {
                con = DBConnect.getConnection();
                switch (setting) {
                    case VPN: ps = con.prepareStatement("call getVPNConfig(?)"); break;
                    case netConfig: ps = con.prepareStatement("call getNetConfig(?)"); break;
                }

                ps.setString(1, device_name);
                rs = ps.executeQuery();

                while (rs.next()) {
                    DefaultConfig defaultConfig = new DefaultConfig();
                    defaultConfig.id = rs.getInt("id");
                    defaultConfig.device_name = rs.getString("device_name");
                    defaultConfig.setting = rs.getString("setting");
                    defaultConfig.service_name = rs.getString("service_name");
                    defaultConfig.status = rs.getBoolean("status");
                    defaultConfig.command = rs.getString("command");

                    list.add(defaultConfig);
                }



            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            try {
                Logger.log("Finishing getConfig");
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        }

        return list;
    }
}
