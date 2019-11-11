/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.MapleCharacter;
import java.lang.ref.WeakReference;
import tools.FilePrinter;

/**
 *
 * @author ms890110
 */
public class AutoGiveCSPoints {

    private long lasttime = 0;
    private final long period = 1000 * 60 * 60;
    private WeakReference<MapleCharacter> chr = null;

    public AutoGiveCSPoints(MapleCharacter chr) {
        this.chr = new WeakReference<>(chr);
    }

    public AutoGiveCSPoints(MapleCharacter chr, long lasttime) {
        this.chr = new WeakReference<>(chr);
        this.lasttime = lasttime;
    }
    
    public long getLasttime()
    {
        return this.lasttime;
    }

    public boolean checkTime() {
        return (System.currentTimeMillis() - lasttime) > period;
    }

    public void checkGivePoints() {
       
    }

}
