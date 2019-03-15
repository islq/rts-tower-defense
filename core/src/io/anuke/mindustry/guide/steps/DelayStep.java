package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.ucore.function.BooleanProvider;
import io.anuke.ucore.scene.event.Event;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.util.Log;

public class DelayStep extends GuideStep {

    private final float delay;

    public DelayStep(float delay) {
        this.delay = delay;
    }

    @Override
    public void run(IGuideDisplay display)
    {
    }

    @Override
    public void update(IGuideDisplay display) {
        super.update(display);
        if (startTs > delay) {
            finish(display);
        }
    }

    @Override
    public boolean onEvent(IGuideDisplay display, Event e) {
        Log.err(e.toString());
        return true;
    }

}
