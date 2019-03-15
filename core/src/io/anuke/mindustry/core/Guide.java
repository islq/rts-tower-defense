package io.anuke.mindustry.core;

import com.badlogic.gdx.Gdx;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.GuideFragment;
import io.anuke.mindustry.guide.GuideManager;
import io.anuke.mindustry.guide.GuideMaskRenderer;
import io.anuke.mindustry.guide.mission.MissionGuide;
import io.anuke.mindustry.guide.utween.ElementAccessor;
import io.anuke.mindustry.ui.dialogs.SkipDialog;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.Group;
import io.anuke.ucore.scene.Scene;
import io.anuke.ucore.scene.event.Event;
import io.anuke.ucore.scene.event.EventListener;
import io.anuke.ucore.scene.event.InputEvent;

import static io.anuke.mindustry.Vars.state;
import static io.anuke.mindustry.Vars.ui;
import static io.anuke.mindustry.Vars.world;
import static io.anuke.ucore.scene.event.InputEvent.Type.touchDown;

public class Guide extends Module {
    static public boolean debug = false;

    private Scene guideScene;
    private GuideMaskRenderer maskRenderer;
    private GuideFragment fragment;
    private GuideManager manager;
    private MissionGuide currentMissionGuide;

    private final TweenManager tweenManager = new TweenManager();

    private boolean inited = false;
    public boolean forceHidePlaceMenu = false;

    public boolean inGuideMode() {
        return inited && currentMissionGuide != null;
    }

    @Override
    public void update() {
        super.update();
        if (!inited) return;
        // if (currentMissionGuide == null) return;

        tweenManager.update(Gdx.graphics.getDeltaTime());

        if (currentMissionGuide != null && currentMissionGuide.update(fragment)) {
            currentMissionGuide = null;
        }

        guideScene.act();
        guideScene.draw();
    }

    @Override
    public void init() {
        super.init();
        initUTween();
        guideScene = new Scene(Core.batch);

        maskRenderer = new GuideMaskRenderer();
        fragment = new GuideFragment(tweenManager, maskRenderer);
        fragment.build(guideScene.getRoot());

        manager = new GuideManager(this);
        // manager.init();

        Group root = guideScene.getRoot();
        EventListener listener = new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (currentMissionGuide != null) {
                    boolean consumed = currentMissionGuide.onEvent(fragment, event);
                    if (!consumed && event instanceof InputEvent && ((InputEvent) event).getType() == touchDown) {
                        guideScene.addTouchFocus(this, root, root, 0, 0);
                    }
                    return consumed;
                }
                return false;
            }
        };
        root.addListener(listener);
    }

    private void initUTween() {
        Tween.setWaypointsLimit(10);
        Tween.setCombinedAttributesLimit(3);
        Tween.registerAccessor(Element.class, new ElementAccessor());
    }

    @Override
    public void dispose() {
        super.dispose();
        maskRenderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        guideScene.resize(width, height);
        Gdx.app.postRunnable(() -> {
            maskRenderer.resize(width, height);
            inited = true;
        });
    }

    public void showMissionGuide(MissionGuide guide) {
        Gdx.app.postRunnable(() -> {
            if (currentMissionGuide != null) {
                hideGuide();
            }
            currentMissionGuide = guide;
            Inputs.addProcessor(0, guideScene);
            guide.onBegin(fragment);
        });
    }

    public void hideGuide() {
        currentMissionGuide = null;
        Inputs.removeProcessor(guideScene);
    }

    public boolean noMenu() {
        return manager.needHideMenu();
    }

    public boolean noPlaceMenu() {
        return forceHidePlaceMenu || manager.needHidePlaceMenu();
    }

    public void nextStep() {
        manager.nextStep();
    }

    public boolean noMiniMap() {
        return manager.needHideMiniMap();
    }

    public boolean isPanEnable() {
        if (this.currentMissionGuide != null) {
            return this.currentMissionGuide.isPanEnable();
        }
        return true;
    }

    public boolean isPlaceEnable(Tile tile) {
        if (this.currentMissionGuide != null) {
            return this.currentMissionGuide.isPlaceEnable(tile);
        }
        if (!manager.hasDone()) return false;
        return true;
    }

    public void forceLaunch() {
        manager.init(true);
    }

    public boolean checkLaunch() {
        if (isGuideFinished()) {
            if(manager.hasDone() && world.sectors.getUnlockedSectorCount() < 2) {
                world.sectors.completeSector(0, 0);
            }
            return false;
        } else {
            forceLaunch();
            return true;
        }
    }

    public boolean isGuideFinished() {
        return manager.hasDone() || world.sectors.getUnlockedSectorCount() > 1;
    }

    SkipDialog skipDlg;
    public void showSkipDlg() {
        if (skipDlg == null) {
            skipDlg = new SkipDialog("退出教程", "确定要跳过新手教程吗？如果游戏中有疑问可再次点击主菜单中的【新手教程】按钮开始新手教程", ()->{
                back();
                state.set(GameState.State.menu);
            });
        }
        skipDlg.show(getScene());
    }

    public void back() {
        abort();
        if(!manager.hasDone()) {
            world.getSector().completedMissions = world.getSector().missions.size;
            world.sectors.completeSector(0, 0);
            manager.done();
        }
    }

    public void abort() {
        if(currentMissionGuide != null) {
            currentMissionGuide.abort(fragment);
            ui.disable(false);
            Vars.disableTap = false;
            currentMissionGuide = null;
        }
    }

    public Scene getScene() {
        return guideScene;
    }
}
