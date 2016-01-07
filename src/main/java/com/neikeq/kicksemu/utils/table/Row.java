package com.neikeq.kicksemu.utils.table;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Row {

    private final List<String> columns;
    private int index;

    private boolean hasColumn(int index) {
        return (index >= 0) && (columns.size() > index);
    }

    public String lastColumnIndex() {
        return String.valueOf(index - 1);
    }

    public void ignoreColumn() {
        index++;
    }

    public void ignoreColumns(int count) {
        index += count;
    }

    public boolean hasNext() {
        return hasColumn(index);
    }

    public Optional<String> nextColumn() throws IndexOutOfBoundsException {
        return hasColumn(index) ? Optional.of(columns.get(index++)) : Optional.empty();
    }

    public Optional<String> columnAt(int index) throws IndexOutOfBoundsException {
        return hasColumn(index) ? Optional.of(columns.get(index)) : Optional.empty();
    }

    public Row(String[] row) {
        columns = Arrays.asList(row);
    }
}
