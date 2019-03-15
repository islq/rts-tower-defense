package io.anuke.mindustry.ui.dialogs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Align;

public class SkipDialog extends FloatingDialog {
    public SkipDialog(String title, String text, Runnable confirmed) {
        super(title);
        shouldPause = true;
        content().add(text).width(400f).wrap().pad(4f).get().setAlignment(Align.center, Align.center);
        buttons().defaults().size(200f, 54f).pad(2f);
        setFillParent(false);
        buttons().addButton("$text.cancel", this::hide);
        buttons().addButton("$text.ok", () -> {
            hide();
            confirmed.run();
        });
        keyDown(Input.Keys.ESCAPE, this::hide);
        keyDown(Input.Keys.BACK, this::hide);
    }

    public void hide(){
        setOrigin(Align.center);
        setClip(false);
        setTransform(true);

        hide(null);
    }
}
