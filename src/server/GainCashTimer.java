/*
 重置所有帳號領取每日點數
 Author: Redhung
 */

package server;

import java.util.*;

public class GainCashTimer {

    java.util.Timer timer;

    public GainCashTimer() {
        Date time = getTime();
        System.out.println("【讀取中】 重置所有帳號領取每日點數時間: " + time + " :::");
        timer = new java.util.Timer();
        timer.schedule(new GainCashTimerTask(), time);
    }

    public Date getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 55);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        return time;
    }
}
