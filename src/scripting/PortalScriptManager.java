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
package scripting;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import client.MapleClient;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import server.MaplePortal;
import tools.EncodingDetect;
import tools.FilePrinter;

public class PortalScriptManager {

    private static final PortalScriptManager instance = new PortalScriptManager();
    private final Map<String, PortalScript> scripts = new HashMap<String, PortalScript>();
    private final static ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("javascript").getFactory();

    public final static PortalScriptManager getInstance() {
        return instance;
    }

    private final PortalScript getPortalScript(final String scriptName) {
        if (scripts.containsKey(scriptName)) {
            return scripts.get(scriptName);
        }
        final File scriptFile = new File("scripts/portal/" + scriptName + ".js");
        if (!scriptFile.exists()) {
            scripts.put(scriptName, null);
            return null;
        }
        InputStream in = null;
        final ScriptEngine portal = sef.getScriptEngine();
        try {
            in = new FileInputStream(scriptFile);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, EncodingDetect.getJavaEncode(scriptFile)));
            //Java8
            //String lines = "load('nashorn:mozilla_compat.js');" + bf.lines().collect(Collectors.joining(System.lineSeparator()));
            CompiledScript compiled = ((Compilable) portal).compile(bf);
            compiled.eval();
        } catch (final Exception e) {
            System.err.println("Error executing Portalscript: " + scriptName + ":" + e);
            FilePrinter.printError("PortalScriptManager.txt", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e) {
                    System.err.println("ERROR CLOSING" + e);
                }
            }
        }
        final PortalScript script = ((Invocable) portal).getInterface(PortalScript.class);
        scripts.put(scriptName, script);
        return script;
    }


    public final void executePortalScript(final MaplePortal portal, final MapleClient c) {
        final PortalScript script = getPortalScript(portal.getScriptName());
        if (c.getPlayer().isShowDebugInfo()) {
            c.getPlayer().dropMessage("您已經建立與傳送門腳本: " + portal.getScriptName() + ".js 的關聯。");
        }
        if (script != null) {
            try {
                script.enter(new PortalPlayerInteraction(c, portal));
            } catch (Exception e) {
                System.err.println("進入傳送腳本失敗: " + portal.getScriptName() + ":" + e);
                if (c.getPlayer().isShowDebugInfo()) {
                    c.getPlayer().dropMessage("進入傳送腳本失敗 " + portal.getScriptName() + ".js 的關聯。");
                }
            }
        } else {
            System.out.println("未處理的傳送腳本 " + portal.getScriptName() + " 所在地圖 " + c.getPlayer().getMapId());
            FilePrinter.printError("PortalScriptManager.txt", "未處理的傳送腳本 " + portal.getScriptName() + " 所在地圖 " + c.getPlayer().getMapId());
            if (c.getPlayer().isShowDebugInfo()) {
                    c.getPlayer().dropMessage("未處理的傳送腳本 " + portal.getScriptName() + ".js 的關聯。");
                }
        }
    }

    public final void clearScripts() {
        scripts.clear();
    }
}
