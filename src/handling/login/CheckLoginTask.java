/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login;

import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.Pair;

/**
 *
 * @author Tasi
 */
public class CheckLoginTask implements Runnable {

    private static CheckLoginTask INSTANCE = null;

    public static CheckLoginTask getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CheckLoginTask();
        }
        return INSTANCE;
    }

    private List<String> getAccountsOnlineFromDB() {
        List<String> ret = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();

        try {
            PreparedStatement ps = con.prepareStatement("SELECT name from accounts where loggedin > 0");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ret.add(rs.getString("name"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(CheckLoginTask.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    private Map<String, Pair<Integer, Integer>> getAccountsOnlineAndPing() {
        Map<String, Pair<Integer, Integer>> ret = new HashMap<>();
        for (ChannelServer channel : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : channel.getPlayerStorage().getAllCharactersThreadSafe()) {
                ret.put(player.getClient().getAccountName(), new Pair<>(player.getClient().getChannel(), player.getId()));
                player.getClient().sendPing();
            }
        }
        for (MapleCharacter player : CashShopServer.getPlayerStorage().getAllCharactersThreadSafe()) {
            ret.put(player.getClient().getAccountName(), new Pair<>(player.getClient().getChannel(), player.getId()));
            player.getClient().sendPing();
        }
        return ret;
    }

    private void updateAccountsState(String account) {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE name = ?")) {
            ps.setString(1, account);
            ps.execute();
        } catch (SQLException e) {
            System.out.println("[CheckLoginTask] error updating login state" + e);
        }
    }

    @Override
    public void run() {

        List<String> dbOnlines = this.getAccountsOnlineFromDB();
        Map<String, Pair<Integer, Integer>> onlines = this.getAccountsOnlineAndPing();

        for (String accOnline : onlines.keySet()) {
            if (!dbOnlines.contains(accOnline)) {
                updateAccountsState(accOnline);
            }
        }

    }

}
