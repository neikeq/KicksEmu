package com.neikeq.kicksemu.config;

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

    private List<String> tipsList;
    private String tipsPath;
    private int index;

    private void init() {
        tipsList = new ArrayList<>();
        index = 0;

        tipsPath = Constants.TABLE_DIR + "tips_" + Configuration.get("lang");

        if (Files.exists(Paths.get(tipsPath))) {
            load();
        }
    }

    private void load() {
        try (Scanner s = new Scanner(new File(tipsPath))) {
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

    public static String getNext() {
        Tips tips = getInstance();

        if (tips.tipsList.size() > 0) {
            return tips.tipsList.get(tips.getIndex());
        }

        return "";
    }

    private Tips() {
        init();
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
