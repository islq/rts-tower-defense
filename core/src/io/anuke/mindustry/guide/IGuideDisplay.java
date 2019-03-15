package io.anuke.mindustry.guide;

import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.scene.Element;

public interface IGuideDisplay {
    void clearAll();
    void showMessage(String s);

    void showFingerMsgAt(String s, float x, float y);
    void showFingerAt(float x, float y);

    void updateFocus(float x, float y);

    void focuseUIElement(Element element, String msg);

    void showFingerPan(Vector2 v, String msg);
}
