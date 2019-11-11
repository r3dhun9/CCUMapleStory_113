package server;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import constants.GameConstants;

public class RandomRewards {

    private final static RandomRewards instance = new RandomRewards();
    private List<Integer> 金寶箱獎勵 = null;
    private List<Integer> 銀寶箱獎勵 = null;
    private List<Integer> compiledFishing = null;
    private List<Integer> 活動獎勵大獎 = null;
    private List<Integer> 活動普通獎勵 = null;
    private List<Integer> 活動獎勵三獎 = null;
    private List<Integer> 活動獎勵二獎 = null;

    public static RandomRewards getInstance() {
        return instance;
    }

    protected RandomRewards() {
        System.out.println("【讀取中】 RandomRewards :::");
        // Gold Box
        List<Integer> returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.goldrewards);

        金寶箱獎勵 = returnArray;

        // Silver Box
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.silverrewards);

        銀寶箱獎勵 = returnArray;

        // Fishing Rewards
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.fishingReward);

        compiledFishing = returnArray;

        // Event Rewards
        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.活動普獎);

        活動普通獎勵 = returnArray;

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.活動三獎);

        活動獎勵三獎 = returnArray;

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.活動二獎);

        活動獎勵二獎 = returnArray;

        returnArray = new ArrayList<>();

        processRewards(returnArray, GameConstants.活動大獎);

        活動獎勵大獎 = returnArray;
    }

    private void processRewards(final List<Integer> returnArray, final int[] list) {
        int lastitem = 0;
        for (int i = 0; i < list.length; i++) {
            if (i % 2 == 0) { // Even
                lastitem = list[i];
            } else { // Odd
                for (int j = 0; j < list[i]; j++) {
                    returnArray.add(lastitem);
                }
            }
        }
        Collections.shuffle(returnArray);
    }

    public final int getGoldBoxReward() {
        return 金寶箱獎勵.get(Randomizer.nextInt(金寶箱獎勵.size()));
    }

    public final int getSilverBoxReward() {
        return 銀寶箱獎勵.get(Randomizer.nextInt(銀寶箱獎勵.size()));
    }

    public final int getFishingReward() {
        return compiledFishing.get(Randomizer.nextInt(compiledFishing.size()));
    }

    public final int getEventReward() {
        final int chance = Randomizer.nextInt(100);
        if (chance < 45) {
            return 活動普通獎勵.get(Randomizer.nextInt(活動普通獎勵.size()));
        } else if (chance < 80) {
            return 活動獎勵三獎.get(Randomizer.nextInt(活動獎勵三獎.size()));
        } else if (chance < 95) {
            return 活動獎勵二獎.get(Randomizer.nextInt(活動獎勵二獎.size()));
        } else {
            return 活動獎勵大獎.get(Randomizer.nextInt(活動獎勵大獎.size()));
        }
    }
}
