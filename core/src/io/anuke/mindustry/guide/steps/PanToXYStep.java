package io.anuke.mindustry.guide.steps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.entities.Player;
import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.scene.ui.layout.Unit;

import static io.anuke.mindustry.Vars.disableTap;
import static io.anuke.mindustry.Vars.disableUI;
import static io.anuke.mindustry.Vars.tilesize;
import static io.anuke.mindustry.entities.traits.BuilderTrait.mineDistance;
import static io.anuke.mindustry.entities.traits.BuilderTrait.placeDistance;
import static io.anuke.ucore.scene.event.InputEvent.Type.touchDown;
import static io.anuke.ucore.scene.event.InputEvent.Type.touchUp;

public class PanToXYStep extends GuideStep {
    private final String msg;
    private int x;
    private int y;
    private final int tx1, tx2, ty1, ty2;
    private boolean oldUIStatus;
    private Vector2 v;
    private boolean oldTapStatus;
    private int x2;
    private int y2;

    public PanToXYStep(int tileX, int tileY) {
        this(tileX, tileY, null);
    }

    public PanToXYStep(int tileX, int tileY, String msg) {
        this(tileX, tileY, -1, -1, msg);
    }

    public PanToXYStep(int tileX, int tileY, int tileX2, int tileY2, String msg) {
        this.msg = msg;
        this.tx1 = tileX;
        this.tx2 = tileX2;
        this.ty1 = tileY;
        this.ty2 = tileY2;
    }

    @Override
    protected void run(IGuideDisplay display) {
        this.x = tx1 * tilesize;
        this.y = ty1 * tilesize;

        if (tx2 >= 0 && ty2 >= 0) {
            this.x2 = tx2 * tilesize;
            this.y2 = ty2 * tilesize;
        } else {
            this.x2 = -1;
            this.y2 = -1;
        }

        oldUIStatus = disableUI;
        oldTapStatus = disableTap;
        Vars.ui.disable(true);
        disableTap = true;
        v = targetV();
        if(v == null) {
            finish(display);
            return;
        }
        display.showFingerPan(v, msg);
    }

    @Override
    public void finish(IGuideDisplay display) {
        super.finish(display);
        Vars.ui.disable(oldUIStatus);
        disableTap = oldTapStatus;
    }

    private boolean inScreen(int x0, int y0, boolean lineMode) {
        Vector2 v1 = Graphics.screen(x0 - tilesize, y0 - tilesize);
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int d = (int) Unit.dp.scl(200f);
        // if (v1.x < 0 || v1.y < 0 || v1.x > width || v1.y > height) return false;
        if (v1.x < 0 || v1.y < 0 || v1.x > width || v1.y > height - d) return false;
        if (!lineMode) {
            // if (v1.x > width / 2 && v1.y < height / 2) return false;
        }
        Vector2 v2 = Graphics.screen(x0 + tilesize, y0 + tilesize);
        // if (v2.x < 0 || v2.y < 0 || v2.x > width || v2.y > height) return false;
        if (v2.x < 0 || v2.y < 0 || v2.x > width || v2.y > height - d) return false;
        if (!lineMode) {
            // if (v2.x > width / 2 && v2.y < height / 2) return false;
            // if (v2.x > width / 2 && v2.y < height / 2) return false;
        }
        return true;
    }

    private Vector2 targetV() {
        Player player = Vars.players[0];
        float dst = Vector2.dst(player.x, player.y, x, y);
        boolean panFlag1 =  (dst > getMaxDistance() * 0.9 || !inScreen(x, y, false/*x2 >= 0*/));
        if (x2 >= 0) {
            float dst2 = Vector2.dst(player.x, player.y, x2, y2);
            boolean panFlag2 =  (dst2 > getMaxDistance() * 0.9 || !inScreen(x2, y2, true));
            float x0 = 0;
            float y0 = 0;
            if (panFlag1 && panFlag2) {
                x0 = (x + x2) / 2;
                y0 = (y + y2) / 2;
            } else if (panFlag1){
                x0 = x;
                y0 = y;
            } else if (panFlag2) {
                x0 = x2;
                y0 = y2;
            } else {
                return null;
            }
            return new Vector2(player.x - x0, player.y - y0).scl(1f / dst);
        } else {
            if (panFlag1) {
                return new Vector2(player.x - x, player.y - y ).scl(1f / dst);
            }
        }
        return null;
    }

    protected float getMaxDistance() {
        return placeDistance;
    }

    @Override
    public void update(IGuideDisplay display) {
        super.update(display);
        Vector2 v2 = targetV();
        if(v2 == null) {
            finish(display);
        } else if (!v2.epsilonEquals(v, 0.2f)) {
            v = v2;
            display.showFingerPan(v, msg);
        }
    }

    @Override
    public boolean consumeEvent(IGuideDisplay display, InputEvent e, float x, float y) {
//        if (e.getType() == touchUp || e.getType() == touchDown) {
//            return true;
//        }
        return false;
    }

    @Override
    public boolean isPanEnable() {
        return true;
    }
}
