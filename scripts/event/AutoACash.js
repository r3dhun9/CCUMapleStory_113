var setupTask;

function init() {
    scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.HOUR, 0);
    cal.set(java.util.Calendar.MINUTE, 10);
    cal.set(java.util.Calendar.SECOND, 0);
    var nextTime = cal.getTimeInMillis();
    while (nextTime <= java.lang.System.currentTimeMillis()) {
        nextTime += 180000; //這裡就是設定多久存檔一次啦，單位是毫秒，可依據玩家數做調整
    }
    setupTask = em.scheduleAtTimestamp("start", nextTime);
}


function cancelSchedule() {
    setupTask.cancel(true);
}

function start() {
    scheduleNew();
    try {
        if( em != null && em.getChannelServer() != null)
            em.getChannelServer().giveCSPoints();
    } catch (e) {
    }
    var iter = em.getInstances().iterator();
    while (iter.hasNext()) {
        var eim = iter.next();
    }
}
