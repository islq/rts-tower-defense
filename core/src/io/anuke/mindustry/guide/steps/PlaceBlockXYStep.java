package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.input.InputHandler;
import io.anuke.mindustry.input.MobileInput;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

public class PlaceBlockXYStep extends ClickTileXYStep {
    public PlaceBlockXYStep(Block block, int tileX, int tileY, String msg) {
        super(tileX, tileY, block.size, 1, msg, () -> {
            InputHandler input = Vars.control.input(0);
            if (input instanceof MobileInput) {
                return ((MobileInput)input).hasPlaced(tileX, tileY, block);
            }
            return false;
        });
    }

    @Override
    public boolean isPlaceEnable(Tile tile) {
        return tile.x == tileX && tile.y == tileY;
    }
}
