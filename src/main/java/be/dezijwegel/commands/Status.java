package be.dezijwegel.commands;

import be.dezijwegel.events.SleepTracker;
import be.dezijwegel.interfaces.Command;
import be.dezijwegel.management.Management;
import org.bukkit.command.CommandSender;

public class Status implements Command {

    private SleepTracker sleepTracker;
    private Management management;

    public Status(SleepTracker sleepTracker, Management management)
    {
        this.sleepTracker = sleepTracker;
        this.management = management;
    }


    @Override
    public boolean execute(CommandSender cs, org.bukkit.command.Command cmnd, String string, String[] strings) {
        return false;
    }
}
