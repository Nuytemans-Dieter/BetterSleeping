package be.dezijwegel.timedEvents.aprilFools;


import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.material.Colorable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ColorBedsRunnable extends BukkitRunnable {

    private List<Block> beds = new ArrayList<>();

    private static final Set<Material> BED_COLORS = EnumSet.of(
            Material.BLACK_BED, Material.BLUE_BED, Material.BROWN_BED,
            Material.CYAN_BED, Material.GRAY_BED, Material.GREEN_BED,
            Material.LIGHT_BLUE_BED, Material.LIGHT_GRAY_BED, Material.LIME_BED,
            Material.MAGENTA_BED, Material.ORANGE_BED, Material.PINK_BED,
            Material.PURPLE_BED, Material.RED_BED, Material.WHITE_BED,
            Material.YELLOW_BED
    );

    private static final Set<DyeColor> DYE_COLORS = EnumSet.of(
            DyeColor.BLACK, DyeColor.BLUE, DyeColor.BROWN, DyeColor.CYAN,
            DyeColor.GRAY, DyeColor.GREEN, DyeColor.LIGHT_BLUE, DyeColor.LIGHT_GRAY,
            DyeColor.LIME, DyeColor.MAGENTA, DyeColor.ORANGE, DyeColor.PINK, DyeColor.PURPLE,
            DyeColor.RED, DyeColor.WHITE, DyeColor.YELLOW
    );

    /**
     * Add a bed to the disco beds-list
     * This function will fail if the Block is not a bed
     * @param block the bed block
     */
    public void addBed(Block block)
    {
        if (BED_COLORS.contains(block.getType()))
            beds.add(block);
    }


    /**
     * Removes a bed from the disco beds-list
     * @param block
     */
    public void removeBed(Block block)
    {
        beds.remove(block);
    }


    @Override
    public void run() {

        // Choose a random bed color
        DyeColor newBedColor = DyeColor.RED;
        int random = new Random().nextInt(DYE_COLORS.size());
        int i = 0;
        for (DyeColor element : DYE_COLORS)
        {
            if (i == random)
            {
                newBedColor = element;
            }
            i++;
        }

        // Change all bed colors
        for (Block bed : beds)
        {
            if (BED_COLORS.contains(bed.getType()))     // Check if the Block is a bed
            {
                if (bed instanceof Colorable)
                {
                    Colorable colorable = (Colorable) bed;
                    colorable.setColor(newBedColor);
                }
            } else {
                beds.remove(bed);                       // Remove the block if it is not a bed
            }
        }
    }
}
