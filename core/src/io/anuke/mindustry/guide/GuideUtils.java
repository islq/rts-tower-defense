package io.anuke.mindustry.guide;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.power.PowerNode;

import static io.anuke.mindustry.Vars.world;

public class GuideUtils {
    public static boolean inRange(int v, int v1, int v2) {
        int max = Math.max(v1, v2);
        int min = Math.min(v1, v2);
        return v >= min && v<= max;
    }

    public static void linkPower(int nodeTx, int nodeTy, int otherTx, int otherTy) {
        Tile tile = Vars.world.tile(nodeTx, nodeTy);
        Tile other = Vars.world.tile(otherTx, otherTy);
        if (other != null && tile != null && tile.block() instanceof PowerNode) {
            PowerNode powerNode = (PowerNode) tile.block();
            powerNode.onConfigureTileTapped(tile, other);
        }
    }

    public static void setBlock(Block block, int tx, int ty, int rotation) {
        Tile tile = Vars.world.tile(tx, ty);
        tile.setRotation((byte) rotation);
        world.setBlock(tile, block, Team.blue);
    }
}
