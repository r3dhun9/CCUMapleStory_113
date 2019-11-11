package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import client.MapleClient;
import handling.world.World;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;
import tools.MaplePacketCreator;

public class AutobanManager implements Runnable {

    private static class ExpirationEntry implements Comparable<ExpirationEntry> {

        public long time;
        public int acc;
        public int points;

        public ExpirationEntry(long time, int acc, int points) {
            this.time = time;
            this.acc = acc;
            this.points = points;
        }

        @Override
        public int compareTo(AutobanManager.ExpirationEntry o) {
            return (int) (time - o.time);
        }

        @Override
        public boolean equals(Object oth) {
            if (!(oth instanceof AutobanManager.ExpirationEntry)) {
                return false;
            }
            final AutobanManager.ExpirationEntry ee = (AutobanManager.ExpirationEntry) oth;
            return (time == ee.time && points == ee.points && acc == ee.acc);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (int) (this.time ^ (this.time >>> 32));
            hash = 67 * hash + this.acc;
            hash = 67 * hash + this.points;
            return hash;
        }
    }
    private final Map<Integer, Integer> points = new HashMap<>();
    private final Map<Integer, List<String>> reasons = new HashMap<>();
    private final Set<ExpirationEntry> expirations = new TreeSet<>();
    private static final int AUTOBAN_POINTS = 5000;
    private static final AutobanManager instance = new AutobanManager();
    private final ReentrantLock lock = new ReentrantLock(true);

    public static final AutobanManager getInstance() {
        return instance;
    }

    public final void autoban(final MapleClient c, final String reason, int points) {
        if (c.getPlayer().isGM() || c.getPlayer().isClone()) {
            c.getPlayer().dropMessage(5, "[自動偵測系統] 已觸違規偵測 :" + reason);
            return;
        }
        addPoints(c, points, 0, reason);
    }

    public final void addPoints(final MapleClient c, final int points, final long expiration, final String reason) {
        lock.lock();
        try {
            List<String> reasonList;
            final int accountId = c.getPlayer().getAccountID();

            if (this.points.containsKey(accountId)) {
                final int lastPoints = this.points.get(accountId);
                this.points.put(accountId, lastPoints + points);
                reasonList = this.reasons.get(accountId);
                reasonList.add(reason);
            } else {
                this.points.put(accountId, points);
                reasonList = new LinkedList<>();
                reasonList.add(reason);
                this.reasons.put(accountId, reasonList);
            }

            if (this.points.get(accountId) >= AUTOBAN_POINTS) { // See if it's sufficient to auto ban
                
                if (c.getPlayer().isGM() || c.getPlayer().isClone()) {
                    c.getPlayer().dropMessage(5, "[自動封號系統] 觸發鎖定 : " + reason);
                    return;
                }
                
                final StringBuilder sb = new StringBuilder("[自動封號系統] ");
                sb.append("角色 : ");
                sb.append(c.getPlayer().getName());
                sb.append(" IP :");
                sb.append(c.getSession().getRemoteAddress().toString());
                for (final String s : reasons.get(accountId)) {
                    sb.append(s);
                    sb.append(", ");
                }
                World.Broadcast.broadcastMessage(MaplePacketCreator.getItemNotice("[自動偵測系統] 玩家" + c.getPlayer().getName() + "已遭到系統鎖定7天。呼籲其他玩家千萬不要開外掛，感謝！").getBytes());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 7);
		//c.getPlayer().tempban(sb.toString(), cal, 1, false);
                c.getPlayer().ban(sb.toString(), false, true, false);
                c.disconnect(true, false);
            } else {
                if (expiration > 0) {
                    expirations.add(new ExpirationEntry(System.currentTimeMillis() + expiration, accountId, points));
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final void run() {
        final long now = System.currentTimeMillis();
        for (final ExpirationEntry e : expirations) {
            if (e.time <= now) {
                this.points.put(e.acc, this.points.get(e.acc) - e.points);
            } else {
                return;
            }
        }
    }
}
