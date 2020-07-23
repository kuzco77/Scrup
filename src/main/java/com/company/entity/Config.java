package com.company.entity;

import com.company.facade.DBConnect;
import com.company.logger.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Config {
    public int id;
    public String device_name, setting, service_name;
    public boolean status;

    public static ArrayList<Config> getConfig(String device_name) {
        ArrayList<Config> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (!DBConnect.getConnection().isClosed()) {
                con = DBConnect.getConnection();
                ps = con.prepareStatement("call getVPNConfig(?)");

                ps.setString(1, device_name);
                rs = ps.executeQuery();

                while (rs.next()) {
                    Config config = new Config();
                    config.id = rs.getInt("id");
                    config.device_name = rs.getString("device_name");
                    config.setting = rs.getString("setting");
                    config.service_name = rs.getString("service_name");
                    config.status = rs.getBoolean("status");

                    list.add(config);
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
