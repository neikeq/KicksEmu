package com.neikeq.kicksemu.utils.table;

import java.util.Arrays;
import java.util.List;

public class Column {
    private List<String> rows;
    int index = 0;

    public String nextRow() {
        return rows.get(index++);
    }

    public Column(String line) {
        rows = Arrays.asList(line.split(","));
    }
}