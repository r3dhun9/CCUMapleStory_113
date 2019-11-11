package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import handling.MaplePacket;
import handling.SendPacketOpcode;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.PachinkoPacket;


/**
 * Created by Toby on 2016/8/31.
 */
public class PachinkoHandler {


    public static final void handlePachinkoGame(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        byte type = slea.readByte();
        if (type == 0) {

        } else if (type == 1) {

        } else if (type == 2) {

        } else if (type == 3) {
            if (chr.pachinkoLight < 7)
                ++chr.pachinkoLight;
            c.sendPacket(PachinkoPacket.setLightLevel(chr.pachinkoLight));
        } else if (type == 4) { // TODO: 中獎演算法
            if (chr.pachinkoLight != 0) {
                --chr.pachinkoLight;
                c.sendPacket(PachinkoPacket.spinTest(chr.pachinkoLight));
            }
        } else if (type == 5) {
            chr.getMap().broadcastMessage(PachinkoPacket.marqueeMessage(chr.getName()));
            c.sendPacket(PachinkoPacket.rewardBalls(2000));
            chr.gainBalls(2000);
        } else if (type == 6) {
            slea.readByte();
            int count = slea.readByte();
            if (chr.pachinkoOpenStage != 0)
                chr.gainBalls(-count);
        } else if (type == 7) {
            slea.readInt();
            int now = slea.readInt();
            if (chr.pachinkoOpenStage == 0)
                chr.pachinkoOpenTime = now;
            if (now - chr.pachinkoOpenTime > 10000) {
                c.sendPacket(PachinkoPacket.rewardBalls(0, 5));
                chr.pachinkoOpenStage = 0;
            } else if (now - chr.pachinkoOpenTime > 5000) {
                chr.pachinkoOpenStage = 4;
                c.sendPacket(PachinkoPacket.rewardBalls(100, 4));
                chr.gainBalls(100);
            } else {
                chr.pachinkoOpenStage = 1;
                c.sendPacket(PachinkoPacket.rewardBalls(100, 1));
                chr.gainBalls(100);
            }
        }
    }

    public static final void handlePachinkoExit(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        chr.pachinkoLight = 0;
        chr.pachinkoOpenStage = 0;
        chr.pachinkoOpenTime = 0;
        c.sendPacket(PachinkoPacket.exitPachinko());
    }
}
