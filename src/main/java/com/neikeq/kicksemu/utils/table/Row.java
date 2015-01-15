package com.neikeq.kicksemu.utils.table;

import java.util.Arrays;
import java.util.List;

public class Row {
    private List<String> columns;
    int index = 0;

    public String nextColumn() throws IndexOutOfBoundsException {
        return columns.get(index++);
    }

    public Row(String[] row) {
        columns = Arrays.asList(row);
    }
}
