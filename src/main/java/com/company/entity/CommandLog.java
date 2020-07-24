package com.company.entity;

import com.company.facade.DBConnect;
import com.company.logger.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

public class CommandLog {
    public int id;
    public String user_agent, content;
    public Date reg_date;


    public static void add2SQL(List<CommandLog> listObjects) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (!DBConnect.getConnection().isClosed()) {
                con = DBConnect.getConnection();
                ps = con.prepareStatement("call addLog(?,?,?)");
                int i = 0;
                for (CommandLog object : listObjects) {
                    Logger.log("add line " + i + " to bach");

                    ps.setString(1, object.user_agent);
                    ps.setString(2, object.content);
                    ps.setString(3, "net config");
                    ps.addBatch();
                    i++;
                    if (i % 10000 == 0 || i == listObjects.size()) {
                        Logger.log("execute batch in add2SQL");
                        Logger.log("=======================================");
                        ps.executeBatch();
                        Logger.log("======= END =======");
                    }
                }

            }
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            try {
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
    }
}
