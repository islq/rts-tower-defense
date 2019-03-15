package io.anuke.mindustry.guide;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Expo;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.core.GameState;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.mindustry.guide.utween.ElementAccessor;
import io.anuke.mindustry.ui.dialogs.SkipDialog;
import io.anuke.mindustry.ui.fragments.Fragment;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.Group;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.style.Drawable;
import io.anuke.ucore.scene.style.NinePatchDrawable;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.Label;
import io.anuke.ucore.scene.ui.TextButton;
import io.anuke.ucore.scene.ui.layout.Cell;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.scene.ui.layout.Unit;

import static io.anuke.mindustry.Vars.guide;
import static io.anuke.mindustry.Vars.state;
import static io.anuke.mindustry.Vars.ui;

public class GuideFragment extends Fragment implements IGuideDisplay {

    private float fingerSize = 60f;
    private float fingerMovDis = 5f;
    private float panDistance = 120;
    private float rippleSize = 200f;

    private final int rippleNum = 3;
    private final float fingerTweenDuration = 2f;
    private final float fingerPanDuration = 1f;// * 2;
    private final float fingerTweenDelay = 1f;

    private final TweenManager tweenManager;
    private final GuideMaskRenderer maskRenderer;

    private Group root;
    private Table msgTable;
    private Image fingerImg;
    private Image[] rippleImg;
    private Table fingerMsgTable;
    private Label fingerMsgLabel;

    private boolean showMask;
    private boolean showMsg;
    private boolean showFinger;
    private boolean showFingerMsg;


    private float fingerX, fingerY;
//    private float fingerDx, fingerDy;

    public GuideFragment(TweenManager tweenManager, GuideMaskRenderer maskRenderer) {
        this.tweenManager = tweenManager;
        this.maskRenderer = maskRenderer;
    }

    @Override
    public void build(Group parent) {
        root = parent;
        parent.fill(tbl -> {
            tbl.addRect((x, y, w, h) -> {
                TextureRegion mask = maskRenderer.getRegion();
                if (mask != null) {
                    Core.batch.draw(mask, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                }
            }).grow().visible(() -> showMask);
        });

        parent.fill(cont -> {
            cont.top().left().visible(() -> guide.inGuideMode());
            cont.table("flat", select -> {
                float isize = 58;
                select.defaults().size(isize).left();
                select.addImage("icon-quit").size(32);

                select.label(() -> "退出教程").size(58 * 2, 58).center().getElement().setAlignment(Align.center);
                select.clicked(guide::showSkipDlg);
            });
        });

        msgTable = new Table();
        msgTable.visible(() -> showMsg);
        parent.addChild(msgTable);

        // msgTable.setDebug(true, true);
        msgTable.setFillParent(true);

        float scl = Unit.dp.scl(1);
        fingerSize *= scl;
        fingerMovDis *= scl;
        rippleSize *= scl;
        panDistance *= scl;

        fingerImg = new Image("finger");
        fingerImg.setSize(fingerSize, fingerSize);
        fingerImg.setOrigin(Align.center);
        root.addChild(fingerImg);
        fingerImg.visible(() -> showFinger);

        rippleImg = new Image[rippleNum];
        for(int i = 0; i<rippleNum; i++) {
            rippleImg[i] = new Image("ripple");
            rippleImg[i].setSize(rippleSize, rippleSize);
            rippleImg[i].setOrigin(Align.center);
            root.addChild(rippleImg[i]);
            rippleImg[i].visible(() -> showFinger);
        }

        // fingerMsgTable = new Table("inventory");// new Table("guide_msg_bg_f");
        fingerMsgTable = new Table("guide_msg_bg_f");
//        fingerMsgTable.setTransform(true);
//        fingerMsgTable.setWidth(50f);
//        fingerMsgTable.defaults().size(50f);
//        fingerMsgTable.setWidth(100f);
        Cell<Label> msgCell = fingerMsgTable.labelWrap("").pad(5f, 5f, 25f, 5f).width(200f).fill();

        fingerMsgLabel = msgCell.getElement();

        root.addChild(fingerMsgTable);
        fingerMsgTable.visible(() -> showFingerMsg);
        fingerMsgTable.clicked(() -> showFingerMsg = false);
        fingerMsgTable.setTouchable(() -> showMask ? Touchable.disabled : Touchable.enabled);
//        fingerMsgTable.setDebug(true);
    }

    public void clearMsg() {
        showMsg = false;
    }

    public void clearMask() {
        showMask = false;
        maskRenderer.clear();
    }

    public void clearFinger() {
        showFinger = false;
        showFingerMsg = false;
    }

    public void clearAll() {
        clearMask();
        clearMsg();
        clearFinger();
        tweenManager.killAll();
    }

    private Vector2 getV2Center(float x, float y) {
        int cx = Gdx.graphics.getWidth() / 2;
        int cy = Gdx.graphics.getHeight() / 2 ;

        float dx = x >= cx ? 1 : -1;
        float dy = y >= cy ? 1 : -1;
        return new Vector2(dx, dy);
    }

    @Override
    public void showFingerMsgAt(String s, float x, float y) {
        if (s != null) {
            fingerMsgLabel.setText(s);
        } else {
            showFingerMsg = false;
            return;
        }
        fingerMsgTable.pack();

        Vector2 v = getV2Center(x, y);

        final float dis = fingerImg.getWidth();// 100;
        int align = Align.topRight;

        if (v.x > 0 && v.y > 0) {
            align = Align.topRight;
        } else if (v.x < 0 && v.y > 0) {
            align = Align.topLeft;
        } else if (v.x <0 && v.y < 0) {
            align = Align.bottomLeft;
        } else {
            align = Align.bottomRight;
        }

        fingerMsgTable.setPosition(x/* + v.x * dis*/, y - v.y * dis, align);
        showFingerMsg = true;
    }

    @Override
    public void showFingerAt(float x, float y) {
        Vector2 v = getV2Center(x, y);
        int align = Align.topLeft;
        int degree = 0;

        fingerImg.setOrigin(Align.topLeft);
        fingerImg.setScale(1);

        if (v.x >= 0 && v.y >= 0) {
            degree = 270;
            align = Align.topRight;
        } else if (v.x >= 0 && v.y < 0) {
            degree = 180;
            align = Align.bottomRight;
        } else if (v.x < 0 && v.y < 0) {
            degree = 90;
            align = Align.bottomLeft;
        } else {
            degree = 0;
            align = Align.topLeft;
        }

        fingerImg.setOrigin(Align.center);
        fingerImg.setRotation(degree);
        fingerImg.setPosition(x - v.x * fingerMovDis / 2, y - v.y * fingerMovDis / 2, align);
        fingerX = x;
        fingerY = y;

        tweenManager.killTarget(fingerImg);
        float timeFinerIn = (fingerTweenDuration - fingerTweenDelay) / 2;
        Timeline.createSequence()
                .push(Tween.set(fingerImg, ElementAccessor.OPACITY).target(1))
                .push(Tween.set(fingerImg, ElementAccessor.SCALE_XY).target(1, 1))
                .push(Timeline.createParallel()
                        .push(Tween.to(fingerImg, ElementAccessor.POS_XY, timeFinerIn).targetRelative(v.x * fingerMovDis, v.y * fingerMovDis).ease(Expo.IN))
//                            .push(Tween.to(fingerImg, ElementAccessor.SCALE_XY, 0.6f).targetRelative(0.9f, 0.9f).ease(Expo.IN))
                        .repeatYoyo(1, 0)
                )
                .repeat(Tween.INFINITY, fingerTweenDelay)
                .start(tweenManager);

        Timeline rippleTimeLine = Timeline.createSequence().delay(timeFinerIn);

        rippleTimeLine.beginParallel();
        for(int i=0; i<rippleNum; i++) {
            float startDelay = 0;
            float runTime = 0;
            float endDelay = 0;
            Image img = rippleImg[i];
            img.setPosition(x, y, Align.center);
            Color c = img.getColor();
            img.setColor(c.r, c.g, c.b, 0);
            tweenManager.killTarget(img);
            float playTime = 0.3f * (i + 1);
            rippleTimeLine.push(Timeline.createSequence()
                    .push(Tween.set(img, ElementAccessor.SCALE_XY).target(0f, 0f))
                    .push(Tween.set(img, ElementAccessor.OPACITY).target(0f))
//                    .pushPause(timeFinerIn)
                    .push(Tween.set(img, ElementAccessor.OPACITY).target(1f))
                    .beginParallel()
                        .push(Tween.to(img, ElementAccessor.SCALE_XY, playTime).target(1f, 1f))
                        .push(Tween.to(img, ElementAccessor.OPACITY, playTime).target(0f))
                    .end()
                    .pushPause(fingerTweenDuration - playTime)
            );
        }
        rippleTimeLine.end();
        rippleTimeLine.repeat(Tween.INFINITY, 0);
        rippleTimeLine.start(tweenManager);

        showFinger = true;
    }

    @Override
    public void updateFocus(float x, float y) {
        if (fingerX != x || fingerY != y) {
            if (showFinger) {
                showFingerAt(x, y);
            }
            if (showFingerMsg) {
                showFingerMsgAt(null, x, y);
            }
        }
    }

    @Override
    public void focuseUIElement(Element element, String msg) {
        showMask = true;
        Vector2 pos = element.localToStageCoordinates(new Vector2(0, 0));

        maskRenderer.setFocusElement(element);
        showFingerMsg = true;
        showFingerMsgAt(msg, pos.x + element.getWidth() / 2, pos.y + element.getHeight() / 2);
    }

    @Override
    public void showFingerPan(Vector2 v, String msg) {
        int x = Gdx.graphics.getWidth()/2;
        int y = Gdx.graphics.getHeight()/2;
        int align = Align.topLeft;
        int degree = 0;

        if (v.x >= 0 && v.y >= 0) {
            degree = 270;
            align = Align.topRight;
        } else if (v.x >= 0 && v.y < 0) {
            degree = 180;
            align = Align.bottomRight;
        } else if (v.x < 0 && v.y < 0) {
            degree = 90;
            align = Align.bottomLeft;
        } else {
            degree = 0;
            align = Align.topLeft;
        }

        showFinger = true;
        fingerImg.setOrigin(align);
        fingerImg.setRotation((degree + 180)%360);
        fingerImg.setPosition(x, y);

        if (msg != null) {
            showFingerMsg = true;
            showFingerMsgAt(msg, x, y);
        } else {
            showFingerMsg = false;
        }

        tweenManager.killTarget(fingerImg);
        for(int i=0; i<rippleNum; i++) {
            Image img = rippleImg[i];
            Color c = img.getColor();
            img.setColor(c.r, c.g, c.b, 0);
            tweenManager.killTarget(img);
        }

        Timeline.createSequence()
                .push(Tween.set(fingerImg, ElementAccessor.POS_XY).target(x - v.x * panDistance, y - v.y * panDistance))
                .push(Tween.set(fingerImg, ElementAccessor.OPACITY).target(1))
                .push(Tween.set(fingerImg, ElementAccessor.SCALE_XY).target(1, 1))
//                .beginParallel()
//                    .push(Tween.to(fingerImg, ElementAccessor.SCALE_XY, 0.15f).target(0.9f, 0.9f))
//                    .push(Tween.to(fingerImg, ElementAccessor.POS_XY, 0.15f).targetRelative(fingerMovDis/2, fingerMovDis/2))
//                .end()
                .push(Tween.to(fingerImg, ElementAccessor.POS_XY, fingerPanDuration).target(x + v.x * panDistance, y + v.y * panDistance))
                .push(Tween.set(fingerImg, ElementAccessor.OPACITY).target(0))
                .repeat(Tween.INFINITY, fingerTweenDelay * 2f)
                .start(tweenManager);
    }

    @Override
    public void showMessage(String s) {
        msgTable.setTouchable(Touchable.enabled);
        msgTable.clear();

        msgTable.table(t -> {
            t.background("guide_msg_bg_c");
            t.labelWrap(s).growX().pad(50f);
        }).growX();

        showMsg = true;
        msgTable.pack();
    }
}
