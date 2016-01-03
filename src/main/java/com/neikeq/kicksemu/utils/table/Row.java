package com.neikeq.kicksemu.utils.table;

import java.util.Arrays;
import java.util.List;

public class Row {

    private final List<String> columns;
    private int index;

    public void ignoreColumn() {
        index++;
    }

    public void ignoreColumns(int count) {
        index += count;
    }

    public boolean hasNext() {
        return columns.size() > index;
    }

    public String nextColumn() throws IndexOutOfBoundsException {
        return columns.get(index++);
    }

    public String columnAt(int idx) throws IndexOutOfBoundsException {
        return columns.get(idx);
    }

    public Row(String[] row) {
        columns = Arrays.asList(row);
    }
}
