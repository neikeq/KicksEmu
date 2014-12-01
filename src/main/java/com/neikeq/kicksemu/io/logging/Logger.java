package com.neikeq.kicksemu.io.logging;

import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.utils.DateUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static Logger instance;

    private String logPath;

    private boolean logging;

    /**
     * Writes text to the log file
     * @param text the {@code String} to write
     */
    public synchronized void write(String text) {
        if (logging) {
            writeFile(text);
        }
    }
    
    /**
     * Writes to the log file
     * 
     * @param text the {@code String} to write
     */
    private void writeFile(String text) {
        try (FileWriter writer = new FileWriter(logPath, true)) {
            writer.write(text);
        } catch (IOException e) {
            setLogging(false);
            Output.println(e.getMessage(), Level.WARNING);
        }
    }

    /**
     * Checks if log directory exist. If it does not exist, try to create it.<br>
     * If the directory cannot be found and we fail to create the directory,
     * logging is disabled.
     */
    private void checkDirectory() {
        logPath = Constants.LOGS_DIR + DateUtils.getFileDateTimeString() + ".log";

        File logDir = new File(Constants.LOGS_DIR);

        if (!logDir.exists() && !logDir.mkdir()) {
            setLogging(false);

            Output.println("Could not find/create log file directory.", Level.WARNING);
        }
    }

    /** Returns a header for the log file. */
    private String getHeader() {
        return "## KicksEmulator" + System.lineSeparator() +
                "## Server output log file" + System.lineSeparator() +
                "## " + DateUtils.getFileDateTimeString() + System.lineSeparator() +
                System.lineSeparator();
    }



    public boolean getLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        // If logging was disabled and we are enabling it
        if (!this.logging && logging) {
            checkDirectory();
            write(getHeader());
        }

        this.logging = logging;
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        
        return instance;
    }

    private Logger() {
        logPath = Constants.LOGS_DIR + DateUtils.getFileDateTimeString() + ".log";
    }
}
