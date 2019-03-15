package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.guide.IGuideDisplay;

public class ActionStep extends GuideStep {
    Runnable r;

    public ActionStep(Runnable r) {
        this.r = r;
    }

    @Override
    public void run(IGuideDisplay display)
    {
        r.run();
        finish(display);
    }

}
