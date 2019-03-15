package io.anuke.mindustry.guide.steps;

import static io.anuke.mindustry.entities.traits.BuilderTrait.mineDistance;

public class PanToXYForMineStep extends PanToXYStep {
    public PanToXYForMineStep(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public PanToXYForMineStep(int tileX, int tileY, String msg) {
        super(tileX, tileY, msg);
    }

    @Override
    protected float getMaxDistance() {
        return mineDistance;
    }
}
