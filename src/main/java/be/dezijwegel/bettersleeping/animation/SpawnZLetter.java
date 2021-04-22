package be.dezijwegel.bettersleeping.animation;

import be.dezijwegel.bettersleeping.BetterSleeping;
import be.dezijwegel.bettersleeping.animation.location.IVariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

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

            // Load the raw animation by following the path

            Vector currentPosition = new Vector(0, 0, 0);

            List<Vector> rawLocations = new ArrayList<>();
            for (double t = 0; t < size; t += spacing)
            {
                Vector movement = new Vector(1, 0, 0);
                rawLocations.add( currentPosition.add( movement ).clone() );
            }

            for (double t = 0; t < size; t += spacing)
            {
                Vector movement = new Vector(-1, -1, 0);
                rawLocations.add( currentPosition.add( movement ).clone() );
            }

            for (double t = 0; t < size; t += spacing)
            {
                Vector movement = new Vector(1, 0, 0);
                rawLocations.add( currentPosition.add( movement ).clone() );
            }

            // Perform calculations

            Location origin = super.getOrigin();

            double halfSize = size / 2;
            Vector offset = new Vector(-halfSize, halfSize, 0);
            List<Location> particleLocations = new ArrayList<>();
            for (Vector position : rawLocations)
            {
                position.add( offset );
                position.rotateAroundY( rotation );
                position.multiply( spacing );
                particleLocations.add( origin.clone().add( position ) );
            }

            // Draw particles

            if (origin.getWorld() != null)
                for (Location location : particleLocations)
                {
                    origin.getWorld().spawnParticle( particle, location, 1 );
                }

            // Mark the animation as finished
            super.isPlaying = false;
        });
    }
}
