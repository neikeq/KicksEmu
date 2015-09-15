package com.neikeq.kicksemu;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.config.Localization;
import com.neikeq.kicksemu.game.chat.ChatCommands;
import com.neikeq.kicksemu.game.events.EventsManager;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.servers.ServerInfo;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Input;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.tcp.NettyTcpServer;
import com.neikeq.kicksemu.network.server.udp.NettyUdpServer;
import com.neikeq.kicksemu.storage.MySqlManager;
import org.quartz.SchedulerException;

import java.net.BindException;
import java.sql.SQLException;

public class KicksEmu {

    private static Output output;

    private static ServerManager serverManager;

    private static NettyTcpServer nettyTcpServer;
    private static NettyUdpServer nettyUdpServer;

    private static boolean initialized = false;

    /** @param args command line arguments */
    public static void main(String[] args) {
        String configFile = args.length > 0 && !args[0].isEmpty() ?
                args[0] : "config.properties";

        configFile += !configFile.endsWith(".properties") ? ".properties" : "";

        KicksEmu.start(configFile);
    }

    private static void start(String configFile) {
        long startTime = System.nanoTime();

        // --- Initialize Configurations
        Configuration.getInstance().init(configFile);

        // --- Initialize Output Stream
        output = new Output(Configuration.getBoolean("output.logging"),
                Configuration.getLevel("output.verbosity"));

        output.printHeader();

        // --- Initialize Language Translations
        Localization.getInstance().init();

        try {
            Output.println(Localization.get("init"));

            // --- Initialize MySQL Database
            Output.println(Localization.get("mysql.init"));
            MySqlManager.initialize();

            // --- Initialize ServerManager
            Output.println(Localization.get("server.init"));

            serverManager = new ServerManager();
            ServerManager.getMessageHandler().defineEvents();
            ServerManager.getMessageHandler().defineCertifyEvents();
            ServerManager.cleanPossibleConnectedUsers();

            // --- Initialize Game Components
            Output.println(Localization.get("game.init"));

            TableManager.initialize();
            EventsManager.initialize();

            // --- Initialize Tcp Server
            Output.println(Localization.get("net.init"));

            // Port for game servers must be relative to the port id
            int tcpPort = Configuration.getShort("game.tcp.port.factor") +
                    Configuration.getShort("game.id");

            nettyTcpServer = new NettyTcpServer(tcpPort);
            nettyTcpServer.start();

            // If this is a game server
            if (ServerManager.getServerType() != ServerType.MAIN) {
                // Port for game servers must be relative to the port id
                int udpPort = Configuration.getShort("game.udp.port.factor") +
                        Configuration.getShort("game.id");

                // --- Initialize Udp Server
                nettyUdpServer = new NettyUdpServer(udpPort);
                nettyUdpServer.start();

                // --- Initialize chat commands parser
                ChatCommands.initialize();
            }
        } catch (ClassNotFoundException e) {
            // MySql Driver not found
            handleFatalError(Localization.get("mysql.error.driver"), e.getMessage());
        } catch (InterruptedException e) {
            // Bind was interrupted
            handleFatalError(Localization.get("net.bind.interrupt"), e.getMessage());
        } catch (SQLException | IllegalArgumentException | AssertionError e) {
            // Connection error
            handleFatalError(e.getMessage());
        } catch (BindException e) {
            handleFatalError(Localization.get("net.bind.error"), e.getMessage());
        } catch (SchedulerException e) {
            handleFatalError(Localization.get("game.event.error"), e.getMessage());
        }

        long endTime = (System.nanoTime() - startTime) / 1000000;

        initialized = true;

        // Print success notification including elapsed time
        Output.println(Localization.get("init.success", Long.toString(endTime)), Level.INFO);

        // Print some information about the initialized server
        Output.println("Information | Type: " + ServerManager.getServerType().toString() +
                " - ID: " + ServerManager.getServerId());

        // Start listening for user inputs
        Input input = new Input();
        input.listen();
    }

    /**
     * Prints an error which the application cannot handle. Then stops the emulator.
     *
     * @param messages error message/s
     */
    private static void handleFatalError(String... messages) {
        String message = messages[0];

        for (int i = 1; i < messages.length; i++) {
            message += System.lineSeparator() + messages[i];
        }

        Output.println(Localization.get("init.error"), Level.CRITICAL);
        Output.println(message, Level.CRITICAL);
        stop();
    }

    /** Performs required disposing operations */
    private static void dispose() {
        // Dispose server sockets
        Output.println(Localization.get("net.close"));
        cleanNetworking();

        // Update server online statics in database
        Output.println(Localization.get("mysql.clean"));
        cleanDatabase();
    }

    private static void cleanNetworking() {
        if (ServerManager.getPlayers() != null) {
            ServerManager.getPlayers().values().forEach(Session::close);
        }

        if (nettyTcpServer != null) {
            nettyTcpServer.close();
        }

        if (getNettyUdpServer() != null) {
            getNettyUdpServer().close();
        }
    }

    private static void cleanDatabase() {
        short serverId = ServerManager.getServerId();

        if (serverId > 0 && initialized) {
            ServerInfo.toggleOffline(serverId);
            ServerInfo.setConnectedUsers((short) 0, serverId);
        }
    }

    public static void stop() {
        dispose();
        System.exit(0);
    }

    public static Output getOutput() {
        return output;
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static NettyUdpServer getNettyUdpServer() {
        return nettyUdpServer;
    }
}
