package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.input.InputHandler;
import io.anuke.mindustry.input.MobileInput;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

public class MineXYStep extends ClickTileXYStep {
    public MineXYStep(int tileX, int tileY, int size, int focusSize, String msg) {
        super(tileX, tileY, size, focusSize, msg, () -> {
            InputHandler input = Vars.control.input(0);
            Tile t = input.player.getMineTile();
            float r = (focusSize + 1f) / 2f;
            return t != null && (Math.abs(t.x - tileX) < r && Math.abs(t.y - tileY) < r) && !input.player.isDead();
        });
    }

    @Override
    public void update(IGuideDisplay display) {
        if (checker == null || checker.get()) {
            finish(display);
        } else {
            super.update(display);
        }
    }
}
