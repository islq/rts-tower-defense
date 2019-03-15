package io.anuke.mindustry.ui.dialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import io.anuke.mindustry.core.Platform;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.ucore.scene.ui.Dialog;

public class DiscordDialog extends Dialog{

    public DiscordDialog(){
        super("", "dialog");

        float h = 70f;

        content().margin(12f);

        Color color = Color.valueOf("7289da");

        content().table(t -> {
            t.background("button").margin(0);

            t.table(img -> {
                img.addImage("white").height(h - 5).width(40f).color(color);
                img.row();
                img.addImage("white").height(5).width(40f).color(color.cpy().mul(0.8f, 0.8f, 0.8f, 1f));
            }).expandY();

            t.table(i -> {
                i.background("button");
                i.addImage("icon-discord").size(14 * 3);
            }).size(h).left();

            t.add("加入官方QQ群: " + Platform.instance.getQQ()).color(Palette.accent).growX().padLeft(10f);
        }).size(470f, h).pad(10f);

        buttons().defaults().size(170f, 50);

        buttons().addButton("$text.back", this::hide);
        buttons().addButton("复制号码", () -> {
            Gdx.app.getClipboard().setContents(Platform.instance.getQQ());
        });
        buttons().addButton("打开QQ群", () -> {
            Platform.instance.joinQQ();
        });
    }
}
