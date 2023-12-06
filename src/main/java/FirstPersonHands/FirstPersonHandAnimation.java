/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FirstPersonHands;

import com.jme3.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author 48793
 */
@AllArgsConstructor
public enum FirstPersonHandAnimation {
    HOLD_PISTOL("HoldPistolR", new Vector3f(0, 0, 0)),
    HOLD_LMG("HoldLMG", new Vector3f(0, -0.3f, 1)),
    HOLD_RIFLE("HoldRifle", new Vector3f(0, 0.0f, 1)),
    HOLD_KNIFE("HoldKnife", new Vector3f(0, 0.0f, 0.5f));

    @Getter
    private final String animationName;

    @Getter
    private final Vector3f rootOffset;

}
