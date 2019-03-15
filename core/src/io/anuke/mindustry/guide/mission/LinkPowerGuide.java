package io.anuke.mindustry.guide.mission;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.guide.steps.ClickElementStep;
import io.anuke.mindustry.guide.steps.ClickPowerNodeStep;
import io.anuke.mindustry.guide.steps.LinkPowerStep;
import io.anuke.mindustry.guide.steps.PanToXYStep;

public class LinkPowerGuide extends MissionGuide {
    private final int tx;
    private final int ty;

    public LinkPowerGuide(int tx, int ty) {
        super();
        enableUI();
        addStep(new ClickElementStep(() -> {
            return Vars.control.input(0).getUIElement("cancel");
        }, "点这里退出建造模式", () -> {
            return !Vars.control.input(0).isPlacing();
        }));
        addStep(new PanToXYStep(tx, ty));
        addStep(new ClickPowerNodeStep(tx, ty));
        this.tx = tx;
        this.ty = ty;
    }

    public LinkPowerGuide linkTile(int tx, int ty) {
        addStep(new PanToXYStep(tx, ty));
        addStep(new LinkPowerStep(tx, ty, this.tx, this.ty));
        return this;
    }
}
