package com.github.Gamecube762.pDeathBan;

/**
 * Created by Gamecube762 on 3/29/2015.
 */
public class Group {
    private int lives, time;
    private TimeScale scale;

    public Group(int lives, TimeScale scale, int time) {
        this.lives = lives;
        this.scale = scale;
        this.time = time;
    }

    public int getLives() {
        return lives;
    }

    public int getTime() {
        return time;
    }

    public TimeScale getScale() {
        return scale;
    }
}
