package io.anuke.mindustry.maps;

import com.badlogic.gdx.utils.Array;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.content.Items;
import io.anuke.mindustry.content.UnitTypes;
import io.anuke.mindustry.content.blocks.*;
import io.anuke.mindustry.game.Difficulty;
import io.anuke.mindustry.game.EventType.WorldLoadEvent;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.guide.GuideUtils;
import io.anuke.mindustry.guide.MissionGuides;
import io.anuke.mindustry.guide.mission.BlockHelpGuide;
import io.anuke.mindustry.guide.mission.LinkPowerGuide;
import io.anuke.mindustry.guide.mission.MissionGuide;
import io.anuke.mindustry.guide.steps.ActionStep;
import io.anuke.mindustry.guide.steps.CheckStep;
import io.anuke.mindustry.guide.steps.PanToXYStep;
import io.anuke.mindustry.input.InputHandler;
import io.anuke.mindustry.maps.generation.Generation;
import io.anuke.mindustry.maps.missions.*;
import io.anuke.mindustry.type.Item;
import io.anuke.mindustry.type.Recipe;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.ucore.core.Events;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.util.Bundles;

import static io.anuke.mindustry.Vars.*;
import static java.util.Arrays.asList;

/**Just a class for returning the list of tutorial missions.*/
public class TutorialSector{
    private static int droneIndex;

    private static boolean debug = false;
    public static Array<Mission> getMissions(){
        if (debug) {
            droneIndex = 100;
            return Array.with(
                    new ActionMission(() ->{
                        addItem(Items.copper, 800);
                        addItem(Items.lead, 800);
                        addItem(Items.densealloy, 100);
                        addItem(Items.silicon, 100);
                        Vars.guide.nextStep();
                        Vars.guide.nextStep();
                        Vars.state.wavetime = 10;
                        Vars.state.difficulty = Difficulty.insane;
                        // Vars.guide.forceHidePlaceMenu = true;
                    }).setMessage("test mode").addGuide(),

                    new MarkerBlockMission(CraftingBlocks.siliconsmelter, 54, 52).setMessage("$tutorial.siliconsmelter").addGuide(),

                    new ActionMission(() -> {}).setGuide(new BlockHelpGuide(CraftingBlocks.siliconsmelter)),

                    new WaveMission(1, 60, 60).setGuide(new MissionGuide(asList(new CheckStep(() -> false)))), //.setMessage("$tutorial.waves"),

                    new ActionMission(() -> {
                        Vars.guide.nextStep();
                    }).setMessage("test end").addGuide()
            );
        }

        Array<Mission> missions = Array.with(

            new ItemMission(Items.copper, 10).setGuide(MissionGuides.copperGuide()),  //.setMessage("$tutorial.begin"),
            new ActionMission(() -> {
                addItem(Items.copper, 800);
            }),//.setMessage("任务完成，奖励1000铜。\n手动采矿效率太低，可以在矿地上放置钻头来自动采矿。").addGuide(),

            new BlockLocMission(ProductionBlocks.mechanicalDrill, 55, 62).setMessage("$tutorial.drill").addGuide(),
//            new BlockLocMission(ProductionBlocks.mechanicalDrill, 55, 82).setMessage("$tutorial.drill").addGuide(),
//                new ActionMission(() -> {}).setGuide(new BlockHelpGuide(CraftingBlocks.siliconsmelter)),
            // new ActionMission(() -> {
            // }).setMessage("钻头采集的物资需要用[accent]传送带[]自动传送至[accent]核心[]").addGuide(),

//            new LineBlockMission(DistributionBlocks.conveyor, 57, 62, 59, 62, 0).setMessage("$tutorial.conveyor").addGuide(),

//            new BlockLocMission(DistributionBlocks.conveyor, 57, 62, 0).setMessage("$tutorial.conveyor").setShowComplete(false).addGuide(),
//            new BlockLocMission(DistributionBlocks.conveyor, 58, 62, 0).setShowComplete(false).addGuide(),
//            new BlockLocMission(DistributionBlocks.conveyor, 59, 62, 0).setShowComplete(false).addGuide(),

//            new BlockLocMission(DistributionBlocks.conveyor, 60, 62, 3).setMessage("请注意传送带的方向").setShowComplete(false).addGuide(),

            // new ItemMission(Items.copper, 100).setMessage("$tutorial.morecopper"),

            new BlockLocMission(TurretBlocks.duo, 56, 59).setMessage("$tutorial.turret").addGuide(),
            // new BlockLocMission(ProductionBlocks.mechanicalDrill, 55, 60).setMessage("$tutorial.drillturret").addGuide(),

            new ActionMission(() ->
                Timers.runTask(30f, () -> {
                    Runnable r = () -> {
//                        Array<Item> ores = Array.with(Items.copper, Items.coal, Items.lead);
//                        GenResult res = new GenResult();
//                        for(int x = 0; x < world.width(); x++){
//                            for(int y = 0; y < world.height(); y++){
//                                Tile tile = world.tile(x, y);
//                                world.generator.generateTile(res, 0, 0, x, y, true, null, ores);
//                                if(!tile.hasCliffs()){
//                                    tile.setFloor((Floor) res.floor);
//                                }
//                            }
//                        }
                        world.tile(58, 87).setCliffs((byte) 0);
                        world.tile(59, 86).setCliffs((byte) 0);
                        world.tile(60, 85).setCliffs((byte) 0);
                        world.tile(61, 84).setCliffs((byte) 0);
                        world.tile(70, 40).setBlock(Blocks.spawn, Team.blue);
                        Events.fire(new WorldLoadEvent());
                    };

                    if(headless){
                        ui.loadLogic(r);
                    }else{
                        threads.run(r);
                    }
                })),

            new ItemMission(Items.lead, 10)/*.setMessage("$tutorial.lead")*/.setGuide(MissionGuides.mineTileGuide(
                    71, 62, "游戏中还有很多种矿，\n请向[accent]左[]滑动屏幕\n您控制的机甲将向[accent]右[]移动\n发现[accent]铅矿[]，并采集[accent]铅[]"
            )),
            new ActionMission(() -> {
                addItem(Items.lead, 490);
            }),
            // new ItemMission(Items.copper, 250).setMessage("$tutorial.morecopper"),

            new BlockLocMission(CraftingBlocks.smelter, 58, 69).setMessage("$tutorial.smelter").addGuide(),

            //drills for smelter
            new BlockLocMission(ProductionBlocks.mechanicalDrill, 62, 86).addGuide(),
            new BlockLocMission(ProductionBlocks.mechanicalDrill, 58, 89).addGuide(),
            new BlockLocMission(ProductionBlocks.mechanicalDrill, 54, 68).addGuide(),

            //conveyors for smelter
//            new LineBlockMission(DistributionBlocks.conveyor, 58, 88, 58, 70, 3).addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 61, 86, 61, 70, 3).addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 61, 69, 59, 69, 2).addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 56, 69, 57, 69, 0).addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 58, 68, 58, 63, 3).addGuide(),
//            new BlockLocMission(DistributionBlocks.junction, 58, 62, 0).setMessage("当两条传送带交汇时，可以放置一个[accent]连接点[]，使它们互不影响").addGuide(),
//            new BlockLocMission(DistributionBlocks.conveyor, 58, 61, 0).addGuide(),

            // new ItemMission(Items.densealloy, 10).setMessage("$tutorial.densealloy"),
            new ActionMission(() -> {
                addItem(Items.densealloy, 200);
            }),

            new MarkerBlockMission(CraftingBlocks.siliconsmelter, 54, 52).setMessage("$tutorial.siliconsmelter").addGuide(),

            new ActionMission(() -> {}).setGuide(new BlockHelpGuide(CraftingBlocks.siliconsmelter)),

            //coal line
            new BlockLocMission(ProductionBlocks.mechanicalDrill, 47, 52).setMessage("$tutorial.silicondrill").addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 49, 52, 53, 52, 0).addGuide(),

            //sand line
            new BlockLocMission(ProductionBlocks.mechanicalDrill, 53, 49).addGuide(),
            new BlockLocMission(ProductionBlocks.mechanicalDrill, 56, 49).addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 55, 50, 55, 51, 1).addGuide(),

            //silicon line
//            new LineBlockMission(DistributionBlocks.conveyor, 56, 53, 59, 53, 0).addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 60, 53, 60, 58, 1).addGuide(),

            new BlockLocMission(PowerBlocks.combustionGenerator, 49, 54).setMessage("$tutorial.generator").addGuide(),
            new BlockLocMission(ProductionBlocks.mechanicalDrill, 47, 54).setMessage("$tutorial.generatordrill").addGuide(),
            new BlockLocMission(PowerBlocks.powerNode, 52, 54).setMessage("$tutorial.node").addGuide(),

            new ConditionMission(Bundles.get("text.mission.linknode"), () -> world.tile(54, 52).entity != null && world.tile(54, 52).entity.power != null && world.tile(54, 52).entity.power.amount >= 0.01f)
                .setMessage("$tutorial.nodelink").setGuide(new LinkPowerGuide(52, 54).linkTile(49, 54).linkTile(54, 52).addMsg("$tutorial.nodelink")),

            // new ItemMission(Items.silicon, 1).setMessage("$tutorial.silicon").setGuide(MissionGuides.massGuide()),
            new ActionMission(() -> {
//                final InputHandler input = Vars.control.input(0);
//                Recipe drillRecipe = Recipe.getByResult(ProductionBlocks.mechanicalDrill);
//                Recipe conveyorRecipe = Recipe.getByResult(DistributionBlocks.conveyor);
//                Recipe routerRecipe = Recipe.getByResult(DistributionBlocks.router);
//                Recipe combustionGeneratorRecipe = Recipe.getByResult(PowerBlocks.combustionGenerator);

                // test
                GuideUtils.setBlock(CraftingBlocks.siliconsmelter, 54, 52, 0);
                GuideUtils.setBlock(PowerBlocks.combustionGenerator, 49, 54, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 47, 54, 0);
                GuideUtils.setBlock(PowerBlocks.powerNode, 52, 54, 0);

                // sand line
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 53, 47, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 53, 45, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 56, 47, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 56, 45, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 55, 49, 1);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 55, 48, 1);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 55, 47, 1);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 55, 46, 1);

                // power coal line
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 45, 54, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 43, 54, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 41, 54, 0);

                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 47, 57, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 45, 57, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 43, 57, 0);
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 41, 57, 0);

//                GuideUtils.setBlock(DistributionBlocks.conveyor, 42, 56, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 43, 56, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 44, 56, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 45, 56, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 46, 56, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 47, 56, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 48, 56, 0);
//
//                // more combustionGenerator
//                GuideUtils.setBlock(DistributionBlocks.router, 49, 56, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 49, 55, 3);
                GuideUtils.setBlock(PowerBlocks.combustionGenerator, 49, 57, 0);

//                GuideUtils.setBlock(DistributionBlocks.conveyor, 50, 56, 3);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 50, 55, 3);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 50, 54, 3);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 50, 53, 3);

                // coal line
                GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 45, 52, 0);
                // GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 43, 52, 0);
                // GuideUtils.setBlock(ProductionBlocks.mechanicalDrill, 41, 52, 0);

                // GuideUtils.setBlock(DistributionBlocks.conveyor, 42, 51, 0);
                // GuideUtils.setBlock(DistributionBlocks.conveyor, 43, 51, 0);
                // GuideUtils.setBlock(DistributionBlocks.conveyor, 44, 51, 0);
                // GuideUtils.setBlock(DistributionBlocks.conveyor, 45, 51, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 46, 51, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 47, 51, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 48, 51, 0);
//                GuideUtils.setBlock(DistributionBlocks.conveyor, 49, 51, 1);

                // test
                GuideUtils.linkPower(52, 54, 54,52);
                GuideUtils.linkPower(52, 54, 49,54);
                // link power
                GuideUtils.linkPower(52, 54, 49,57);

            }).setMessage("生产系统的优化非常重要，充足的输入才能保证输出\n\n下面新手向导将自动创建更多的钻头来采矿").addGuide(),

            // new ActionMission(() -> {
            //    addItem(Items.silicon, 100);
            // }),

//            new BlockLocMission(DistributionBlocks.router, 60, 53).setMessage("[accent]路由器[]可以将一路输入，分成3路输出，可用来供应炮塔子弹").addGuide(),
            new BlockLocMission(TurretBlocks.duo, 61, 53).setMessage("资源充足时，应该建筑更多的炮塔").addGuide(),
            new BlockLocMission(TurretBlocks.duo, 60, 52).addGuide(),

            new ActionMission(() ->{
                addItem(Items.silicon, 100);
                Vars.state.wavetime = 10;
                Vars.state.difficulty = Difficulty.insane;
                // Vars.guide.forceHidePlaceMenu = true;
                Vars.ui.disable(true);
            }).setMessage("敌人马上要发动进攻了，利用自身的火力和炮塔的火力消灭他们吧").addGuide(),

            new WaveMission(1, 60, 60).setGuide(new MissionGuide(asList(new CheckStep(() -> false), new ActionStep(() -> {
                Vars.ui.disable(false);
            })))), //.setMessage("$tutorial.waves"),

            new BlockLocMission(UnitBlocks.daggerFactory, 64, 59).setMessage("$tutorial.daggerfactory").addGuide(),

            //silicon lines for dagger factory
//            new BlockLocMission(DistributionBlocks.router, 60, 57).setMessage("$tutorial.router").addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 61, 57, 63, 57, 0).addGuide(),
//            new LineBlockMission(DistributionBlocks.conveyor, 64, 57, 64, 58, 1).addGuide(),

            //power for dagger factory
            new BlockLocMission(PowerBlocks.powerNode, 57, 54).addGuide(),
            new BlockLocMission(PowerBlocks.powerNode, 62, 54).setMessage("连续放置能量节点时，如果在链接范围内，新放置的节点会自动链接上一个节点").addGuide(),

            new ConditionMission(Bundles.get("text.mission.linknode"), () -> world.tile(64, 59).entity != null && world.tile(64, 59).entity.power != null && world.tile(64, 59).entity.power.amount >= 0.01f)
                    .setMessage("$tutorial.nodelink").setGuide(new LinkPowerGuide(62, 54).linkTile(64, 59).addMsg("点击能源节点并选择机甲工厂来链接能源")),

//            new ActionMission(() -> {
//                Vars.control.input(0).frag.config.hideConfig();
//            }),

            new UnitMission(UnitTypes.dagger).setMessage("满足条件时，尖刀机甲工厂会自动生产尖刀机甲"/*"$tutorial.dagger"*/).setGuide(MissionGuides.daggerGuide()), //.addGuide(),

            new ActionMission(() -> {
                generateBase();
                Vars.guide.nextStep();
            }).setMessage("敌人的核心在右上角，尖刀机甲会自动寻找敌方核心，跟它一起去摧毁敌人的核心吧").addGuide(),
            new BattleMission(){
                public void generate(Generation gen){} //no
                // public void onBegin(){} //also no
            }.setMessage("$tutorial.battle").setGuide(MissionGuides.battleGuide()),
            new ActionMission(() -> {
                Vars.guide.nextStep();
            }).setMessage("我能教你的就只有这么多了，剩下的就要靠你自己去探索了，留在这里四处看看，或者马上挑战[accent]下一关[]吧\n\n提示：\n从主菜单的[accent]新手教程[]进入，可以重玩新手教程").addGuide()
        );

        //find drone marker mission
        for(int i = 0; i < missions.size; i++){
            if(missions.get(i) instanceof MarkerBlockMission){
                droneIndex = i;
                break;
            }
        }
        return missions;
    }

    public static boolean supressDrone(){
        return world.getSector() != null && world.getSector().x == 0 && world.getSector().y == 0 && world.getSector().completedMissions < droneIndex;
    }

    private static void generateBase(){
        int x = sectorSize - 50, y = sectorSize - 50;
        world.setBlock(world.tile(x, y), StorageBlocks.core, waveTeam);
        world.setBlock(world.tile(x - 1, y + 2), UnitBlocks.daggerFactory, waveTeam);
        world.setBlock(world.tile(x - 1, y - 3), UnitBlocks.daggerFactory, waveTeam);

        //since placed() is not called here, add core manually
        state.teams.get(waveTeam).cores.add(world.tile(x, y));
    }

    private static class MarkerBlockMission extends BlockLocMission{
        public MarkerBlockMission(Block block, int x, int y){
            super(block, x, y);
        }
    }

    public static void addItem(Item item, int num) {
        for(Tile tile : state.teams.get(defaultTeam).cores){
            tile.entity.items.add(item, num);
        }
    }

}
