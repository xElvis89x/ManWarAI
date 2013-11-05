package model;

/**
 * Направление.
 */
public enum Direction {
    /**
     * Текущая клетка.
     */
    CURRENT_POINT(0, 0),

    /**
     * Клетка к северу.
     */
    NORTH(0, -1),

    /**
     * Клетка к востоку.
     */
    EAST(1, 0),

    /**
     * Клетка к югу.
     */
    SOUTH(0, 1),

    /**
     * Клетка к западу.
     */
    WEST(-1, 0);

    private final int offsetX;
    private final int offsetY;

    Direction(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }
}
