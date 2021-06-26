package com.camp.campusmvp.SwuTask;

/**
 * Created by Administrator on 2021/6/26.
 */

public class Flags {

    private static Flags ourInstance = new Flags();

    private boolean HAS_LOGED = false;

    public boolean isHAS_LOGED(){
        return HAS_LOGED;
    }

    public void setHAS_LOGED(boolean HAS_LOGED) {
        this.HAS_LOGED = HAS_LOGED;
    }

    public static Flags getInstance(){
        return ourInstance;
    }

    private Flags(){

    }
}
