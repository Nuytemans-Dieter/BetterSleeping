package be.dezijwegel.bettersleeping.animation;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.animation.location.IVariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class SpawnZLetter extends Animation {

    private final Particle particle;
    private final double size;
    private final double spacing;
    private final double rotation;

//    [cos  0   sin 0]
//    [0    2   0   0]
//    [-sin 0   cos 0]
//    [0    0   0   1]

    public SpawnZLetter(IVariableLocation location, Particle particle, double size, double spacing)
    {
        this(location, particle, size, spacing, 0);
    }

    public SpawnZLetter(IVariableLocation location, Particle particle, double size, double spacing, double rotation)
    {
        super( location );
        this.particle = particle;
        this.size = size;
        this.spacing = spacing;
        this.rotation = rotation;
    }

    /**
     * Change the location by an offset and spawn a particle in this location
     * @param location the starting location
     * @param offset offset, should only contain -1, 0 and 1 as the spacing is applied internally
     */
    private void handleLocation(Location location, Vector offset)
    {
        location.add( offset.multiply( spacing ) );

        if (location.getWorld() == null) return;
        location.getWorld().spawnParticle( particle, location, 1 );
    }

    private Vector rotate(Vector vector, double theta)
    {
        Vector x = new Vector( Math.cos( theta ), 0, - Math.sin( theta ) );
        Vector y = new Vector( 0, 1, 0);
        Vector z = new Vector( Math.sin( theta ), 0, Math.cos( theta ));

        return new Vector(
            x.getX() * vector.getX() + x.getY() * vector.getY()  + x.getZ() * vector.getZ(),
            y.getX() * vector.getX() + y.getY() * vector.getY()  + y.getZ() * vector.getZ(),
            z.getX() * vector.getX() + z.getY() * vector.getY()  + z.getZ() * vector.getZ()
        );
    }

    @Override
    public void startAnimation() {
        super.startAnimation();

        // Do calculations async
        Bukkit.getScheduler().runTaskAsynchronously(BetterSleeping.getInstance(), () -> {

            double halfSize = size / 2;
            Location lastLocation = super.getOrigin().add( - halfSize, halfSize, 0 );

            for (double t = 0; t < size; t += spacing)
                handleLocation(lastLocation, rotate(new Vector(1, 0, 0), rotation));

            for (double t = 0; t < size; t += spacing)
                handleLocation(lastLocation, rotate(new Vector(-1, -1, 0), rotation));

            for (double t = 0; t < size; t += spacing)
                handleLocation(lastLocation, rotate(new Vector(1, 0, 0), rotation));

            // Mark the animation as finished
            super.isPlaying = false;
        });
    }
}
