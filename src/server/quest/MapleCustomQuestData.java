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
package server.quest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataEntity;
import provider.wzxml.MapleDataType;

public class MapleCustomQuestData implements MapleData, Serializable {

    private static final long serialVersionUID = -8600005891655365066L;
    private final List<MapleCustomQuestData> children = new LinkedList<>();
    private final String name;
    private final Object data;
    private final MapleDataEntity parent;

    public MapleCustomQuestData(String name, Object data, MapleDataEntity parent) {
        this.name = name;
        this.data = data;
        this.parent = parent;
    }

    public void addChild(MapleData child) {
        children.add((MapleCustomQuestData) child);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MapleDataType getType() {
        return MapleDataType.UNKNOWN_TYPE;
    }

    @Override
    public List<MapleData> getChildren() {
        MapleData[] ret = new MapleData[children.size()];
        ret = children.toArray(ret);
        return new ArrayList<>(Arrays.asList(ret));
    }

    @Override
    public MapleData getChildByPath(String name) {
        if (name.equals(this.name)) {
            return this;
        }
        String lookup, nextName;
        if (!name.contains("/")) {
            lookup = name;
            nextName = name;
        } else {
            lookup = name.substring(0, name.indexOf("/"));
            nextName = name.substring(name.indexOf("/") + 1);
        }
        for (MapleData child : children) {
            if (child.getName().equals(lookup)) {
                return child.getChildByPath(nextName);
            }
        }
        return null;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public Iterator<MapleData> iterator() {
        return getChildren().iterator();
    }

    @Override
    public MapleDataEntity getParent() {
        return parent;
    }
}
