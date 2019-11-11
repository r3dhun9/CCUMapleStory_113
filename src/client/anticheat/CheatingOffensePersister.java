/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client.anticheat;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import server.Timer.CheatTimer;

public class CheatingOffensePersister {

    private final static CheatingOffensePersister instance = new CheatingOffensePersister();
    private final Set<CheatingOffenseEntry> toPersist = new LinkedHashSet<CheatingOffenseEntry>();
    private final Lock mutex = new ReentrantLock();

    private CheatingOffensePersister() {
        CheatTimer.getInstance().register(new PersistingTask(), 61000);
    }

    public static CheatingOffensePersister getInstance() {
        return instance;
    }

    public void persistEntry(CheatingOffenseEntry coe) {
        mutex.lock();
        try {
            toPersist.remove(coe); //equal/hashCode h4x
            toPersist.add(coe);
        } finally {
            mutex.unlock();
        }
    }

    public class PersistingTask implements Runnable {

        @Override
        public void run() {
            //CheatingOffenseEntry[] offenses;

            mutex.lock();
            try {
                //offenses = toPersist.toArray(new CheatingOffenseEntry[toPersist.size()]);
                toPersist.clear();
            } finally {
                mutex.unlock();
            }

        }
    }
}
