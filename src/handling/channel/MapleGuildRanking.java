/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.channel;

import java.util.List;
import java.util.LinkedList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.DatabaseConnection;

public class MapleGuildRanking {

    private static final MapleGuildRanking instance = new MapleGuildRanking();
    private final List<GuildRankingInfo> ranks = new LinkedList<>();
    private final List<levelRankingInfo> ranks1 = new LinkedList<>();
    private final List<mesoRankingInfo> ranks2 = new LinkedList<>();
    private final List<dpmRankingInfo> ranks3 = new LinkedList<>();

    public static MapleGuildRanking getInstance() {
        return instance;
    }

    public List<GuildRankingInfo> getGuildRank() {
        if (ranks.isEmpty()) {
            reload();
        }
        return ranks;
    }

    public List<levelRankingInfo> getLevelRank() {
        if (ranks1.isEmpty()) {
            showLevelRank();
        }
        return ranks1;
    }

    public List<mesoRankingInfo> getMesoRank() {
        if (ranks2.isEmpty()) {
            showMesoRank();
        }
        return ranks2;
    }

    public List<dpmRankingInfo> getDpmRank() {
        if (ranks3.isEmpty()) {
            showdpmRank();
        }
        return ranks3;
    }

    private void reload() {
        ranks.clear();

        Connection con = DatabaseConnection.getConnection();
        ResultSet rs;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds ORDER BY `GP` DESC LIMIT 50")) {
            rs = ps.executeQuery();
            while (rs.next()) {
                final GuildRankingInfo rank = new GuildRankingInfo(
                        rs.getString("name"),
                        rs.getInt("GP"),
                        rs.getInt("logo"),
                        rs.getInt("logoColor"),
                        rs.getInt("logoBG"),
                        rs.getInt("logoBGColor"));

                ranks.add(rank);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error handling guildRanking");
        }
    }

    private void showLevelRank() {
        ranks1.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE gm < 1 ORDER BY `level` DESC LIMIT 20");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final levelRankingInfo rank1 = new levelRankingInfo(
                        rs.getString("name"),
                        rs.getInt("level"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"));
                ranks1.add(rank1);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("未能顯示等級排行");
        }
    }

    private void showMesoRank() {
        ranks2.clear();

        Connection con = DatabaseConnection.getConnection();
        ResultSet rs;
        try (PreparedStatement ps = con.prepareStatement("SELECT *, ( chr.meso + s.meso ) as money FROM `characters` as chr , `storages` as s WHERE s.accountid = chr.accountid ORDER BY money DESC LIMIT 20")) {
            rs = ps.executeQuery();
            while (rs.next()) {
                final mesoRankingInfo rank2 = new mesoRankingInfo(
                        rs.getString("name"),
                        rs.getLong("money"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"));
                ranks2.add(rank2);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("未能顯示財產排行");
            e.printStackTrace();
        }
    }

    private void showdpmRank() {
        ranks3.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE gm < 1 ORDER BY `dps` DESC LIMIT 20");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final dpmRankingInfo rank3 = new dpmRankingInfo(
                        rs.getString("name"),
                        rs.getLong("dps"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"));
                ranks3.add(rank3);
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println("未能顯示等級排行");
        }
    }

    public static class mesoRankingInfo {

        private final String name;
        private final long meso;
        private final int str, dex, _int, luk;

        public mesoRankingInfo(String name, long meso, int str, int dex, int intt, int luk) {
            this.name = name;
            this.meso = meso;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
        }

        public String getName() {
            return name;
        }

        public long getMeso() {
            return meso;
        }

        public int getStr() {
            return str;
        }

        public int getDex() {
            return dex;
        }

        public int getInt() {
            return _int;
        }

        public int getLuk() {
            return luk;
        }
    }

    public static class levelRankingInfo {

        private final String name;
        private final int level, str, dex, _int, luk;

        public levelRankingInfo(String name, int level, int str, int dex, int intt, int luk) {
            this.name = name;
            this.level = level;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
        }

        public String getName() {
            return name;
        }

        public int getLevel() {
            return level;
        }

        public int getStr() {
            return str;
        }

        public int getDex() {
            return dex;
        }

        public int getInt() {
            return _int;
        }

        public int getLuk() {
            return luk;
        }
    }

    public static class dpmRankingInfo {

        private final String name;
        private final int str, dex, _int, luk;
        private final long dps;

        public dpmRankingInfo(String name, long dps, int str, int dex, int intt, int luk) {
            this.name = name;
            this.dps = dps;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
        }

        public String getName() {
            return name;
        }

        public long getdps() {
            return dps;
        }

        public int getStr() {
            return str;
        }

        public int getDex() {
            return dex;
        }

        public int getInt() {
            return _int;
        }

        public int getLuk() {
            return luk;
        }
    }

    public static class GuildRankingInfo {

        private final String name;
        private final int gp, logo, logocolor, logobg, logobgcolor;

        public GuildRankingInfo(String name, int gp, int logo, int logocolor, int logobg, int logobgcolor) {
            this.name = name;
            this.gp = gp;
            this.logo = logo;
            this.logocolor = logocolor;
            this.logobg = logobg;
            this.logobgcolor = logobgcolor;
        }

        public String getName() {
            return name;
        }

        public int getGP() {
            return gp;
        }

        public int getLogo() {
            return logo;
        }

        public int getLogoColor() {
            return logocolor;
        }

        public int getLogoBg() {
            return logobg;
        }

        public int getLogoBgColor() {
            return logobgcolor;
        }
    }
}
