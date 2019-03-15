package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.TileEntity;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;

public class LinkPowerStep extends ClickTileXYStep {
    private final int targetX;
    private final int targetY;
    Tile tile, targetTile;
    public LinkPowerStep(int tileX, int tileY, int targetX, int targetY) {
        super(tileX, tileY, 1, 1, null);
        this.targetX = targetX;
        this.targetY = targetY;
        checker = this::isLinked;
    }

    @Override
    public void run(IGuideDisplay display) {
        tile = Vars.world.tile(tileX, tileY);
        targetTile = Vars.world.tile(targetX, targetY);
        if (tile == null || targetTile == null) {
            finish(display);
            return;
        };
        size = tile.block().size;
        focusSize = size;
        super.run(display);
    }

    private boolean isLinked() {
        TileEntity entity1 = tile.entity();
        TileEntity entity2 = targetTile.entity();

        return (entity1 != null && entity2 != null && entity1.power != null && entity2.power != null && entity1.power.graph == entity2.power.graph);
    }
}
