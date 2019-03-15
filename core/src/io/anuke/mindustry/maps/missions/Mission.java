package io.anuke.mindustry.maps.missions;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import io.anuke.mindustry.Vars;
import io.anuke.mindustry.game.GameMode;
import io.anuke.mindustry.game.SpawnGroup;
import io.anuke.mindustry.game.UnlockableContent;
import io.anuke.mindustry.guide.mission.MissionGuide;
import io.anuke.mindustry.maps.Sector;
import io.anuke.mindustry.maps.generation.Generation;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.util.Bundles;

import static io.anuke.mindustry.Vars.*;

public abstract class Mission{
    protected String extraMessage;
    private boolean showComplete = true;
    protected MissionGuide guide;

    public abstract boolean isComplete();

    /**Returns the string that is displayed in-game near the menu.*/
    public abstract String displayString();

    /**Returns the info string displayed in the sector dialog (menu)*/
    public String menuDisplayString(){
        return displayString();
    }

    public String getIcon(){
        return "icon-mission-defense";
    }

    public GameMode getMode(){
        return GameMode.noWaves;
    }

    /**Sets the message displayed on mission begin. Returns this mission for chaining.*/
    public Mission setMessage(String message){
        this.extraMessage = message;
        return this;
    }

    public Mission setGuide(MissionGuide guide) {
        this.guide = guide;
        guide.setTask(this);
        return this;
    }

    public Mission addGuide() {
        return this;
    }

    public boolean isGuideCompleted() {
        return guide == null || guide.completed();
    }

    public Mission setShowComplete(boolean complete){
        this.showComplete = complete;
        return this;
    }

    /**Called when a specified piece of content is 'used' by a block.*/
    public void onContentUsed(UnlockableContent content){

    }

    /**Draw mission overlay.*/
    public void drawOverlay(){

    }

    public void update(){

    }

    public void reset(){

    }

    /**Shows the unique sector message.*/
    public void showMessage(){
        if(!headless && extraMessage != null){
            ui.hudfrag.showTextDialog(extraMessage);
        }
    }

    public boolean hasMessage(){
        return extraMessage != null;
    }

    public void onBegin(){
        initGuide();
        showGuide();
        //Timers.runTask(60f, this::showGuide);
    }

    public void initGuide() {
        if(!headless && guide != null){
            guide.init();
        }
    }

    public void showGuide(){
        if(!headless && guide != null){
            Vars.guide.showMissionGuide(guide);
        }
    }

    public void onComplete(){
        if(showComplete && !headless){
            threads.runGraphics(() -> ui.hudfrag.showToast("[LIGHT_GRAY]"+menuDisplayString() + ":\n" + Bundles.get("text.mission.complete")));
        }
    }

    public void display(Table table){
        table.add(displayString());
    }

    public Array<SpawnGroup> getWaves(Sector sector){
        return new Array<>();
    }

    public Array<GridPoint2> getSpawnPoints(Generation gen){
        return Array.with();
    }

    public void generate(Generation gen){}
}
