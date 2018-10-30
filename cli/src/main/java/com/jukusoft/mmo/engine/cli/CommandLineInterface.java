package com.jukusoft.mmo.engine.cli;

import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.cli.impl.VersionCmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CommandLineInterface implements Runnable {

    //map with all commands
    protected static Map<String,CLICommand> commands = new HashMap<>();

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

        JTextArea ta = new JTextArea("CLI started!\nEnter 'help' to show a list with all available commands.\n\n", 10, 50);
        ta.setEditable(false);

        JTextField textField = new JTextField("");
        textField.addActionListener(e -> {
            String text = textField.getText();
            //System.err.println("on enter: " + text);
            ta.append("> " + text + "\n");

            //process command
            String[] array = text.split(" ");
            String[] args = new String[array.length - 1];

            String command = array[0];

            for (int i = 0; i < args.length; i++) {
                args[i] = array[i+1];
            }

            ta.append(process(command, args) + "\n");

            textField.setText("");
        });

        JScrollPane sp = new JScrollPane(ta, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //sp.add(textField);

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
                    sb.append(entry.getKey() + " - " + entry.getValue().getDescription() + "\n");
                }

                return sb.toString();
            }

            @Override
            public String getDescription() {
                return "shows all available commands";
            }
        });

        registerCommand("version", new VersionCmd());
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
