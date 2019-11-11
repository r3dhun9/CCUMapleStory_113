/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.life;

import database.DatabaseConnection;
import handling.channel.ChannelServer;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.maps.MapleMap;
import tools.FilePrinter;

/**
 *
 * @author user
 */
public class CustomNPC extends MapleNPC {

    private int customNpcId;
    private int mapId;
    private int channel;

    public CustomNPC(MapleNPC npc, final int mapId, final int channel) {
        super(npc.getId(), npc.getName());
    }

    public CustomNPC(final int npcid, final String name, final int mapId, final int channel) {
        super(npcid, name);
        this.mapId = mapId;
        this.channel = channel;
        this.setCustom(true);
    }

    public void deleteFromDB() {
        deleteFromDB(this.getId(), getMapId(), getChannel());
    }

    public static void deleteFromDB(final int npcId, final int mapId, final int channel) {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM custom_npcs WHERE npcId = ? and map = ? and channel = ?")) {
            ps.setInt(1, npcId);
            ps.setInt(2, mapId);
            ps.setInt(3, channel);
            ps.execute();
        } catch (SQLException ex) {
            FilePrinter.printError("CustomNPC.txt", ex, "deleteFromDB");
        }
    }

    public void loadFromDB() {
        CustomNPC ret = loadFromDB(getId(), getMapId(), getChannel());
        this.setPosition(ret.getPosition());
        this.setFh(ret.getFh());
        this.setCy(ret.getCy());
        this.setRx0(ret.getRx0());
        this.setRx1(ret.getRx1());
        this.setCustom(true);
    }
    
    public static CustomNPC loadFromDB(final int npcId, final int mapId, final int channel) {

        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM custom_npcs WHERE npcId = ? and map = ? and channel = ?")) {
            ps.setInt(1, npcId);
            ps.setInt(2, mapId);
            ps.setInt(3, channel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CustomNPC ret = new CustomNPC(rs.getInt("npcId"), rs.getString("name"), rs.getInt("map"), rs.getInt("channel"));
                    ret.setCustom(true);
                    Point pos = new Point(rs.getInt("x"), rs.getInt("y"));
                    ret.setPosition(pos);
                    ret.setCy((int) ret.getPosition().getY());
                    ret.setFh(rs.getInt("foothold"));
                    ret.setRx0((int) (pos.getX() + 50));
                    ret.setRx1((int) (pos.getX() - 50));
                    return ret;
                }
            }
        } catch (SQLException ex) {
            FilePrinter.printError("CustomNPC.txt", ex, "loadFromDB");
            return null;
        }
        return null;
    }

    public static List<CustomNPC> loadAll(int mapId, int channel) {
        List<CustomNPC> ret = new ArrayList<>();
        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM custom_npcs WHERE channel = ? and map = ?")) {
            ps.setInt(1, channel);
            ps.setInt(2, mapId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomNPC npc = new CustomNPC(rs.getInt("npcId"), rs.getString("name"), rs.getInt("map"), rs.getInt("channel"));
                    npc.setCustom(true);
                    Point pos = new Point(rs.getInt("x"), rs.getInt("y"));
                    npc.setPosition(pos);
                    npc.setCy((int) pos.getY());
                    npc.setFh(rs.getInt("foothold"));
                    npc.setRx0((int) (pos.getX() + 50));
                    npc.setRx1((int) (pos.getX() - 50));
                    ret.add(npc);
                }
            }
        } catch (SQLException ex) {
            FilePrinter.printError("CustomNPC.txt", ex, "loadFromDB");
        }
        return ret;
    }

    public void saveToDB() {
        Connection con = DatabaseConnection.getConnection();
        this.deleteFromDB();
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO custom_npcs(npcId, name, x, y, map, channel, foothold) values ( ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, getId());
            ps.setString(2, getName());
            ps.setInt(3, (int) this.getPosition().getX());
            ps.setInt(4, (int) this.getPosition().getY());
            ps.setInt(5, getMapId());
            ps.setInt(6, getChannel());
            ps.setInt(7, getFh());
            ps.execute();
        } catch (SQLException ex) {
            FilePrinter.printError("CustomNPC.txt", ex, "saveToDB");
        }
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(final int channel) {
        this.channel = channel;
    }

    public int getMapId() {
        return this.mapId;
    }

    public void setMapId(final int mapId) {
        this.mapId = mapId;
    }

}
