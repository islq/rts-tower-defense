package io.anuke.mindustry.guide;

import com.badlogic.gdx.Gdx;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.content.blocks.DistributionBlocks;
import io.anuke.mindustry.content.blocks.ProductionBlocks;
import io.anuke.mindustry.guide.steps.ActionStep;
import io.anuke.mindustry.guide.steps.CenterMsgStep;
import io.anuke.mindustry.guide.steps.CheckStep;
import io.anuke.mindustry.guide.steps.ClickElementStep;
import io.anuke.mindustry.guide.steps.ClickPowerNodeStep;
import io.anuke.mindustry.guide.steps.ClickTileXYStep;
import io.anuke.mindustry.guide.steps.DelayStep;
import io.anuke.mindustry.guide.steps.FocusUIElementStep;
import io.anuke.mindustry.guide.mission.MissionGuide;
import io.anuke.mindustry.guide.steps.LinkPowerStep;
import io.anuke.mindustry.guide.steps.MineXYStep;
import io.anuke.mindustry.guide.steps.PanToXYForMineStep;
import io.anuke.mindustry.guide.steps.PanToXYStep;
import io.anuke.mindustry.guide.steps.PlaceBlockLineStep;
import io.anuke.mindustry.guide.steps.PlaceBlockXYStep;
import io.anuke.mindustry.input.InputHandler;
import io.anuke.mindustry.input.MobileInput;
import io.anuke.mindustry.maps.missions.Mission;
import io.anuke.mindustry.type.Category;
import io.anuke.mindustry.type.Recipe;
import io.anuke.mindustry.world.Block;

import static java.util.Arrays.asList;

public class MissionGuides {
//    public static MissionGuide firstGuide, copperGuide, drillGuide;

    public static void load() {
//        copperGuide = createCopperGuide();
//        drillGuide = createDrillGuide();
    }

    public static MissionGuide msgGuide(String msg) {
        MissionGuide guide = new MissionGuide();
        guide.addStep(new CenterMsgStep(msg));
        return guide;
    }

    public static MissionGuide mineTileGuide(int tileX, int tileY, String msg) {
        MissionGuide guide = new MissionGuide();
        guide.disableUI();
        if (msg != null) {
            guide.addStep(new CenterMsgStep(msg));
        }
        guide.enableUI();
        guide.addStep(new ClickElementStep(() -> {
            return Vars.control.input(0).getUIElement("cancel");
        }, "点这里退出建筑模式", () -> {
            return !Vars.control.input(0).isPlacing();
        }));
        guide.addStep(new PanToXYForMineStep(tileX, tileY, null));
        guide.addStep(new ActionStep(() -> {
            Vars.ui.disable(true);
        }));
        guide.addStep(new ClickTileXYStep(tileX, tileY, 1, 1, "点这里开始采矿"));
        guide.enableUI();

        return guide;
    }

    public static MissionGuide copperGuide() {
        return new MissionGuide(asList(
                new CenterMsgStep("游戏的目标是保卫我方核心，并摧毁敌人的核心"),
                new MineXYStep(55, 62, 1, 5, "点矿区开始采矿"),
                new ActionStep(() -> {
                    Vars.guide.nextStep();
                }),
                // new DelayStep(1f),
                // new ClickTileXYStep(60, 60, 3, 3, "点[accent]核心[]可以查看当前拥有的物品的种类和数量"),
                // new DelayStep(10f),
//                new FocusUIElementStep(()-> {
//                   return Vars.ui.hudfrag.getTaskInfoElement();
//                }, "task info at here"),
                new CheckStep(() -> {
                    return false;
                }),
                // new DelayStep(1f),
                new ActionStep(() -> {
                    Vars.guide.nextStep();
                })
        ));
    }

    public static MissionGuide placeBlockGuide(Block block, int tileX, int tileY, String msg) {
        return placeBlockGuide(block, tileX, tileY, 0, msg);
    }
    public static MissionGuide placeBlockGuide(Block block, int tileX, int tileY, int rotation, String msg) {
        Recipe recipe = Recipe.getByResult(block);
        Category category = recipe.category;
        MissionGuide guide = new MissionGuide();
        if (msg != null) {
            guide.addStep(new CenterMsgStep(msg));
        }

        guide.addStep(new PanToXYStep(tileX, tileY, "[accent]滑动[]屏幕,机甲[accent]反向[]移动"));

        guide.addStep(new ClickElementStep(() -> {
                    return Vars.ui.hudfrag.blockfrag.getCategoryIcon(category);
                }, "点这里选择建筑分类", () -> {
                    return Vars.ui.hudfrag.blockfrag.getCurrentCategory() == category;
                }));
        guide.addStep(new ClickElementStep(() -> {
                    return Vars.ui.hudfrag.blockfrag.getRecipIcon(recipe);
                }, "点这里选择建筑", () -> {
                    return Vars.control.input(0).recipe != null && Vars.control.input(0).recipe.result == block;
                }));
        guide.addStep(new ClickElementStep(() -> {
                    return Vars.control.input(0).getUIElement("rotate");
                }, "点这里旋转方向", () -> {
                    return !block.rotate || Vars.control.input(0).rotation == rotation;
                }));
        guide.addStep(new PlaceBlockXYStep(block, tileX, tileY,"点这里选择建造位置"));
        guide.addStep(new ClickElementStep(() -> {
                    return Vars.control.input(0).getUIElement("check");
                }, "点这里确认")
        );
        return guide;
    }

    public static MissionGuide placeBlockLineGuide(Block block, int x1, int y1, int x2, int y2, int rotation, String msg) {
        Recipe recipe = Recipe.getByResult(block);
        Category category = recipe.category;
        MissionGuide guide = new MissionGuide();

        if (msg != null) {
            guide.addStep(new CenterMsgStep(msg));
        }

        guide.addStep(new ClickElementStep(() -> {
                    return Vars.ui.hudfrag.blockfrag.getCategoryIcon(category);
                }, "点这里选择建筑分类", () -> {
                    return Vars.ui.hudfrag.blockfrag.getCurrentCategory() == category;
                }));
        guide.addStep(new ClickElementStep(() -> {
                    return Vars.ui.hudfrag.blockfrag.getRecipIcon(recipe);
                }, "点这里选择建筑", () -> {
                    return Vars.control.input(0).recipe != null && Vars.control.input(0).recipe.result == block;
                }));
        guide.addStep(new ClickElementStep(() -> {
                    return Vars.control.input(0).getUIElement("rotate");
                }, "点这里旋转方向", () -> {
                    return !block.rotate || Vars.control.input(0).rotation == rotation;
                }));

        guide.addStep(new PanToXYStep(x1, y1, x2, y2, null));
        guide.addStep(new PlaceBlockLineStep(block, x1, y1, x2, y2, null));

//        int dx = x1 <= x2 ? 1 : -1;
//        int dy = y1 <= y2 ? 1 : -1;
//        for(int x=x1; (dx > 0 ? x<=x2 : x>=x2); x += dx) {
//            for(int y=y1; (dy > 0 ? y<=y2 : y>=y2); y += dy) {
//                int finalX = x;
//                int finalY = y;
//                guide.addStep(new PanToXYStep(x, y, "pan to there"));
//                guide.addStep(new PlaceBlockXYStep(block, x, y, "Place at here"));
//            }
//        }
        guide.addStep(new ClickElementStep(() -> {
                    return Vars.control.input(0).getUIElement("check");
                }, "点这里确认"));

        return guide;
    }

    public static MissionGuide drillGuide(Block block) {
        Recipe recipe = Recipe.getByResult(block);
        Category category = recipe.category;
        return new MissionGuide(asList(
                new CenterMsgStep("$tutorial.drill"),
                new ClickElementStep(() -> {
                    return Vars.ui.hudfrag.blockfrag.getCategoryIcon(Category.production);
                }, "点这里选择建筑分类", () -> {
                    return Vars.ui.hudfrag.blockfrag.getCurrentCategory() ==  Category.production;
                }),
                new ClickElementStep(() -> {
                    return Vars.ui.hudfrag.blockfrag.getRecipIcon(Recipe.getByResult(ProductionBlocks.mechanicalDrill));
                }, "点这里选择建筑", () -> {
                    return Vars.control.input(0).recipe != null && Vars.control.input(0).recipe.result == ProductionBlocks.mechanicalDrill;
                }),
                new ClickTileXYStep(55, 62, 2, 1, "Place drill at here"),
                new ClickElementStep(() -> {
                    return Vars.control.input(0).getUIElement("check");
                }, "点这里确认")
        ));
    }

//    public static MissionGuide firstConveyGuide(int x, int y, int rotation) {
//        return conveyGuide(x, y, rotation).addStep(0, new CenterMsgStep("$tutorial.conveyor"));
//    }

//    public static MissionGuide conveyGuide(int x, int y, int rotation) {
//        return new MissionGuide(asList(
//            new ClickElementStep(() -> {
//                return Vars.ui.hudfrag.blockfrag.getCategoryIcon(Category.distribution);
//            }, "点这里选择建筑分类", () -> {
//                return Vars.ui.hudfrag.blockfrag.getCurrentCategory() ==  Category.distribution;
//            }),
//            new ClickElementStep(() -> {
//                return Vars.ui.hudfrag.blockfrag.getRecipIcon(Recipe.getByResult(DistributionBlocks.conveyor));
//            }, "点这里选择建筑", () -> {
//                return Vars.control.input(0).recipe != null && Vars.control.input(0).recipe.result == DistributionBlocks.conveyor;
//            }),
//            new ClickElementStep(() -> {
//                return Vars.control.input(0).getUIElement("rotate");
//            }, "点这里旋转方向", () -> {
//                return Vars.control.input(0).rotation == rotation;
//            }),
//            new ClickTileXYStep(x, y, 1, 1, "Place conveyor at here"),
//            new ClickElementStep(() -> {
//                return Vars.control.input(0).getUIElement("check");
//            }, "点这里确认")
//
//        ));
//    }

    public static MissionGuide linkPower(int tx1, int ty1, int tx2, int ty2) {
        MissionGuide guide = new MissionGuide();
        guide.enableUI();
        guide.addStep(new ClickElementStep(() -> {
            return Vars.control.input(0).getUIElement("cancel");
        }, "点这里退出建造模式", () -> {
            return !Vars.control.input(0).isPlacing();
        }));
        guide.addStep(new ClickPowerNodeStep(tx1, ty1));
        return guide;
    }

    public static MissionGuide massGuide() {
        MissionGuide guide = new MissionGuide();

        // guide.addStep(new CenterMsgStep("生产系统的优化非常重要，让我们来优化硅的生产线吧"));

        return guide;
    }

    static float battleTargetDelay = 0;
    public static MissionGuide battleGuide() {

        MissionGuide guide = new MissionGuide();

        guide.addStep(new CheckStep(() -> {
            if (Vars.control.input(0).player.target == null) {
                battleTargetDelay = 0;
            } else {
                battleTargetDelay += Gdx.graphics.getDeltaTime();
            }
            return battleTargetDelay > 5f;
        }));
        guide.addStep(new CenterMsgStep("敌人的无人机会自动修理建筑，需要集结足够多的火力才能摧毁敌人的核心"));
        return guide;
    }

    public static MissionGuide daggerGuide() {
        MissionGuide guide = new MissionGuide();
        guide.addStep(new ClickTileXYStep(65, 55, 3, 3, "点空地退出能源链接模式"));
        guide.addStep(new ClickTileXYStep(64, 59, 2, 2, "点尖刀工厂查看状态"));
        guide.addStep(new CenterMsgStep("选中[accent]尖刀工厂[]时\n上方的进度条为[accent]生产进度[]指示和[accent]电力[]状态指示\n下方显示[accent]血量[](生命值)\n满足条件时，尖刀机甲工厂会自动生产[accent]尖刀机甲[]"));
        return guide;
    }
}
