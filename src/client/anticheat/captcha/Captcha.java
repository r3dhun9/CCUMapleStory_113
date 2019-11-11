/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.anticheat.captcha;

/**
 *
 * @author Weber
 */
public class Captcha {

    private final String answer;
    private final byte[] imageData;

    public Captcha(String answer, byte[] imageData) {
        this.answer = answer;
        this.imageData = imageData;
    }

    public String getAnswer() {
        return answer;
    }

    public byte[] getImageData() {
        return imageData;
    }
}
