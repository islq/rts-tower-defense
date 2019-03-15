package io.anuke.mindustry.guide.steps;

import com.badlogic.gdx.Gdx;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.guide.IGuideRunable;
import io.anuke.mindustry.guide.mission.MissionGuide;
import io.anuke.mindustry.input.InputHandler;
import io.anuke.mindustry.input.MobileInput;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.scene.event.Event;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.util.Log;

import static io.anuke.ucore.scene.event.InputEvent.Type.mouseMoved;
import static io.anuke.ucore.scene.event.InputEvent.Type.touchDown;
import static io.anuke.ucore.scene.event.InputEvent.Type.touchUp;

public abstract class GuideStep {
    protected float MIN_STEP_DELAY = 0;// 0.5f;
    protected boolean done = false;
    protected float startTs = 0;
    protected MissionGuide guide;

    protected abstract void run(IGuideDisplay display);
    public void start(IGuideDisplay display) {
        startTs = 0;
        done = false;
        run(display);
    }

    public boolean isFinished() {
        return done;
    }
    public void finish(IGuideDisplay display) {
        if (done) return;
        done = true;
        display.clearAll();
    }
    public void update(IGuideDisplay display) {
        startTs += Gdx.graphics.getDeltaTime();
    }

    /**
     *
     * @param e
     * @return false to pop event, true to skip it
     */
    public boolean onEvent(IGuideDisplay display, Event e) {
        if (!(e instanceof  InputEvent)) return false;
        InputEvent ie = (InputEvent) e;
        InputEvent.Type type = ie.getType();

        if (startTs < MIN_STEP_DELAY) return true;

        boolean consumed = consumeEvent(display, ie, ie.getStageX(), ie.getStageY());
        if (type == touchUp) {
            // Log.err("event x:" + ie.getStageX() + " y:" + ie.getStageY() + "y2:" + (Gdx.graphics.getHeight() - 1 - ie.getStageY()));
            if (!onClick(display, ie.getStageX(), ie.getStageY())) {
                return false;
            }
        } else if (type == touchDown) {
            Log.err("e is handle:" + e.isHandled());
        }

        return consumed;
    }

    /**
     *
     * @param x
     * @param y
     * @return false to pop event, true to skip it
     */
    public boolean onClick(IGuideDisplay display, float x, float y) {
        return true;
    }

    public boolean consumeEvent(IGuideDisplay display, InputEvent e, float x, float y) {
        return true;
    }

    public void setGuide(MissionGuide guide) {
        this.guide = guide;
    }

    public boolean isTaskCompleted() {
        return guide == null || guide.isTaskCompleted();
    }

    public boolean isPanEnable() {
        return false;
    }

    public boolean isPlaceEnable(Tile tile) {
        return false;
    }
}
