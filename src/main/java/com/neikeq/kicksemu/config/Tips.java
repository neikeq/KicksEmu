package com.neikeq.kicksemu.config;

import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tips {

    private static Tips instance;

    private final List<String> tipsList = new ArrayList<>();
    private int index = 0;

    private void load() {
        final String path = TableManager.getTablePath(Constants.PROPERTY_TABLE_TIPS) +
                Configuration.get("lang");

        if (Files.exists(Paths.get(path))) {
            try (Scanner s = new Scanner(new File(path))) {
                while (s.hasNextLine()) {
                    String line = s.nextLine();

                    if (!line.isEmpty()) {
                        tipsList.add(line);
                    }
                }
            } catch (FileNotFoundException e) {
                Output.println("Cannot read tips file.", Level.INFO);
            }
        }
    }

    public static String getNext() {
        Tips tips = getInstance();

        if (tips.tipsList.size() > 0) {
            return tips.tipsList.get(tips.getIndex());
        }

        return "";
    }

    private Tips() {
        load();
    }

    private static Tips getInstance() {
        if (instance == null) {
            instance = new Tips();
        }

        return instance;
    }

    private int getIndex() {
        int curIndex = index;
        index = index < tipsList.size() - 1 ? ++index : 0;
        return curIndex;
    }
}
