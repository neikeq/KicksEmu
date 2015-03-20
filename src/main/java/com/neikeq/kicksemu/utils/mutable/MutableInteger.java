package com.neikeq.kicksemu.utils.mutable;

public class MutableInteger {

    private int value;

    public MutableInteger(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;
    }

    public void sum(int add) {
        this.value += add;
    }
}
