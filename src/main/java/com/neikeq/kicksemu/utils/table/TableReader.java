package com.neikeq.kicksemu.utils.table;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TableReader {

    List<Column> columns;
    int index = 0;

    public Column nextColumn() {
        if (index < columns.size()) {
            return columns.get(index++);
        }

        return null;
    }

    public TableReader(String path) {
        try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {
            columns = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                columns.add(new Column(line));
            }
        } catch (IOException ignored) {}
    }
}
