package io.anuke.mindustry.guide.steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.GuideUtils;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.input.InputHandler;
import io.anuke.mindustry.input.MobileInput;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.util.Log;

import static io.anuke.mindustry.Vars.disableUI;

public class PlaceBlockLineStep extends GuideStep {
    private final int offset;
    Block block;
    int tileX1;
    int tileY1;
    int tileX2;
    int tileY2;
    int headX;
    int headY;
    String msg;
    private boolean oldUIStatus;

    public PlaceBlockLineStep(Block block, int tileX1, int tileY1, int tileX2, int tileY2, String msg) {
        this.block = block;
        this.tileX1 = tileX1;
        this.tileY1 = tileY1;
        this.tileX2 = tileX2;
        this.tileY2 = tileY2;
        if (msg == null) {
            if (Math.abs(tileX1 - tileX2) > 3 || Math.abs(tileY1 - tileY2) > 3) {
                this.msg = "提示：[accent]长按[]起点2秒，然后[accent]拖动[]到终点可以一次放一排建筑";
            }
        } else {
            this.msg = msg;
        }
        this.offset = ((block.size + 1) % 2) * Vars.tilesize / 2;
        this.headX = this.tileX1;
        this.headY = this.tileY1;
    }

    private void pointHead(IGuideDisplay display, int tx, int ty, boolean updateOnly) {
        Tile tile = Vars.world.tile(tx, ty);
        float x = tile.drawx();
        float y = tile.drawy();
        if (block.size % 2 == 0) {
            x += Vars.tilesize / 2;
            y += Vars.tilesize / 2;
        }

        Vector2 v = Graphics.screen(x, y);
        if (updateOnly) {
            display.updateFocus(v.x, v.y);
        } else {
            display.showFingerMsgAt(msg, v.x, v.y);
            display.showFingerAt(v.x, v.y);
        }
    }

    @Override
    public void finish(IGuideDisplay display) {
        if (done) return;
        super.finish(display);
        Vars.ui.disable(oldUIStatus);
    }
    
    @Override
    protected void run(IGuideDisplay display) {
        oldUIStatus = disableUI;
        Vars.ui.disable(true);
        pointHead(display, headX, headY, false);
    }

    @Override
    public void update(IGuideDisplay display) {
        super.update(display);
        pointHead(display, headX, headY, true);
    }

    private Vector2 lastValidLinePoint = new Vector2(-1, -1);
    private boolean isEventOnTile(IGuideDisplay display, InputEvent e, float x, float y) {
        Vector2 v = Graphics.mouseWorld();//world(x, y);

        int tx = Vars.world.toTile(v.x - this.offset);
        int ty = Vars.world.toTile(v.y - this.offset);

        boolean flag = true;
        boolean isTouchUp = false;
        boolean isLongPress = false;
        final InputHandler input = Vars.control.input(0);

        if (e.getType() == InputEvent.Type.touchUp) {
            if (input instanceof MobileInput) {
                if(((MobileInput) input).isBadLineMode(tx, ty)) {
                    display.showFingerMsgAt("请按要求的方向拖动", x, y);
                    return true;
                } else {
                    isTouchUp = true;
                    Vars.threads.runDelay(() -> {
                        int dx = tileX1 <= tileX2 ? 1 : -1;
                        int dy = tileY1 <= tileY2 ? 1 : -1;

                        boolean allPlaced = true;
                        for (int tx0 = tileX1; (dx > 0 ? tx0 <= tileX2 : tx0 >= tileX2); tx0 += dx) {
                            for (int ty0 = tileY1; (dy > 0 ? ty0 <= tileY2 : ty0 >= tileY2); ty0 += dy) {
                                if (!((MobileInput) input).hasPlaced(tx0, ty0, block)) {
                                    allPlaced = false;
                                    headX = tx0;
                                    headY = ty0;
                                    break;
                                }
                            }
                            if (!allPlaced) {
                                pointHead(display, headX, headY, true);
                                break;
                            }
                        }
                        if (allPlaced) {
                            ((MobileInput) input).removeRequests(tileX1, tileY1, tileX2, tileY2);
                            finish(display);
                        }
                    });

                }
            }
        }

        if (tileX1 > tileX2) {
            flag = flag && tx >= tileX2 && tx <= tileX1;
        } else {
            flag = flag && tx >= tileX1 && tx <= tileX2;
        }

        if (tileY1 > tileY2) {
            flag = flag && ty >= tileY2 && ty <= tileY1;
        } else {
            flag = flag && ty >= tileY1 && ty <= tileY2;
        }

        if (isTouchUp && !flag) {
            ((MobileInput) input).tryPlaceAll((int)x, (int)(Gdx.graphics.getHeight() - 1 - y));
        }
        return flag;
    }

    @Override
    public boolean consumeEvent(IGuideDisplay display, InputEvent e, float x, float y) {
        return !isEventOnTile(display, e, x, y);
    }

    @Override
    public boolean isPlaceEnable(Tile tile) {
        return GuideUtils.inRange(tile.x, tileX1, tileX2) && GuideUtils.inRange(tile.y, tileY1, tileY2);
    }
}
