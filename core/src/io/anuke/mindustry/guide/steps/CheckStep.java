package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.ucore.function.BooleanProvider;
import io.anuke.ucore.scene.event.InputEvent;

public class CheckStep extends GuideStep {
    boolean forceCheck;
    float delaySeconds;
    BooleanProvider checker;

    public CheckStep(boolean forceCheck, float delaySeconds, BooleanProvider r) {
        this.forceCheck = forceCheck;
        this.checker = r;
        this.delaySeconds = delaySeconds;
    }

    public CheckStep(float delaySeconds, BooleanProvider r) {
        this(false, delaySeconds, r);
    }

    public CheckStep(BooleanProvider r) {
        this(0, r);
    }

    @Override
    public void run(IGuideDisplay display)
    {
    }

    @Override
    public void update(IGuideDisplay display) {
        super.update(display);
        if (delaySeconds > 0 && startTs < delaySeconds) return;
        if (checker.get() || (!forceCheck && isTaskCompleted())) {
            finish(display);
        }
    }

    @Override
    public boolean consumeEvent(IGuideDisplay display, InputEvent e, float x, float y) {
        return false;
    }

    @Override
    public boolean isPanEnable() {
        return true;
    }

}
