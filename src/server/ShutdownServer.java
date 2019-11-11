package server;

import database.DatabaseConnection;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.World;
import java.util.Set;
import server.Timer.*;

public class ShutdownServer implements Runnable, ShutdownServerMBean {

    private static final ShutdownServer instance = new ShutdownServer();
    public static boolean running = false;

    public static ShutdownServer getInstance() {
        return instance;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (running) { //Run once!
                return;
            }
            running = true;
        }
        World.isShutDown = true;
        EventTimer.getInstance().stop();
        WorldTimer.getInstance().stop();
        MapTimer.getInstance().stop();
        MobTimer.getInstance().stop();
        BuffTimer.getInstance().stop();
        BoatTimer.getInstance().stop();
        CloneTimer.getInstance().stop();
        EtcTimer.getInstance().stop();
        System.out.println("Timer 關閉完成");

        for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
            cserv.closeAllMerchant();
        }
        System.out.println("精靈商人儲存完畢.");
        int ret = 0;
        for (handling.channel.ChannelServer cserv : handling.channel.ChannelServer.getAllInstances()) {
            ret += cserv.closeAllPlayerShop();
        }
        System.out.println("共儲存了 " + ret + " 個營業執照");
        World.Guild.save();

        System.out.println("公會資料儲存完畢");

        World.Alliance.save();

        System.out.println("聯盟資料儲存完畢");

        World.Family.save();

        System.out.println("家族資料儲存完畢");

        Set<Integer> channels = ChannelServer.getAllChannels();

        for (Integer channel : channels) {
            try {
                ChannelServer cs = ChannelServer.getInstance(channel);
                cs.saveAll();
                cs.setPrepareShutdown();
                cs.shutdown();
            } catch (Exception e) {
                System.out.println("頻道" + String.valueOf(channel) + " 關閉失敗.");
            }
        }
        try {
            LoginServer.shutdown();
            System.out.println("登陸伺服器關閉完成.");
        } catch (Exception e) {
            System.out.println("登陸伺服器關閉失敗");
        }
        try {
            CashShopServer.shutdown();
            System.out.println("購物商城伺服器關閉完成.");
        } catch (Exception e) {
            System.out.println("購物商城伺服器關閉失敗");
        }

        try {
            DatabaseConnection.closeAll();
            System.out.println("資料庫清除連線完成");
        } catch (Exception e) {
            System.out.println("資料庫清除連線失敗");
        }

    }

    @Override
    public void shutdown() {
        this.run();
    }
}
