package com.jukusoft.mmo.engine.cli;

import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;

import javax.swing.*;
import java.awt.*;

public class CommandLineInterface implements Runnable {

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

        JPanel p = new JPanel(new BorderLayout());
        p.setSize(600, 400);

        JTextArea ta = new JTextArea("CLI started!\n\n> ", 5, 20);
        ta.setEditable(false);

        JTextField textField = new JTextField("test");
        window.add(textField);

        JScrollPane sp = new JScrollPane(ta, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.add(textField);

        p.add(sp, BorderLayout.NORTH);
        p.add(textField, BorderLayout.SOUTH);

        window.add(p);

        window.requestFocus();
        window.pack();
        window.setVisible(true);
    }

}
