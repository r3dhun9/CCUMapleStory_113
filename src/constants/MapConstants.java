package constants;

public class MapConstants {

    public static boolean isBlockFM(final int mapid) {
        int header = mapid / 100000;
        if (isEventMap(mapid)) {
            return true;
        }
        if (header == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100)) {
            return true;
        }
        if (mapid / 10000 == 92502) {
            return true;
        }
        if (header == 7090) {
            return true;
        }
        if (header == 1090) {
            return true;
        }
        switch (mapid) {
            case 702060000:
                return true;
            default:
                return false;
        }
    }

    public static boolean isCar(final int mapid) {
        switch (mapid) {
            case 980000000:
            case 980030000:
                return true;
        }
        return false;
    }

    public static boolean CanUseDropCard(final int mapid) {
        switch (mapid) {
            case 100040101:
            case 100040102:
            case 100040103:
            case 100040104:
            case 107000401:
            case 107000402:
            case 107000403:
            case 191000000:
                return true;
        }
        return false;
    }

    public static boolean CanUseDropCard1(final int mapid) {
        switch (mapid) {
            case 251010000:
            case 251010100:
            case 251010101:
                return true;
        }
        return false;
    }

    public static boolean CanUseDropCard2(final int mapid) {
        switch (mapid) {
            case 222020100:
            case 222020200:
            case 222020300:
                return true;
        }
        return false;
    }

    public static boolean CanUseMesoCard(final int mapid) {
        switch (mapid) {
            case 221020100:
            case 221020200:
            case 221020400:
            case 221020300:
            case 221023700:
            case 221023800:
            case 221023900:
            case 221024000:
            case 221024100:
            case 221024200:
                return true;
        }
        return false;
    }

    public static boolean isStartingEventMap(final int mapid) {
        switch (mapid) {
            case 109010000:
            case 109020001:
            case 109030001:
            case 109030101:
            case 109030201:
            case 109030301:
            case 109030401:
            case 109040000:
            case 109060001:
            case 109060002:
            case 109060003:
            case 109060004:
            case 109060005:
            case 109060006:
            case 109080000:
            case 109080001:
            case 109080002:
            case 109080003:
                return true;
        }
        return false;
    }

    public static boolean isBlackFM(final int mapid) {
        return mapid >= 910000001 && mapid < 910000022;
    }

    public static boolean isEventMap(final int mapid) {
        return mapid >= 109010000 && mapid < 109050000 || mapid > 109050001 && mapid < 109090000;
    }
}
