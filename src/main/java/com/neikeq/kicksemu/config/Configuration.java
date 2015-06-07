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

/** Manages application settings. */
public class Configuration {

    private static Configuration instance;

    private final Properties config = new Properties();

    private String configPath;

    /**
     * Initializes the configuration properties with default values.
     * If a configuration file exist, tries to read it.
     */
    public void init(String path) {
        configPath = Constants.CONFIG_DIR + path;

        // Output Settings
        config.put("output.logging", "true");
        config.put("output.verbosity", "0");
        
        // MySQL Database Settings
        config.put("mysql.idle.min", "5");
        config.put("mysql.idle.max", "20");
        config.put("mysql.user", "root");
        config.put("mysql.pass", "");
        config.put("mysql.host", "localhost");
        config.put("mysql.port", "3306");
        config.put("mysql.database", "kicksdb");
        
        // Networking Settings
        config.put("net.backlog", "50");
        
        // Language Settings
        config.put("lang", "en");

        // Game Server Settings
        config.put("id", "99");
        config.put("game.tcp.port.factor", "1200");
        config.put("game.users.max", "500");
        config.put("game.type", "main");
        
        // Try to read the properties from the configuration file
        loadConfiguration();
    }

    private void loadConfiguration() {
        if (Files.exists(Paths.get(configPath))) {
            try (InputStream configStream = new FileInputStream(configPath)) {
                config.load(configStream);
            } catch (IOException e) {
                System.out.println("Cannot read configuration file." + e.getMessage());
            }
        } else {
            Output.println("Initialized configuration with default values.", Level.WARNING);
        }
    }

    public void save() throws IOException {
        try (OutputStream out = new FileOutputStream(new File(configPath))) {
            config.store(out, "Generated code");
            Output.println("Configuration file saved: " + configPath, Level.INFO);
        }
    }

    /** Return the property with the specified key. */
    public static String get(String key) {
        Configuration conf = getInstance();

        if (conf.config != null && conf.config.containsKey(key)) {
            return conf.config.getProperty(key);
        } else {
            return "";
        }
    }

    public static byte getByte(String key) {
        return Byte.parseByte(get(key));
    }

    public static boolean getBoolean(String key) {
        return get(key).equals("true");
    }

    public static short getShort(String key) {
        return Short.parseShort(get(key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static Level getLevel(String key) {
        return Level.fromString(get(key));
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }

        return instance;
    }
}
