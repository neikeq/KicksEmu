package com.neikeq.kicksemu.utils.table;

import java.util.Arrays;
import java.util.List;

public class Row {
    private List<String> rows;
    int index = 0;

    public String nextRow() throws IndexOutOfBoundsException {
        return rows.get(index++);
    }

    public Row(String[] row) {
        rows = Arrays.asList(row);
    }
}
