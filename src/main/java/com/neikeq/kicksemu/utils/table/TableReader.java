package com.neikeq.kicksemu.utils.table;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableReader {

    private final List<Row> rows;
    private int index;
    private final String tablePath;

    public int getIndex() {
        return index;
    }

    public Optional<Row> nextRow() throws IndexOutOfBoundsException {
        if (index < rows.size()) {
            return Optional.of(rows.get(index++));
        }

        return Optional.empty();
    }

    public String getTablePath() {
        return tablePath;
    }

    public TableReader(String path) {
        rows = new ArrayList<>();
        tablePath = path;

        try (FileReader fr = new FileReader(path);
             CSVReader reader = new CSVReader(fr, ',', '\"', 1)) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                rows.add(new Row(row));
            }
        } catch (IOException e) {
            Output.println("Exception when opening table: " + path, Level.DEBUG);
        }
    }
}
