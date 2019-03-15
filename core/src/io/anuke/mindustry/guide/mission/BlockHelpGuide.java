package io.anuke.mindustry.guide.mission;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.steps.CheckStep;
import io.anuke.mindustry.guide.steps.ClickElementStep;
import io.anuke.mindustry.type.Category;
import io.anuke.mindustry.type.Recipe;
import io.anuke.mindustry.world.Block;

public class BlockHelpGuide extends MissionGuide {
    public BlockHelpGuide(Block block) {
        super();
        enableUI();
        Recipe recipe = Recipe.getByResult(block);
        Category category = recipe.category;
        addMsg("需要查看建筑的详细信息时，可以点击右上角的 [accent]?[] 图标");
        addStep(new ClickElementStep(() -> {
            return Vars.ui.hudfrag.blockfrag.getCategoryIcon(category);
        }, "点这里选择建筑分类", () -> {
            return Vars.ui.hudfrag.blockfrag.getCurrentCategory() == category;
        }));
        addStep(new ClickElementStep(() -> {
            return Vars.ui.hudfrag.blockfrag.getRecipIcon(recipe);
        }, "点这里选择建筑", () -> {
            return Vars.control.input(0).recipe != null && Vars.control.input(0).recipe.result == block;
        }));
        addStep(new ClickElementStep(() -> {
            return Vars.ui.hudfrag.blockfrag.getHelpIcon();
        }, "点击问号查看详细信息", () -> {
            return Vars.ui.content.hasParent();
        }));
        addStep(new CheckStep(true,0, () -> {
            return !Vars.ui.content.hasParent();
        }));
    }
}
