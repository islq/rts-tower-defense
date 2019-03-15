package io.anuke.mindustry.maps.missions;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.mindustry.guide.MissionGuides;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Bundles;
import io.anuke.ucore.util.Mathf;

import static io.anuke.mindustry.Vars.guide;
import static io.anuke.mindustry.Vars.headless;
import static io.anuke.mindustry.Vars.players;
import static io.anuke.mindustry.Vars.tilesize;
import static io.anuke.mindustry.Vars.world;

public class BlockLocMission extends Mission{
    private final Block block;
    private final int x, y, rotation;

    public BlockLocMission(Block block, int x, int y, int rotation){
        this.block = block;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public BlockLocMission(Block block, int x, int y){
        this.block = block;
        this.x = x;
        this.y = y;
        this.rotation = 0;
    }

    @Override
    public void drawOverlay(){
        Lines.stroke(2f);

        Draw.color(Palette.accentBack);
        float ox = this.x * tilesize + block.offset();
        float oy = y * tilesize + block.offset();
        Lines.square(ox, oy - 1f, block.size * tilesize/2f + 1f+ Mathf.absin(Timers.time(), 6f, 2f));

        Draw.color(Palette.accent);
        Lines.square(ox, oy, block.size * tilesize/2f + 1f+ Mathf.absin(Timers.time(), 6f, 2f));


        if(block.rotate){
            Draw.colorl(0.4f);
            Draw.rect("icon-arrow-overlay", ox, oy - 1f, rotation*90);
            Draw.colorl(0.6f);
            Draw.rect("icon-arrow-overlay", ox, oy, rotation*90);
        }

        if (players[0].distanceTo(ox, oy) > Vars.tilesize * 10) {

            float rot = players[0].angleTo(ox, oy);
            float len = 12f;

            Draw.color(Palette.accentBack);
            Draw.rect("icon-arrow-overlay", players[0].x + Angles.trnsx(rot, len), players[0].y + Angles.trnsy(rot, len), rot);
            Draw.color(Palette.accent);
            Draw.rect("icon-arrow-overlay", players[0].x + Angles.trnsx(rot, len), players[0].y + Angles.trnsy(rot, len) + 1f, rot);
        }

        Draw.reset();
    }

    @Override
    public void showMessage() {
//        if (!headless) {
//            Tile tile = world.tile(x, y);
//            if (tile != null) {
//                guide.focusTile(tile);
//                guide.showMsg(displayString());
//            }
//        }
        super.showMessage();
    }

    @Override
    public boolean isComplete(){
        return world.tile(x, y).block() == block && (!block.rotate || world.tile(x,y).getRotation() == rotation);
    }

    @Override
    public String displayString(){
        return Bundles.format("text.mission.block", block.formalName);
    }

    @Override
    public Mission addGuide() {
        return setGuide(MissionGuides.placeBlockGuide(block, x, y, rotation, extraMessage));
    }
}
