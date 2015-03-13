package com.neikeq.kicksemu;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Localization;
import com.neikeq.kicksemu.game.servers.ServerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Input;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.ServerType;
import com.neikeq.kicksemu.network.server.tcp.NettyTcpServer;
import com.neikeq.kicksemu.network.server.udp.NettyUdpServer;
import com.neikeq.kicksemu.storage.MySqlManager;

import java.net.BindException;
import java.sql.SQLException;

public class KicksEmu {

    private Output output;

    private ServerManager serverManager;

    private NettyTcpServer nettyTcpServer;
    private NettyUdpServer nettyUdpServer;

    private static KicksEmu instance;

    /** @param args command line arguments */
    public static void main(String[] args) {
        String configFile = args.length > 0 && !args[0].isEmpty() ?
                args[0] : "config.properties";

        configFile += !configFile.endsWith(".properties") ? ".properties" : "";

        KicksEmu.getInstance().start(configFile);
    }

    void start(String configFile) {
        long startTime = System.nanoTime();

        // Initialize Configurations
        Configuration.getInstance().init(configFile);

        // Initialize Output Stream
        output = new Output(Configuration.getBoolean("output.logging"),
                Configuration.getLevel("output.verbosity"));

        output.printHeader();

        // Initialize Language Translations
        Localization.getInstance().init();

        try {
            Output.println(Localization.get("init"));

            // Initialize MySQL Database
            MySqlManager.init();

            // Initialize ServerManager
            serverManager = new ServerManager(Configuration.get("net.type"));

            if (serverManager.getServerType() == null) {
                handleFatalError("Invalid server type.");
            } else if (!getServerManager().init()) {
                handleFatalError("Could not initialize Server Manager.");
            }

            // Initialize Tcp Server
            Output.println(Localization.get("net.init"));

            // Port for game servers must be relative to the port id
            int portTcp = serverManager.getServerType() == ServerType.MAIN ?
                    Configuration.getInt("net.tcp.bind.port") :
                    Configuration.getShort("game.tcp.port.factor") +
                            Configuration.getShort("game.id");

            nettyTcpServer = new NettyTcpServer(portTcp);
            nettyTcpServer.start();

            // If this is a game server, initialize Udp Server
            if (serverManager.getServerType() == ServerType.GAME) {
                // Port for game servers must be relative to the port id
                int portUdp = Configuration.getShort("game.udp.port.factor") +
                        Configuration.getShort("game.id");

                nettyUdpServer = new NettyUdpServer(portUdp);
                getNettyUdpServer().start();
            }

        } catch (ClassNotFoundException e) {
            // MySql Driver not found
            handleFatalError(Localization.get("mysql.error.driver"), e.getMessage());
        } catch (InterruptedException e) {
            // Bind was interrupted
            handleFatalError(Localization.get("net.bind.interrupt"), e.getMessage());
        } catch (SQLException e) {
            // Connection error
            handleFatalError(Localization.get("mysql.error.conn"), e.getMessage());
        } catch (IllegalArgumentException e) {
            // e.x.: Pool size is less than zero
            handleFatalError(e.getMessage());
        } catch (BindException e) {
            handleFatalError(Localization.get("net.bind.error"), e.getMessage());
        }

        long endTime = (System.nanoTime() - startTime) / 1000000;

        // Print success notification including elapsed time
        Output.println(Localization.get("init.success", Long.toString(endTime)), Level.INFO);

        // Start listening for user inputs
        Input input = new Input();
        input.listen();
    }

    /**
     * Prints an error which the application cannot handle. Then stops the emulator.
     *
     * @param messages error message/s
     */
    void handleFatalError(String... messages) {
        String message = messages[0];

        for (int i = 1; i < messages.length; i++) {
            message += System.lineSeparator() + messages[i];
        }

        Output.println(message, Level.CRITICAL);
        Output.println(Localization.get("init.error.exit"), Level.CRITICAL);
        stop();
    }

    /** Performs required disposing operations */
    private void dispose() {
        // Dispose server sockets
        Output.println(Localization.get("net.close"));
        cleanNetworking();

        // Update server online statics in database
        Output.println(Localization.get("mysql.clean"));
        cleanDatabase();
    }

    private void cleanDatabase() {
        ServerInfo.setOnline(false, ServerManager.getServerId());
    }

    private void cleanNetworking() {
        if (ServerManager.getPlayers() != null) {
            ServerManager.getPlayers().values().stream().forEach(Session::close);
        }

        if (nettyTcpServer != null) {
            nettyTcpServer.close();
        }

        if (getNettyUdpServer() != null) {
            getNettyUdpServer().close();
        }
    }

    public void stop() {
        Output.println(Localization.get("stop"));

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

    public NettyUdpServer getNettyUdpServer() {
        return nettyUdpServer;
    }
}
