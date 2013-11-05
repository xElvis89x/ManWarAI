package model;

public final class PlayerContext {
    private final Trooper trooper;
    private final World world;

    public PlayerContext(Trooper trooper, World world) {
        this.trooper = trooper;
        this.world = world;
    }

    public Trooper getTrooper() {
        return trooper;
    }

    public World getWorld() {
        return world;
    }
}
