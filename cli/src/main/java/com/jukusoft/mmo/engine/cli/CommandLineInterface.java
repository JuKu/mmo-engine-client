package com.jukusoft.mmo.engine.cli;

import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.cli.impl.*;
import com.jukusoft.mmo.engine.shared.client.events.input.PlayerMoveEvent;
import com.jukusoft.mmo.engine.shared.client.events.input.TakeScreenshotEvent;
import com.jukusoft.mmo.engine.shared.events.EventData;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CommandLineInterface implements Runnable {

    //map with all commands
    protected static Map<String,CLICommand> commands = new HashMap<>();

    //map with events, which can be fired
    protected Map<String,Class<?>> eventTypes = new HashMap<>();

    @Override
    public void run() {
        if (!Config.getBool("CLI", "enabled")) {
            Log.i("CLI", "command line interface is disabled.");
            return;
        }

        JFrame window = new JFrame(Config.get("CLI", "title"));
        window.setSize(600, 400);
        window.setBackground(Color.BLACK);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout());
        p.setSize(600, 400);

        JTextArea ta = new JTextArea("CLI started!\nType 'help' to show a list with all available commands.\n\n", 10, 50);
        ta.setEditable(false);

        JScrollPane sp = new JScrollPane(ta, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JTextField textField = new JTextField("");
        textField.addActionListener(e -> {
            String text = textField.getText();
            ta.append("> " + text + "\n");

            //process command
            String[] array = text.split(" ");
            String[] args = new String[array.length - 1];

            String command = array[0];

            for (int i = 0; i < args.length; i++) {
                args[i] = array[i+1];
            }

            ta.append(process(command, args) + "\n");

            //scroll down
            ta.setCaretPosition(ta.getDocument().getLength());

            textField.setText("");
        });

        p.add(sp, BorderLayout.NORTH);
        p.add(textField, BorderLayout.SOUTH);

        window.add(p);

        window.requestFocus();
        window.pack();
        window.setVisible(true);

        registerCommand("help", new CLICommand() {
            @Override
            public String execute(String command, String[] args) {
                StringBuilder sb = new StringBuilder();

                for (Map.Entry<String,CLICommand> entry : commands.entrySet()) {
                    sb.append(" - " + entry.getKey() + (entry.getValue().getParams().isEmpty() ? "" : " " + entry.getValue().getParams()) + " - " + entry.getValue().getDescription() + "\n");
                }

                return sb.toString();
            }

            @Override
            public String getDescription() {
                return "shows all available commands";
            }
        });

        //register available commands
        registerCommand("version", new VersionCmd());
        registerCommand("listEvents", new ListEventsCmd(this.eventTypes));
        registerCommand("fireEvent", new FireEventCmd(this.eventTypes));
        registerCommand("takeScreenshot", new TakeScreenshotCmd());
        registerCommand("listLoadedAssets", new ListLoadedAssetsCmd());
        registerCommand("changeConfig", new ChangeConfigCmd());
        registerCommand("reloadConfig", new ReloadConfigCmd());
        registerCommand("loadConfigFile", new LoadConfigFileCmd());

        //register events, which can be fired
        registerEvent(PlayerMoveEvent.class);
        registerEvent(TakeScreenshotEvent.class);
    }

    protected void registerEvent (Class<? extends EventData> cls) {
        eventTypes.put(cls.getSimpleName(), cls);
    }

    protected String process (String command, String[] args) {
        //find command
        CLICommand cliCommand = commands.get(command);

        if (cliCommand == null) {
            return "Unknown command!";
        }

        return cliCommand.execute(command, args);
    }

    public static void registerCommand (String cmd, CLICommand command) {
        commands.put(cmd, command);
    }

    public static void unregisterCommand (String cmd) {
        commands.remove(cmd);
    }

}
