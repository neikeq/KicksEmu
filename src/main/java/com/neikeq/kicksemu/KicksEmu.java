package com.neikeq.kicksemu;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Location;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Input;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.tcp.NettyTcpServer;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.sql.SQLException;

public class KicksEmu {

    private Output output;

    private ServerManager serverManager;
    private NettyTcpServer nettyTcpServer;

    private static KicksEmu instance;

    /** @param args command line arguments */
    public static void main(String[] args) {
        String configFile = args.length > 0 ? args[0] : "config.properties";
        KicksEmu.getInstance().start(configFile);
    }

    public void start(String configFile) {
        long startTime = System.nanoTime();

        // Initialize Configurations
        Configuration.getInstance().init(configFile);

        // Initialize Output Stream
        output = new Output(
                Configuration.getBoolean("output.logging"),
                Configuration.getLevel("output.verbosity")
        );

        output.printHeader();

        // Initialize Language Translations
        Location.getInstance().init();

        try {
            Output.println(Location.get("init"));

            // Initialize MySQL Database
            MySqlManager.init();

            // Initialize Tcp and Udp Server
            Output.println(Location.get("net.init"));
            nettyTcpServer = new NettyTcpServer(Configuration.getInt("net.bind.port"));

            // Initialize ServerManager
            serverManager = new ServerManager(Configuration.get("net.type"));

            if (serverManager.getServerType() == null) {
                handleFatalError("Invalid server type.");
            } else if (!getServerManager().init()) {
                handleFatalError("Could not initialize Server Manager.");
            }

            // Start listening
            nettyTcpServer.start();

        } catch (ClassNotFoundException e) {
            // MySql Driver not found
            handleFatalError(Location.get("mysql.error.driver"), e.getMessage());
        } catch (InterruptedException e) {
            // Bind was interrupted
            handleFatalError(Location.get("net.bind.interrupt"), e.getMessage());
        } catch (SQLException e) {
            // Connection error
            handleFatalError(Location.get("mysql.error.conn"), e.getMessage());
        } catch (IllegalArgumentException e) {
            // e.x.: Pool size is less than zero
            handleFatalError(e.getMessage());
        }

        long endTime = (System.nanoTime() - startTime) / 1000000;

        // Print success notification including elapsed time
        Output.println(Location.get("init.success", Long.toString(endTime)), Level.INFO);

        // Start listening for user inputs
        Input input = new Input();
        input.listen();
    }

    /**
     * Prints an error which the application cannot handle. Then stops the emulator.
     * 
     * @param messages error message/s
     */
    public void handleFatalError(String ... messages) {
        String message = messages[0];

        for (int i = 1; i < messages.length; i++) {
            message += System.lineSeparator() + messages[i];
        }

        Output.println(message, Level.CRITICAL);
        Output.println(Location.get("init.error.exit"), Level.CRITICAL);
        stop();
    }
    
    /** Performs required disposing operations */
    private void dispose() {
        // Dispose server sockets
        Output.println(Location.get("net.close"));
        cleanNetworking();

        // Update server online statics in database
        Output.println(Location.get("mysql.clean"));
        cleanDatabase();
    }

    private void cleanDatabase() {
        if (ServerManager.getPlayers() != null) {
            ServerManager.getPlayers().values().stream().forEach(Session::close);
        }
    }

    private void cleanNetworking() {
        if (nettyTcpServer != null) {
            nettyTcpServer.close();
        }
    }

    public void stop() {
        Output.println(Location.get("stop"));

        dispose();

        System.exit(0);
    }

    public Output getOutput() {
        return output;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public static KicksEmu getInstance() {
        if (instance == null) {
            instance = new KicksEmu();
        }

        return instance;
    }
}
