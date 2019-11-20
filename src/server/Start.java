package server;

import client.SkillFactory;
import client.messages.ConsoleCommandProcessor;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import static database.DatabaseConnection.CloseSQLConnections;
import handling.world.family.MapleFamilyBuff;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.gashapon.GashaponFactory;
import server.life.MapleLifeFactory;
import server.quest.MapleQuest;

public class Start {

    private static void resetAllLoginState() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("【錯誤】 請確認資料庫是否正確連接");
        }
    }

    public final static void main(final String args[]) {

        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        System.setProperty("file.encoding", "utf-8");

        
        System.out.println("【台版楓之谷模擬器】");
        System.out.println("【版本】 v113");

        boolean adminMode = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.admin"));
        boolean autoReg = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.autoRegister"));
        boolean gmitems = Boolean.parseBoolean(ServerProperties.getProperty("server.settings.gmitems"));
        

        if (adminMode) {
            System.out.println("【管理員模式】開啟");
        } else {
            System.out.println("【管理員模式】關閉");
        }

        if (autoReg) {
            System.out.println("【自動註冊】開啟");
        } else {
            System.out.println("【自動註冊】關閉");
        }
        
        if (gmitems) {
            System.out.println("【允許玩家使用管理員物品】開啟");
        } else {
            System.out.println("【允許玩家使用管理員物品】關閉");
        }

        /*重置玩家登入(解卡)*/
        resetAllLoginState();

        /*載入楓之谷*/
        World.init();

        /*載入各項計時器*/
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        BoatTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();

        /*載入WZ內禁止名單*/
        LoginInformationProvider.getInstance();

        /*載入釣魚*/
        FishingRewardFactory.getInstance();

        /*載入任務*/
        MapleQuest.initQuests();

        /*載入怪物、NPC*/
        MapleLifeFactory.loadQuestCounts();

        /*載入轉蛋機NPC*/
        GashaponFactory.getInstance().reloadGashapons();

        /*Unknown*/
        //ItemMakerFactory.getInstance();

        /*載入裝備、道具*/
        MapleItemInformationProvider.getInstance().load();

        /*載入金銀寶箱、活動獎勵*/
        RandomRewards.getInstance();

        /*載入技能*/
        SkillFactory.getSkill(99999999);

        /*載入圈叉題目*/
        MapleOxQuizFactory.getInstance().initialize();

        /*載入怪物擂台*/
        MapleCarnivalFactory.getInstance();

        /*載入薇薇安*/
        PredictCardFactory.getInstance().initialize();

        /*載入公會排名*/
        MapleGuildRanking.getInstance().getGuildRank();

        /*載入職業排名*/
        RankingWorker.getInstance().run();

        /*載入遠征排名*/
        SpeedRunner.getInstance().loadSpeedRuns();

        /*載入家族Buff*/
        MapleFamilyBuff.getBuffEntry();

        /*MbeanServer*/
        MapleServerHandler.registerMBean();
        
        /*載入拍賣*/
        // MTSStorage.load();

        /*載入每日點數重置定時器*/
        new GainCashTimer();

        /*載入購物商城物品*/
        CashItemFactory.getInstance().initialize();

        /*載入登入伺服器*/
        LoginServer.setup();

        /*載入頻道、活動*/
        ChannelServer.startAllChannels();

        /*載入購物商城*/
        System.out.println("【啟動中】 CashShop Items:::");
        CashShopServer.setup();
        
        /*載入偵測封鎖系統*/
        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        
        /*載入程式關閉時的ShutdownHook*/
        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownServer.getInstance()));

        /*註冊怪物重生時間、技能CD、寵物CD*/
        World.registerRespawn();

        /*登入伺服器設置On*/
        LoginServer.setOn();
        System.out.println("【伺服器開啟完畢】");

        /*從Console執行Command*/
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        while (!World.isShutDown) {
            try {
                System.out.print(">>");
                String cmd = br.readLine();
                if (cmd.equals("")) {
                    continue;
                }
                ConsoleCommandProcessor.processCommand(cmd);
            } catch (IOException ex) {
                //Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
            } catch(Exception ex) {
                
            }
        }
    }

}
