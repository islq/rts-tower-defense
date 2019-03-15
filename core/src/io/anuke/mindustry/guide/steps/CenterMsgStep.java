package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.guide.IGuideDisplay;

public class CenterMsgStep extends GuideStep {
    String msg;

    public CenterMsgStep(String msg) {
        this.msg = msg;
        MIN_STEP_DELAY = 0.5f;
    }

    @Override
    public void run(IGuideDisplay display) {
        display.showMessage(msg);
    }

    @Override
    public boolean onClick(IGuideDisplay display, float x, float y) {
        finish(display);
        return false;
    }
}
