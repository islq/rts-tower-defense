package io.anuke.mindustry.guide.steps;

import io.anuke.mindustry.guide.IGuideDisplay;
import io.anuke.ucore.function.Supplier;
import io.anuke.ucore.scene.Element;

public class FocusUIElementStep extends GuideStep {
    Element element;
    String msg;
    private Supplier<Element> elementSupplier;

    public FocusUIElementStep(Supplier<Element> elementSupplier, String msg) {
        this.elementSupplier = elementSupplier;
        this.msg = msg;
    }

    public FocusUIElementStep(Element element, String msg) {
        this.element = element;
        this.msg = msg;
    }

    @Override
    public void run(IGuideDisplay display) {
        if (element == null) {
            element = elementSupplier.get();
        }
        display.focuseUIElement(element, msg);
    }

    @Override
    public boolean onClick(IGuideDisplay display, float x, float y) {
        finish(display);
        return false;
    }
}
