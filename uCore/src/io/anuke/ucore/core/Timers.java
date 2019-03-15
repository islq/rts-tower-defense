package io.anuke.ucore.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer.Task;
import io.anuke.ucore.function.DelayRun;
import io.anuke.ucore.util.Pooling;

public class Timers{
    private static double time;
    private static DelayedRemovalArray<DelayRun> runs = new DelayedRemovalArray<>();
    private static LongArray marks = new LongArray();
    private static DeltaProvider deltaimpl = () -> Math.min(Gdx.graphics.getDeltaTime() * 60f, 3f);

    public static synchronized void run(float delay, Runnable r){
        DelayRun run = Pooling.obtain(DelayRun.class, DelayRun::new);
        run.finish = r;
        run.delay = delay;
        runs.add(run);
    }

    public static synchronized void runTask(float delay, Runnable r){
        Timer.schedule(new Task(){
            @Override
            public void run(){
                r.run();
            }
        }, delay / 60f);
    }

    public static float time(){
        return (float)time;
    }

    public static void resetTime(float time){
        Timers.time = time;
    }

    public static void mark(){
        marks.add(TimeUtils.nanoTime());
    }

    /** A value of -1 means mark() wasn't called beforehand. */
    public static float elapsed(){
        if(marks.size == 0){
            return -1;
        }else{
            return TimeUtils.timeSinceNanos(marks.pop()) / 1000000f;
        }
    }

    /** Use normal delta time (e. g. gdx delta * 60) */
    public static synchronized void update(){
        float delta = delta();

        time += delta;

        runs.begin();

        for(DelayRun run : runs){
            run.delay -= delta;

            if(run.run != null)
                run.run.run();

            if(run.delay <= 0){
                if(run.finish != null)
                    run.finish.run();
                runs.removeValue(run, true);
                Pooling.free(run);
            }
        }

        runs.end();
    }

    public static synchronized void clear(){
        runs.clear();
    }

    public static float delta(){
        return deltaimpl.get();
    }

    public static void setDeltaProvider(DeltaProvider impl){
        deltaimpl = impl;
    }

    static void dispose(){
        runs.clear();
    }

    public interface DeltaProvider{
        float get();
    }
}
