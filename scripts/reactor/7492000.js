function act() {

    rm.showEffect(true, "englishSchool/correct");
    if (!rm.haveItem(1302085)) {
        rm.gainItem(1302085, 1);
    }
    rm.spawnNpc(9330079);
    rm.killAll();

}
