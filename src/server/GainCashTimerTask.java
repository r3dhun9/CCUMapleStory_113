/*
 重置所有帳號領取每日點數
 Author: Redhung
 */

package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import database.DatabaseConnection;

public class GainCashTimerTask extends TimerTask {
    @Override
    public void run() {
        Connection conn = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement("UPDATE accounts SET gaincash=0");
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            System.out.println("重置每日領取點數錯誤");
        }
    }
}
