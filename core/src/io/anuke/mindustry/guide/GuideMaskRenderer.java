package io.anuke.mindustry.guide;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.ui.layout.Unit;

import static io.anuke.mindustry.Vars.tilesize;

public class GuideMaskRenderer implements Disposable{

    // region pixmap
    private Pixmap pixmap;
    private Texture texture;
    private TextureRegion region;
    static private final int colorMask = Color.rgba8888(Color.valueOf("000000C0"));
    static private final int colorFocus = Color.rgba8888(Color.valueOf("00000000"));
    static private final float zoom = 4;
    private int mapWidth;
    private int mapHeigh;
    private float mapFocusR;
    private Shape2D mapFocusShape;
    // endregion

    // region screen
    private float focusDpSize = 100;
    private float rippleDpSize = 80;
    private Shape2D focusShape;
    private float focusR;
//    private float focusX;
//    private float focusY;
    // endregion

    // region status value
    private Element focusElement;
    private Tile focusTile;
    private int screenWidth;
    private int screenHeight;
    // endregion

    public GuideMaskRenderer(){
    }

    public Texture getTexture(){
        return texture;
    }

    public TextureRegion getRegion() {
        return region;
    }

    private void focus(float x, float y) {
        focus(new Circle(x, y, focusR));
    }

    private void focus(Circle circle) {
//        focusX = circle.x;
//        focusY = circle.y;
        focusShape = circle;
        mapFocusShape = new Circle(circle.x / zoom, circle.y / zoom, circle.radius / zoom);
    }

    private void focus(Rectangle rectangle) {
        focusShape = rectangle;
        mapFocusShape = new Rectangle(rectangle.x / zoom, rectangle.y / zoom, rectangle.width / zoom, rectangle.height / zoom);
    }

    public void clear() {
        mapFocusShape = null;
        focusElement = null;
    }


    public void resize(int width, int height){
        if(pixmap != null){
            pixmap.dispose();
            texture.dispose();
        }
        screenWidth = width;
        screenHeight = height;
        mapWidth = (int) (width/zoom);
        mapHeigh = (int) (height/zoom);
        focusR = Unit.dp.scl(focusDpSize);
        mapFocusR =  focusR / zoom;

        pixmap = new Pixmap(mapWidth, mapHeigh, Format.RGBA8888);
        texture = new Texture(pixmap);
        region = new TextureRegion(texture);
        updateMask();
    }

    public void updateMask(){
        if (focusElement != null) {
            Vector2 pos = focusElement.localToStageCoordinates(new Vector2(0, 0));
            focus(new Rectangle(pos.x, pos.y, focusElement.getWidth(), focusElement.getHeight()));
        } else if (focusTile != null) {
            Vector2 v = Graphics.screen(focusTile.drawx() + focusTile.block().size * tilesize / 2f, focusTile.drawy() + focusTile.block().size * tilesize / 2f);
            focus(v.x, v.y);
        } else {
            return;
        }

        pixmap.setBlending(Pixmap.Blending.None);
        for(int x = 0; x < mapWidth; x++){
            for(int y = 0; y < mapHeigh; y++){
                pixmap.drawPixel(x, mapHeigh - 1 - y, (_focused(x, y) ? colorFocus : colorMask));
            }
        }
        pixmap.setBlending(Pixmap.Blending.SourceOver);
        texture.draw(pixmap, 0, 0);
    }

    public void draw() {
    }

    // screen pos
    public boolean mouseFocused(Vector2 point) {
        boolean f = focusShape != null && focusShape.contains(point);
        return f;
    }

    // zoomed pos
    private boolean _focused(float x, float y){
        return mapFocusShape != null && mapFocusShape.contains(x, y);
    }

    @Override
    public void dispose(){
        pixmap.dispose();
        texture.dispose();
        texture = null;
        pixmap = null;
        clear();
    }

    // region set status
    public void setFocusElement(Element focusElement) {
        this.focusElement = focusElement;
        this.focusTile = null;
        updateMask();
    }

    public void setFocusTile(Tile tile) {
        this.focusTile = tile;
        this.focusElement = null;
        updateMask();
    }
    // endregion

}
