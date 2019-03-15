package io.anuke.mindustry.maps.missions;

import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.mindustry.guide.MissionGuides;
import io.anuke.mindustry.world.Block;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

import static io.anuke.mindustry.Vars.players;
import static io.anuke.mindustry.Vars.tilesize;

public class LineBlockMission extends Mission{
    private final int x1, y1, x2, y2;
    private final Block block;
    private final int rotation;
    private Array<BlockLocMission> points = new Array<>();
    private int completeIndex;

    public LineBlockMission(Block block, int x1, int y1, int x2, int y2, int rotation){
        this.block = block;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.rotation = rotation;
        Array<GridPoint2> points = new Bresenham2().line(x1, y1, x2, y2);
        for(GridPoint2 point : points){
            this.points.add(new BlockLocMission(block, point.x, point.y, rotation));
        }
        // setGuide(MissionGuides.placeBlockLineGuide(block, x1, y1, x2, y2, rotation));
    }

    @Override
    public boolean isComplete(){
        while(completeIndex < points.size && points.get(completeIndex).isComplete()){
            completeIndex ++;
        }
        return completeIndex >= points.size;
    }

//    @Override
    public void drawOverlay2(){
//        if(completeIndex < points.size){
//            points.get(completeIndex).drawOverlay();
//        }
        for (BlockLocMission mission : points) {
            if (mission.isComplete()) continue;
            mission.drawOverlay();
        }
    }

    @Override
    public void drawOverlay(){
        Lines.stroke(1f);

        int count = 0;
        int w = block.size * tilesize;
        int dx = x1 <= x2 ? 1 : -1;
        int dy = y1 <= y2 ? 1 : -1;
        for(int x=x1; (dx > 0 ? x<=x2 : x>=x2); x += dx) {
            for(int y=y1; (dy > 0 ? y<=y2 : y>=y2); y += dy) {
                count ++;
                float ox = x * tilesize + block.offset();
                float oy = y * tilesize + block.offset();

                Draw.color(Palette.accentBack);
                Lines.square(ox, oy - 1f, w / 2);

                Draw.color(Palette.accent);
                Lines.square(ox, oy, w / 2);

                if(block.rotate){
                    Draw.colorl(0.4f);
                    Draw.rect("icon-arrow-overlay", ox, oy - 1f, w, w,rotation*90);
                    Draw.colorl(0.6f);
                    Draw.rect("icon-arrow-overlay", ox, oy, w, w,rotation*90);
                }
            }
        }

        Draw.reset();
    }

    @Override
    public void reset(){
        completeIndex = 0;
    }

    @Override
    public String displayString(){
        if(completeIndex < points.size){
            return points.get(completeIndex).displayString();
        }
        return points.first().displayString();
    }

    @Override
    public Mission addGuide() {
        return setGuide(MissionGuides.placeBlockLineGuide(block, x1, y1, x2, y2, rotation, extraMessage));
    }
}
