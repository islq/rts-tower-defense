package io.anuke.ucore.lsystem;

import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;

public class EvolutionData{

    public IEvaluator eval = null;

    public float defaultSpace = 35f;
    public boolean changeSpace = true;

    public int variants = 10;
    public int generations = 40;
    public int maxMutations = 5;
    public int iterations = 3;

    public char[] insertChars = {'+', '-', 'F', 'X'};
    public String axiom = "FX";
    public HashMap<Character, String> defaultRules = new HashMap<Character, String>(){{
        put('X', "XF");
        put('F', "FF");
    }};

    public boolean limitrulesize = false;
    public int maxrulesize = 50;

    public Color start = Color.BLACK, end = Color.WHITE;
    public float thickness = 1f;
    public float swayspace = 0f, swayphase = 0f, swayscale = 4f;
    public float length = 4f;

}
