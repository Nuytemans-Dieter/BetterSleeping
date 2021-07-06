package be.betterplugins.bettersleeping.model;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NoBossBar implements BossBar
{

    @Override
    public @NotNull String getTitle()
    {
        return "";
    }

    @Override
    public void setTitle(@Nullable String s) {}

    @Override
    public @NotNull BarColor getColor()
    {
        return BarColor.BLUE;
    }

    @Override
    public void setColor(@NotNull BarColor barColor) {}

    @Override
    public @NotNull BarStyle getStyle()
    {
        return BarStyle.SOLID;
    }

    @Override
    public void setStyle(@NotNull BarStyle barStyle){}

    @Override
    public void removeFlag(@NotNull BarFlag barFlag){}

    @Override
    public void addFlag(@NotNull BarFlag barFlag){}

    @Override
    public boolean hasFlag(@NotNull BarFlag barFlag)
    {
        return false;
    }

    @Override
    public void setProgress(double progress){}

    @Override
    public double getProgress()
    {
        return 0;
    }

    @Override
    public void addPlayer(@NotNull Player player){}

    @Override
    public void removePlayer(@NotNull Player player){}

    @Override
    public void removeAll(){}

    @Override
    public @NotNull List<Player> getPlayers()
    {
        return new ArrayList<>();
    }

    @Override
    public void setVisible(boolean b){}

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public void show(){}

    @Override
    public void hide(){}


}
