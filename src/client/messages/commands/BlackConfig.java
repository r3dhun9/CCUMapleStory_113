/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.messages.commands;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lrenex
 */
public class BlackConfig {

    public static Map<Integer, String> BlackList = new HashMap() {
        {
            put(321, "一朵花");
        }
    };
    public static int 商店一次拍賣獲得最大楓幣 = 1500000;

    public static Map<Integer, String> getBlackList() {
        return BlackList;
    }

    public static void setBlackList(int accid, String name) {
        BlackList.put(accid, name);
    }
}
