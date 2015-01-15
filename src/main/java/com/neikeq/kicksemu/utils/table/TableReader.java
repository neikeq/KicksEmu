package com.neikeq.kicksemu.utils.table;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableReader {

    List<Row> rows;
    int index = 0;

    public Row nextColumn() throws IndexOutOfBoundsException {
        if (index < rows.size()) {
            return rows.get(index++);
        }

        return null;
    }

    public TableReader(String path) {
        rows = new ArrayList<>();

        try (FileReader fr = new FileReader(path);
             CSVReader reader = new CSVReader(fr, ',', '\"', 1)) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(new Row(row));
            }
        } catch (IOException ignored) {}
    }
}
