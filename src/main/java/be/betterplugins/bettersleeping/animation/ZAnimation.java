//package be.dezijwegel.bettersleeping.animation;
//
//import be.dezijwegel.bettersleeping.BetterSleepingOld;
//import be.dezijwegel.bettersleeping.animation.location.IVariableLocation;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Particle;
//import org.bukkit.util.Vector;
//
//import java.util.*;
//
//public class ZAnimation extends Animation implements PreComputeable<Double> {
//
//    private final List<Double> rotations;
//    private final Map<Double, List<Vector>> animationComputations;
//
//    private final Particle particle;
//    private final double size;
//    private final double spacing;
//
//
//    public ZAnimation(Particle particle, double size, double spacing)
//    {
//        this.animationComputations = new HashMap<>();
//
//        this.particle = particle;
//        this.size = size;
//        this.spacing = spacing;
//
//        this.rotations = new ArrayList<>();
//        for (double rotation = 0; rotation < 2 * Math.PI; rotation += Math.PI/4)
//        {
//            this.rotations.add(rotation);
//        }
//
//    }
//
//    @Override
//    public void preCompute(Double rotation)
//    {
//        // Do calculations async
//        Bukkit.getScheduler().runTaskAsynchronously(BetterSleepingOld.getInstance(), () -> {
//
//            // Load the raw animation by following the path
//
//            List<Vector> rawLocations = new ArrayList<>();
//            for (int i = -1; i < 2; i++)
//            {
//                double correctedSize = size / spacing;
//                double spaceSize = 3;
//                Vector currentPosition = new Vector(i * (correctedSize + spaceSize), 0, 0);
//
//                for (double t = 0; t < size; t += spacing) {
//                    Vector movement = new Vector(1, 0, 0);
//                    rawLocations.add(currentPosition.add(movement).clone());
//                }
//
//
//                for (double t = 0; t < size; t += spacing) {
//                    Vector movement = new Vector(-1, -1, 0);
//                    rawLocations.add(currentPosition.add(movement).clone());
//                }
//
//
//                for (double t = 0; t < size; t += spacing) {
//                    Vector movement = new Vector(1, 0, 0);
//                    rawLocations.add(currentPosition.add(movement).clone());
//                }
//            }
//
//            // Perform calculations
//
//            double halfSize = (3 * size) / 2;
//            Vector offset = new Vector(-halfSize, halfSize, 0).add( new Vector(0, 1 / spacing, 0) );
//            List<Vector> locations = new ArrayList<>();
//            for (Vector position : rawLocations)
//            {
//                position.add( offset );
//                position.rotateAroundY( rotation );
//                position.multiply( spacing );
//                locations.add( position );
//            }
//
//            this.animationComputations.put( rotation, locations);
//        });
//    }
//
//    @Override
//    public boolean isComputed(Double rotation)
//    {
//        return this.animationComputations.containsKey( rotation );
//    }
//
//    @Override
//    public void startAnimation(IVariableLocation variableLocation) {
//        super.startAnimation(variableLocation);
//
//        // Handle particle spawning async
//        Bukkit.getScheduler().runTaskAsynchronously(BetterSleepingOld.getInstance(), () -> {
//
//            // Pick a random rotation
//            double rotation = this.rotations.get( new Random().nextInt( this.rotations.size() ) );
//
//            if ( ! this.isComputed( rotation ))
//            {
//                this.preCompute( rotation );
//                return;
//            }
//
//            Location origin = variableLocation.getLocation();
//            if (origin.getWorld() == null) return;
//
//            // Draw particles
//            List<Vector> locations = this.animationComputations.get( rotation );
//            for (Vector location : locations) {
//
//                origin.getWorld().spawnParticle(particle, origin.clone().add( location ), 1);
//            }
//
//            // Mark the animation as finished
//            super.isPlaying = false;
//        });
//    }
//}
