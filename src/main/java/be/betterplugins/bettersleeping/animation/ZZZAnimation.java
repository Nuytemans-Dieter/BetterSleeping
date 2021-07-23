package be.betterplugins.bettersleeping.animation;

import be.betterplugins.bettersleeping.animation.location.IVariableLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZZZAnimation extends Animation implements PreComputeable<Double>
{

    private final List<Double> rotations;
    private static final Map<Double, List<Vector>> animationComputations = new HashMap<>();

    private final Particle particle;
    private final double size;
    private final double spacing;

    private final long delay;

    private final JavaPlugin plugin;


    public ZZZAnimation(Particle particle, double size, double spacing, long delayMilliseconds, JavaPlugin plugin)
    {
        this.plugin = plugin;

        this.particle = particle;
        this.size = size;
        this.spacing = spacing;

        this.delay = delayMilliseconds;

        this.rotations = new ArrayList<>();
        for (double rotation = 0; rotation < 2 * Math.PI; rotation += Math.PI/16)
        {
            this.rotations.add(rotation);
            preCompute( rotation );
        }

    }


    @Override
    public void preCompute(Double rotation)
    {
        // Do calculations async
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {

            // Load the raw animation by following the path

            List<Vector> rawLocations = new ArrayList<>();
            for (int i = -1; i < 2; i++)
            {
                double correctedSize = size / spacing;
                double spaceSize = 3;
                Vector currentPosition = new Vector(i * (correctedSize + spaceSize), 0, 0);

                for (double t = 0; t < size; t += spacing)
                {
                    Vector movement = new Vector(1, 0, 0);
                    rawLocations.add(currentPosition.add(movement).clone());
                }


                for (double t = 0; t < size; t += spacing)
                {
                    Vector movement = new Vector(-1, -1, 0);
                    rawLocations.add(currentPosition.add(movement).clone());
                }


                for (double t = 0; t < size; t += spacing)
                {
                    Vector movement = new Vector(1, 0, 0);
                    rawLocations.add(currentPosition.add(movement).clone());
                }
            }

            // Perform calculations

            double halfSize = (3 * size) / 2;
            Vector offset = new Vector(-halfSize, halfSize, 0).add( new Vector(0, 1 / spacing, 0) );
            List<Vector> locations = new ArrayList<>();
            for (Vector position : rawLocations)
            {
                position.add( offset );
                position.rotateAroundY( rotation );
                position.multiply( spacing );
                locations.add( position );
            }

            animationComputations.put( rotation, locations);
        });
    }


    @Override
    public boolean isComputed(Double rotation)
    {
        return animationComputations.containsKey( rotation );
    }


    @Override
    public void startAnimation(IVariableLocation variableLocation)
    {
        super.startAnimation(variableLocation);

        // Handle particle spawning async
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
        {

            int iteration = 0;
            while (super.isPlaying)
            {

                // Pick a random rotation
                double rotation = this.rotations.get( iteration );

                Location origin = variableLocation.getLocation();
                if (origin.getWorld() == null) return;

                // Draw particles
                List<Vector> locations = animationComputations.get( rotation );
                if (locations != null)
                {
                    for (Vector location : locations)
                    {
                        origin.getWorld().spawnParticle(particle, origin.clone().add( location ), 1);
                    }
                }

                iteration++;
                iteration = iteration % this.rotations.size();
                try
                {
                    Thread.sleep( delay );
                }
                catch (InterruptedException e)
                {
                    super.isPlaying = false;
                    this.stopAnimation();
                }
            }
        });
    }
}
