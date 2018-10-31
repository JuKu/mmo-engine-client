package com.jukusoft.mmo.engine.cli;

public interface CLICommand {

    public String execute (String command, String[] args);

    public String getDescription ();

    public default String getParams () {
        return "";
    }

}
