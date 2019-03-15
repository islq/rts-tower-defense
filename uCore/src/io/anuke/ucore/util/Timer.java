package io.anuke.ucore.util;

import io.anuke.ucore.core.Timers;

import java.util.Arrays;

public class Timer{
    float[] times;

    public Timer(int capacity){
        times = new float[capacity];
    }

    public Timer(){
        this(1);
    }

    public boolean get(float time){
        return get(0, time);
    }

    public boolean get(int id, float time){
        if(id >= times.length) throw new RuntimeException("Out of bounds! Max timer size is " + times.length + "!");

        if(Timers.time() - times[id] >= time ||
                Timers.time() < times[id]){ //when 'time travel' happens, reset.
            times[id] = Timers.time();
            return true;
        }else{
            return false;
        }
    }

    public void reset(int id, float time){
        times[id] = Timers.time() - time;
    }

    public void clear(){
        Arrays.fill(times, 0);
    }

    public float getTime(int id){
        return Timers.time() - times[id];
    }

    public float[] getTimes(){
        return times;
    }
}
