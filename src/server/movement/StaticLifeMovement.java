/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.movement;

import java.awt.Point;
import tools.data.output.LittleEndianWriter;

/**
 *
 * @author user
 */
public class StaticLifeMovement extends AbstractLifeMovement {

    private Point pixelsPerSecond;
    private short unk, fh;
    private int wui;

    public StaticLifeMovement(int type, Point position, int duration, int newstate, int newfh) {
        super(type, position, duration, newstate, newfh);
    }

    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    public void setFh(short fh) {
        this.fh = fh;
    }

    public void setUnk(short unk) {
        this.unk = unk;
    }

    public short getUnk() {
        return unk;
    }

    public void setWui(int wui) {
        this.wui = wui;
    }

    public void defaulted() {
        unk = 0;
        fh = 0;
        pixelsPerSecond = new Point(0, 0);
        wui = 0;
    }

    @Override
    public void serialize(LittleEndianWriter lew) {
        lew.write(getType());
        switch (getType()) {
            case 0:
            case 5:
            case 15:
            case 17:
                lew.writePos(getPosition());
                lew.writePos(pixelsPerSecond);
                lew.writeShort(unk);
                if (getType() == 15 ) {
                    lew.writeShort(fh);
                }
                break;
            case 1:
            case 2:
            case 6:
            case 12:
            case 13:
            case 16:
            case 18:
            case 19:
            case 20:
            case 22:
                lew.writePos(pixelsPerSecond);
                break;
            case 3:
            case 4:
            case 7:
            case 8:
            case 9:
            case 11:
                lew.writePos(getPosition());
                lew.writeShort(unk);
                break;
            case 14:
                lew.writePos(pixelsPerSecond);
                lew.writeShort(fh);
                break;
        }
        if (getType() != 10) {
            lew.write(getNewstate());
            lew.writeShort(getDuration());
        } else {
            lew.write(wui);
        }
    }
}
