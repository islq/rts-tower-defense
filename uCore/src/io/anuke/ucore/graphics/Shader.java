package io.anuke.ucore.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shader{
    /** Whether to fallback on the default shader when things fail. */
    public static boolean useFallback = true;

    public final String frag, vert;
    public boolean isFallback;
    public ShaderProgram shader;
    public TextureRegion region;

    public Shader(String frag, String vert){
        ShaderProgram.pedantic = false;
        this.frag = frag;
        this.vert = vert;
        reload();
    }

    public Shader(String frag){
        this(frag, "default");
    }

    public void reload(){
        if(this.shader != null) this.shader.dispose();
        this.shader = new ShaderProgram(Gdx.files.internal("shaders/" + vert + ".vertex"),
                Gdx.files.internal("shaders/" + frag + ".fragment"));

        if(!shader.isCompiled()){
            if(useFallback){
                Gdx.app.error("Shaders", "Failed to load shaders\"" + frag + "\" and \"" + vert + "\", using fallback: " + shader.getLog());
                isFallback = true;
                shader = SpriteBatch.createDefaultShader();
            }else{
                throw new RuntimeException("Error compiling shaders \"" + frag + "\" and \"" + vert + "\": " + shader.getLog());
            }
        }else{
            isFallback = false;
        }

        if(shader.getLog().length() > 0)
            Gdx.app.error("Shaders", "Shader Log (" + frag + "/" + vert + "): " + shader.getLog());
    }

    protected void apply(){
    }

    public void applyParams(){
        if(!isFallback){
            apply();
        }
    }

    public ShaderProgram program(){
        return shader;
    }
}
