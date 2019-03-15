package io.anuke.mindustry.guide;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.core.Guide;
import io.anuke.mindustry.maps.Sector;
import io.anuke.ucore.core.Settings;

import static io.anuke.mindustry.Vars.world;

public class GuideManager {

    public static final String GUIDE_STEP_KEY = "guide_step";
    private final Guide guide;

    public GuideManager(Guide guide) {
        this.guide = guide;
        load();
    }

    public void init() {
        init(false);
    }

    public void init(boolean forceLaunch) {
        if (forceLaunch) {
            currentStep = GuideStatus.first;
            Settings.putInt(GUIDE_STEP_KEY, currentStep.ordinal());
        }
        if (currentStep != GuideStatus.done) {
            final Sector sector = Vars.world.sectors.get(0, 0);
            Vars.ui.loadLogic(() -> {
                if (Guide.debug || forceLaunch) {
                    world.sectors.abandonSector(sector);
                }
                world.sectors.playSector(sector);
            });
        }
    }

    public boolean hasDone() {
        return currentStep == GuideStatus.done;
    }

    public boolean needHideMenu() {
        return currentStep == GuideStatus.first;
        // return currentStep != GuideStatus.done;
    }

    public boolean needHidePlaceMenu() {
        return currentStep == GuideStatus.first || currentStep == GuideStatus.second;
    }

    public boolean needHideMiniMap() {
        return currentStep != GuideStatus.done;
    }

    enum GuideStatus {
        done,
        first,  // skip menu and goto tutorial directly
        second,
        third
    }

    GuideStatus currentStep = GuideStatus.first;

    public void updateCurrentStep(int step) {
        if (step < 0) {
            step = GuideStatus.first.ordinal();
        }

        if (step >= GuideStatus.values().length) {
            step = GuideStatus.done.ordinal();
        }
        currentStep = GuideStatus.values()[step];
    }

    public void load() {
        if (Guide.debug) {
            currentStep = GuideStatus.first;
            return;
        }
        int step = Settings.getInt(GUIDE_STEP_KEY, -1);
        updateCurrentStep(step);
    }

    public void nextStep() {
        if (currentStep == GuideStatus.done) return;

        int step = currentStep.ordinal();
        step ++;
        if (step >= GuideStatus.values().length) {
            step = GuideStatus.done.ordinal();
        }
        Settings.putInt(GUIDE_STEP_KEY, step);
        if(step == GuideStatus.done.ordinal()) {
            Settings.save();
        }
        updateCurrentStep(step);
    }

    public void done() {
        currentStep = GuideStatus.done;
        Settings.putInt(GUIDE_STEP_KEY, GuideStatus.done.ordinal());
        Settings.save();
    }

}
