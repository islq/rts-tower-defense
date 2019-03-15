package io.anuke.mindustry.guide.steps;

import com.badlogic.gdx.math.Vector2;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.function.BooleanProvider;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.util.Log;

import static io.anuke.mindustry.Vars.disableUI;

public class ClickTileXYStep extends GuideStep {
    protected String msg;
    protected int tileX;
    protected int tileY;
    protected int size;
    protected int focusSize;
    protected int offset;
    protected BooleanProvider checker;

    private Tile tile;
    private boolean oldUIStatus;

    public ClickTileXYStep(int tileX, int tileY, int size, int focusSize, String msg) {
        this(tileX, tileY, size, focusSize, msg, null);
    }

    public ClickTileXYStep(int tileX, int tileY, int size, int focusSize, String msg, BooleanProvider checker) {
        this.msg = msg;
        this.tileX = tileX;
        this.tileY = tileY;
        this.size = size;
        this.focusSize = focusSize;
        this.offset = ((this.size + 1) % 2) * Vars.tilesize / 2;
        this.checker = checker;
    }

    private float getX() {
        float x = tile.drawx();
        if (size % 2 == 0) x += Vars.tilesize / 2;
        return x;
    }

    private float getY() {
        float y = tile.drawy();
        if (size % 2 == 0) y += Vars.tilesize / 2;
        return y;
    }

    @Override
    public void finish(IGuideDisplay display) {
        if (done) return;
        super.finish(display);
        Vars.ui.disable(oldUIStatus);
    }

    @Override
    public void run(IGuideDisplay display) {
        tile = Vars.world.tile(tileX, tileY);
        if (tile == null) {
            throw new RuntimeException("guide at null tile x=" + tileX + ", y=" + tileY);
        }

        oldUIStatus = disableUI;
        Vars.ui.disable(true);

        if (checker != null && checker.get()) {
            finish(display);
            return;
        }

        Vector2 v = Graphics.screen(getX(), getY());
        display.showFingerMsgAt(msg, v.x, v.y);
        display.showFingerAt(v.x, v.y);
        // Log.err("run tileX1" + tileX + ", tileY1:" + tileY + ", worldX:" + getX() + ", worldY:" + getY() + ", screenX:" + v.x + ", screenY:" + v.y);

    }

    @Override
    public void update(IGuideDisplay display) {
        super.update(display);
        Vector2 v = Graphics.screen(getX(), getY());
        display.updateFocus(v.x, v.y);
        // Log.err("update tileX1" + tileX1 + ", tileY1:" + tileY1 + ", worldX:" + getX() + ", worldY:" + getY() + ", screenX:" + v.x + ", screenY:" + v.y);
        // Log.err("update camera x:" + Core.camera.position.x + ", y:" + Core.camera.position.y + ", zoom:" +Core.camera.zoom);
    }

    @Override
    public boolean onClick(IGuideDisplay display, float x, float y) {
        if (isEventOnTile(display, x, y)) {
             Vars.threads.runDelay(() -> {
                if (checker == null || checker.get()) {
                    finish(display);
                } else {
                    display.updateFocus(x, y);
                }
             });
            return false;
        } else {
            return true;
        }
    }

    private boolean isEventOnTile(IGuideDisplay display, float x, float y) {
        Vector2 v = Graphics.mouseWorld();//world(x, y);

        int tx = Vars.world.toTile(v.x - this.offset);
        int ty = Vars.world.toTile(v.y - this.offset);

        // Log.err("tx" + tx + ", ty:" + ty + ", worldX:" + v.x + ", worldY:" + v.y + ", screenX:" + x + ", screenY:" + y);

        float xx = tileX;
        float yy = tileY;
        if (this.focusSize % 2 == 0) {
            xx += 0.5f;
            yy += 0.5f;
        }

        if (Math.abs(tx - xx) < focusSize / 2f && Math.abs(ty - yy) < focusSize / 2f) {
            return true;
        }
        return false;
    }

    @Override
    public boolean consumeEvent(IGuideDisplay display, InputEvent e, float x, float y) {
        return !isEventOnTile(display, x, y);
    }

}
