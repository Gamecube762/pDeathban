package com.github.Gamecube762.pDeathBan;

/**
 * Created by Gamecube762 on 3/29/2015.
 */
public enum TimeScale {
    SECONDS(1000L),
    MINUTES(60000L),
    HOURS(3600000L),
    DAYS(86400000L),
    WEEKS(604800000L),  //7 days
    MONTHS(2592000000L),//30 days
    YEARS(31536000000L),//365 days
    DECADES(315360000000L),//~10 yrs
    CENTURIES(3153600000000L);//~100 yrs

    private long scale;

    TimeScale(long scale) {
        this.scale = scale;
    }

    public long getScale() {
        return scale;
    }

    public static TimeScale fromString(String s) {
        try {return valueOf(s.toUpperCase());}
        catch (IllegalArgumentException ex) {return null;}
    }

    public static long add(Long time, TimeScale ts) {
        return time + ts.getScale();
    }

    public static long scale(int i, TimeScale ts) {
        return i * ts.getScale();
    }

    public static int scaleDown(Long time, TimeScale ts) {
        return (int) (time / ts.getScale());
    }
}
