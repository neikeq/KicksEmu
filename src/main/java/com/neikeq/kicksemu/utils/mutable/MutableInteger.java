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

    public void set(MutableInteger value) {
        this.value = value.get();
    }

    public void sum(int add) {
        this.value += add;
    }

    public void sum(MutableInteger add) {
        this.value += add.get();
    }
}
