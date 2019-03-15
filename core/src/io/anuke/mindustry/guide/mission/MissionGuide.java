package io.anuke.mindustry.guide.mission;

import java.util.ArrayList;
import java.util.List;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.guide.steps.ActionStep;
import io.anuke.mindustry.guide.steps.CenterMsgStep;
import io.anuke.mindustry.guide.steps.GuideStep;
import io.anuke.mindustry.maps.missions.Mission;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.scene.event.Event;

public class MissionGuide {
    int iStep;
    ArrayList<GuideStep> steps;
    private Mission task;

    public MissionGuide() {
        this.steps = new ArrayList<>();
        iStep = 0;
    }

    public MissionGuide(List<GuideStep> steps) {
        this.steps = new ArrayList<GuideStep>(steps);
        iStep = 0;
        for (GuideStep step : steps) {
            step.setGuide(this);
        }
    }

    public MissionGuide disableUI() {
        addStep(new ActionStep(() -> {
            Vars.ui.disable(true);
        }));
        return this;
    }

    public MissionGuide enableUI() {
        addStep(new ActionStep(() -> {
            Vars.ui.disable(false);
        }));
        return this;
    }

    public MissionGuide addStep(GuideStep step) {
        step.setGuide(this);
        steps.add(step);
        return this;
    }

    public MissionGuide addStep(int index, GuideStep step) {
        step.setGuide(this);
        steps.add(index, step);
        return this;
    }

    public GuideStep currentStep() {
        if (iStep >=0 && steps != null && steps.size() > iStep) {
            return steps.get(iStep);
        }
        return null;
    }

    public GuideStep nextStep() {
        iStep ++;
        return currentStep();
    }

    /**
     *
     * @param display
     * @return true if all steps finished
     */
    public boolean update(IGuideDisplay display) {
        GuideStep step = currentStep();
        if (step == null) return true;

        if (step.isFinished()) {
            step = nextStep();
            if (step != null) {
                step.start(display);
            }
        } else {
            step.update(display);
        }
        return false;
    }

    public void init() {
        iStep = 0;
    }

    public void onBegin(IGuideDisplay display) {
        GuideStep step = currentStep();
        if (step != null) {
            step.start(display);
        }
    }

    public boolean onEvent(IGuideDisplay display, Event event) {
        GuideStep step = currentStep();
        if (step != null) {
            return step.onEvent(display, event);
        }
        return false;
    }

    public void setTask(Mission task) {
        this.task = task;
    }

    public boolean isTaskCompleted() {
        return task == null || task.isComplete();
    }

    public boolean completed() {
        return currentStep() == null;
    }

    public boolean isPanEnable() {
        if (currentStep() != null) {
            return currentStep().isPanEnable();
        }
        return true;
    }

    public boolean isPlaceEnable(Tile tile) {
        if (currentStep() != null) {
            return currentStep().isPlaceEnable(tile);
        }
        return true;
    }

    public MissionGuide addMsg(String msg) {
        addStep(0, new CenterMsgStep(msg));
        return this;
    }

    public void abort(IGuideDisplay display) {
        if (currentStep() != null) {
            currentStep().finish(display);
            iStep = steps.size();
        }
    }
}
