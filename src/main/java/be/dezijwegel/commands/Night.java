package be.dezijwegel.commands;

import be.dezijwegel.events.SleepTracker;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Night implements be.dezijwegel.interfaces.Command {

    private SleepTracker sleepTracker;

    public Night(SleepTracker sleepTracker)
    {
        this.sleepTracker = sleepTracker;
    }

    @Override
    public boolean execute(CommandSender cs, Command cmnd, String string, String[] strings) {
        return false;
    }

}
