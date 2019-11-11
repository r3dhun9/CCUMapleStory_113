/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author Tasi
 */
public enum AntiMacroType {
    
    NORMAL(0),
    ADMIN_SKILL(1);
    
    private byte value = (byte)0;
    
    private AntiMacroType(int value) {
        this.value = (byte)value;
    }
    
    public byte getValue() {
        return value;
    }
}
