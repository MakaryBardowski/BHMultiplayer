/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LemurGUI.components;

import client.Main;

/**
 *
 * @author 48793
 */
public class LemurUtils {

    public static float getHeightRatio() {
        return ((float) Main.getInstance().getAppSettings().getHeight() / 1080);
    }

    public static float getWidthRatio() {
        return ((float) Main.getInstance().getAppSettings().getWidth() / 1920);
    }

}
