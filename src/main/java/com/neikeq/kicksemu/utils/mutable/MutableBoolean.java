package com.neikeq.kicksemu.utils.mutable;

public class MutableBoolean {

    private boolean value;

    public MutableBoolean(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }
}
