package com.major.yodaserver.interrupter;

public class EndlessInterrupter implements ServerInterrupter {
    @Override
    public boolean activated() {
        return false;
    }
}
