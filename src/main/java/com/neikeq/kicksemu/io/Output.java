package com.neikeq.kicksemu.io;

import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.io.logging.Logger;
import com.neikeq.kicksemu.utils.DateUtils;

/** Basic output and logging manager. */
public class Output {

    private static Level level;

    /**
     * Prints a message and logs it if logging is enabled.
     *
     * @param text the message to print
     */
    private static  void write(String text) {
        Logger.getInstance().write(text);
        System.out.print(text);
    }

    public static void println(String line) {
        // Output format: [HH:mm:ss] Message
        write(DateUtils.getTimeString() + " " + line + System.lineSeparator());
    }

    public static void println(String line, Level level) {
        if (Output.level.toInt() <= level.toInt()) {
            // Output format: [HH:mm:ss] Message
            write(DateUtils.getTimeString() + " " + line + System.lineSeparator());
        }
    }
    
    public void printHeader() {
        System.out.println("# KicksEmulator Server" + System.lineSeparator() +
                "# github.com/neikeq/com.neikeq.kicksemu" + System.lineSeparator());

        // License
        System.out.println("KicksEmu is available under the GNU GPL v3 license.");
        System.out.println(
                "This program comes with ABSOLUTELY NO WARRANTY. " +
                "This is free software, and you are welcome to redistribute it " +
                "under certain conditions; please review the included LICENSE file." +
                        System.lineSeparator());
        
        if (level.toInt() < Level.INFO.toInt()) {
            System.out.println(
                    "WARNING: Debug logging level is active. " +
                    "This may cause a decrease in performance." +
                            System.lineSeparator());
        }
    }

    public void setLevel(Level level) {
        Output.level = level;
    }

    public Output(boolean logging, Level level) {
        setLevel(level);
        Logger.getInstance().setLogging(logging);
    }
}
