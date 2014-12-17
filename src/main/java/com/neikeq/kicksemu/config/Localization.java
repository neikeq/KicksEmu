package com.neikeq.kicksemu.config;

import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/** Manages application translations. */
public class Localization {

    private static Localization localization;

    private String langPath;
    private Properties lang;

    public void init() {
        langPath = Constants.LANG_DIR + "lang_" + Configuration.get("lang") + ".properties";

        if (!Files.exists(Paths.get(langPath))) {
            Output.println("Language file missing.", Level.CRITICAL);
        } else {
            try {
                lang = load();
            } catch (IOException e) {
                Output.println("Cannot read language file: " + e.getMessage(), Level.CRITICAL);
            }
        }
    }

    private Properties load() throws IOException {
        Properties properties = new Properties();

        try (InputStream configStream = new FileInputStream(langPath)) {
            properties.load(configStream);
        }

        return properties;
    }

    public void save() throws IOException{
        try (OutputStream out = new FileOutputStream(new File(langPath))) {
            lang.store(out, "Generated code");
            Output.println("Language file saved.", Level.INFO);
        }
    }

    /** Returns the {@code String} with the specified key. */
    public static String get(String key) {
        Properties lang = getInstance().lang;

        if (lang != null && lang.containsKey(key)) {
            return lang.getProperty(key);
        } else {
            return key;
        }
    }
    
    /** Returns the {@code String} with the specified key, formatted with arguments. */
    public static String get(String key, String arg) {
        Properties lang = getInstance().lang;

        if (lang != null && lang.containsKey(key)) {
            return String.format(lang.getProperty(key), arg);
        } else {
            return key + " w/ arg: " + arg;
        }
    }

    public synchronized static Localization getInstance() {
        if (localization == null) {
            localization = new Localization();
        }

        return localization;
    }
}
