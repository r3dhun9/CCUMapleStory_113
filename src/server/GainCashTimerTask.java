/*
 重置所有帳號領取每日點數
 Author: Redhung
 */

package server;

import java.util.*;
import client.MapleCharacter;

public class GainCashTimerTask extends TimerTask {
    @Override
    public void run() {
        try {
            MapleCharacter chr = new MapleCharacter(false);
            chr.setAllGainCashBack();
        }
        catch (Exception e) {
            System.out.println("重置每日領取點數錯誤");
        }
    }
}
