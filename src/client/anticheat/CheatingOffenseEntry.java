package client.anticheat;

public class CheatingOffenseEntry {

    private final CheatingOffense offense;
    private final long firstOffense;
    private final int characterid;
    private int count = 0;

    private long lastOffense;

    private String param;


    public CheatingOffenseEntry(CheatingOffense offense, int characterid) {
        super();
        this.offense = offense;
        this.characterid = characterid;
        firstOffense = System.currentTimeMillis();
    }

    public CheatingOffense getOffense() {
        return offense;
    }

    public int getCount() {
        return count;
    }

    public int getChrfor() {
        return characterid;
    }

    public void incrementCount() {
        this.count++;
        lastOffense = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return lastOffense < (System.currentTimeMillis() - offense.getValidityDuration());
    }

    public int getPoints() {
        return count * offense.getPoints();
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public long getLastOffenseTime() {
        return lastOffense;
    }

}
