package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

public class ClickPowerNodeStep extends ClickTileXYStep {
    Tile tile;
    public ClickPowerNodeStep(int tileX, int tileY) {
        super(tileX, tileY, 1, 1, null);
        checker = this::isSelected;
    }

    @Override
    public void run(IGuideDisplay display) {
        tile = Vars.world.tile(tileX, tileY);
        if (tile == null) {
            finish(display);
            return;
        };
        size = tile.block().size;
        focusSize = size;
        super.run(display);
    }

    private boolean isSelected() {
        Tile selectedTile = Vars.control.input(0).getFrag().config.getSelectedTile();
        return selectedTile != null && selectedTile.x == tileX && selectedTile.y == tileY;
    }
}
