package io.anuke.ucore.scene.event;

public class VisibilityEvent extends Event{
    private boolean hide;

    public VisibilityEvent(){
    }

    public VisibilityEvent(boolean hide){
        this.hide = hide;
    }

    public boolean isHide(){
        return hide;
    }
}
