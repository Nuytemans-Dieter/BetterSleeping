package be.betterplugins.bettersleeping.animation;

public interface PreComputeable<T> {

    /**
     * Start precomputing with a given argument
     * @param t the argument for which we want to start a pre-computation
     */
    void preCompute(T t);

    /**
     * Check whether or not the pre-computation has been completed yet
     * @return true if the computation is ready
     */
    boolean isComputed(T t);

}
