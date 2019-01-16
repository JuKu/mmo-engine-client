package com.jukusoft.mmo.engine.cli.impl;

import com.jukusoft.mmo.engine.cli.CLICommand;
import com.jukusoft.mmo.engine.shared.client.events.cli.ChangeConfigEvent;
import com.jukusoft.mmo.engine.shared.client.events.cli.ReloadConfigEvent;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;

public class ChangeConfigCmd implements CLICommand {

    @Override
    public String execute(String command, String[] args) {
        if (args.length != 3) {
            return "Invalide use of command! Correct Usage: changeConfig <section> <key> <value>";
        }

        //change config
        Config.set(args[0], args[1], args[2]);

        //fire event to notify subsystems to reload config
        ChangeConfigEvent event = Pools.get(ChangeConfigEvent.class);
        event.set(args[0], args[1], args[2]);
        Events.queueEvent(event);

        ReloadConfigEvent event1 = Pools.get(ReloadConfigEvent.class);
        Events.queueEvent(event1);

        return "Config changed successfully!";
    }

    @Override
    public String getDescription() {
        return "Change a config value at runtime, Usage: changeConfig <section> <key> <value>";
    }

}
