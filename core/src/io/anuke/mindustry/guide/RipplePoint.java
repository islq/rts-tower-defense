package io.anuke.mindustry.guide;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.core.Core;

public class RipplePoint {
    private final int num = 1;
    private final TextureRegion rippleRegion;
    float[] states = new float[num];
    private Vector2 point;
    float time = 0;
    float offset = 0.5f;
    float distance = 0.2f;

    private final Interpolation interpolation = Interpolation.circleOut;

    public RipplePoint(Vector2 point) {
        this.point = point;
        for(int i=0; i<num; i++) states[i] = 0;

        rippleRegion = Core.skin.getRegion("ripple");
    }

    /**
     *
     * @param delta : delta time
     * @return true if finished
     */
    public boolean act(float delta) {
        time += delta;

        float dt = time - offset;
        float t = dt;
        for(int i=0; i<num; i++) {
            if (dt < 0) break;
            t = dt;
            while (t >= 1f) t -= 1f;
            t = t > 0.5f ? 0 : t * 2;;
            states[i] = interpolation.apply(t);
            dt -= distance;
        }
        if (dt > 1f) time -= 1f;

        return false;
    }

    public void draw() {
        int w0 = rippleRegion.getRegionWidth();
        int h0 = rippleRegion.getRegionHeight();
        Color c = Core.batch.getColor();
        float oldA = c.a;
        for (int i=0; i<num; i++) {
            float w = w0 * states[i];
            float h = w0 * states[i];
            if (w < 0.01) continue;
            c.a = 1 - states[i];
            Core.batch.setColor(c);
            Core.batch.draw(rippleRegion, point.x - w/2, point.y - h/2, w, h);
        }
        c.a = oldA;
        Core.batch.setColor(c);
    }

    public void clear() {

    }
}
