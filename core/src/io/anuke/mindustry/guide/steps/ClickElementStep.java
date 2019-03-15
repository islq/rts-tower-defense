package io.anuke.mindustry.guide.steps;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.input.InputHandler;
import io.anuke.mindustry.input.MobileInput;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.function.BooleanProvider;
import io.anuke.ucore.function.Supplier;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.util.Log;

public class ClickElementStep extends GuideStep {
    private Rectangle rc;
    private final String msg;
    private Supplier<Element> elementSupplier;
    private final BooleanProvider checker;

    public ClickElementStep(Supplier<Element> elementSupplier, String msg) {
        this(elementSupplier, msg, null);
    }

    public ClickElementStep(Supplier<Element> elementSupplier, String msg, BooleanProvider checker) {
        this.elementSupplier = elementSupplier;
        this.msg = msg;
        this.checker = checker;
    }

    @Override
    public void run(IGuideDisplay display) {
        if (checker != null && checker.get()) {
            finish(display);
            return;
        }

        Element element = elementSupplier.get();

        if (element == null) {
            finish(display);
        }

        Vector2 v = element.localToStageCoordinates(new Vector2(0, 0));
        float x = v.x + element.getWidth() / 2;
        float y = v.y + element.getHeight() / 2;
        rc = new Rectangle(v.x, v.y, element.getWidth(), element.getHeight());
        display.showFingerMsgAt(msg, x, y);
        display.showFingerAt(x, y);

    }

    @Override
    public void update(IGuideDisplay display) {
        super.update(display);
        Element element = elementSupplier.get();
        Vector2 v = element.localToStageCoordinates(new Vector2(0, 0));
        if (rc.x != v.x || rc.y != v.y) {
            float x = v.x + element.getWidth() / 2;
            float y = v.y + element.getHeight() / 2;
            rc = new Rectangle(v.x, v.y, element.getWidth(), element.getHeight());
            display.updateFocus(x, y);
        }
    }

    @Override
    public boolean onClick(IGuideDisplay display, float x, float y) {
        if (rc != null && rc.contains(x, y)) {
            Vars.threads.runDelay(() -> {
                if (checker == null || checker.get()) {
                    finish(display);
                } else {
                    display.updateFocus(x, y);
                }
            });
            return false;
        }
        return true;
    }

    @Override
    public boolean consumeEvent(IGuideDisplay display, InputEvent e, float x, float y) {
        return rc == null || !rc.contains(x, y);
    }

}
