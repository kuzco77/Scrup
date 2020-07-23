package com.company.entity;

import com.company.facade.DBConnect;
import com.company.logger.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

public class Terminal {
    public int id;
    public String device_name, command;
    public Date created_date;

    public static ArrayList<Terminal> getTerminal(String device_name) {
        ArrayList<Terminal> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (!DBConnect.getConnection().isClosed()) {
                con = DBConnect.getConnection();
                ps = con.prepareStatement("call getOtherCommand(?)");

                ps.setString(1, device_name);
                rs = ps.executeQuery();

                while (rs.next()) {
                    Terminal config = new Terminal();
                    config.id = rs.getInt("id");
                    config.device_name = rs.getString("device_name");
                    config.command = rs.getString("command");

                    list.add(config);
                }


            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            try {
                Logger.log("Finishing getTerminal");
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
