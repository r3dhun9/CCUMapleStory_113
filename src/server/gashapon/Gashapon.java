/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.gashapon;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Randomizer;
import tools.FilePrinter;
import tools.Pair;

/**
 *
 * @author user
 */
public final class Gashapon {

    private final int id;
    private final int npcId;
    private final String name;
    private final List<Pair<Long, GashaponReward>> items = new LinkedList<>();

    public Gashapon(final int id, final int npcId, final String name) {
        this.id = id;
        this.npcId = npcId;
        this.name = name;
        this.reloadItems();
    }

    public int getId() {
        return this.id;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public String getName() {
        return this.name;
    }

    public GashaponReward generateReward() {
        if (this.items.isEmpty()) {
            this.reloadItems();
        }
        Iterator<Pair<Long, GashaponReward>> iterator = this.items.iterator();

        long total = items.get(items.size() - 1).left;

        Long n = Math.abs(Randomizer.nextLong() * System.currentTimeMillis() + 47 * System.currentTimeMillis()) % total;
        
        while (iterator.hasNext()) {
            Pair<Long, GashaponReward> c = iterator.next();
            if (n <= c.left) {
                return c.right;
            }
        }
        
        return null;
    }

    public void reloadItems() {

        Connection con = DatabaseConnection.getConnection();
        long chanceTotal = 0L;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM gashapon_items WHERE gashaponsid = ? ORDER BY chance ASC")) {
            ps.setInt(1, getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GashaponReward reward = new GashaponReward(rs.getInt("itemid"), rs.getInt("chance"), rs.getBoolean("showmsg"));
                chanceTotal += reward.getChance();
                this.items.add(new Pair<>(chanceTotal, reward));
            }
        } catch (SQLException ex) {
            FilePrinter.printError("Gashapon.txt", ex, "reloadItems");
        }

    }
}
