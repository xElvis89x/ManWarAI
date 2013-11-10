package model;

import java.util.Arrays;

import static java.lang.StrictMath.hypot;
import static java.lang.StrictMath.min;

/**
 * Содержит описание игрового мира и позволяет получить списки различных юнитов, присутствующих на поле боя.
 */
public final class World {
    private final int moveIndex;
    private final int width;
    private final int height;
    private final Player[] players;
    private final Trooper[] troopers;
    private final Bonus[] bonuses;
    private final CellType[][] cells;
    private final boolean[][][][][] cellVisibilities;

    public World(
            int moveIndex, int width, int height, Player[] players, Trooper[] troopers, Bonus[] bonuses,
            CellType[][] cells, boolean[][][][][] cellVisibilities) {
        this.moveIndex = moveIndex;
        this.width = width;
        this.height = height;
        this.players = Arrays.copyOf(players, players.length);
        this.troopers = Arrays.copyOf(troopers, troopers.length);
        this.bonuses = Arrays.copyOf(bonuses, bonuses.length);

        this.cells = new CellType[width][];
        for (int x = 0; x < width; ++x) {
            this.cells[x] = Arrays.copyOf(cells[x], cells[x].length);
        }

        this.cellVisibilities = cellVisibilities;
    }

    /**
     * @return Возвращает номер текущего хода.
     */
    public int getMoveIndex() {
        return moveIndex;
    }

    /**
     * @return Возвращает ширину мира в клетках.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return Возвращает высоту мира в клетках.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return Возвращает список игроков (в случайном порядке).
     *         Объекты, задающие игроков, пересоздаются перед каждым вызовом {@code Strategy.move()}.
     */
    public Player[] getPlayers() {
        return Arrays.copyOf(players, players.length);
    }

    /**
     * @return Возвращает список видимых юнитами игрока бойцов (в случайном порядке),
     *         включая бойца стратегии, вызвавшей этот метод.
     *         Объекты, задающие бойцов, пересоздаются перед каждым вызовом {@code Strategy.move()}.
     */
    public Trooper[] getTroopers() {
        return Arrays.copyOf(troopers, troopers.length);
    }

    /**
     * @return Возвращает список видимых юнитами игрока бонусов (в случайном порядке).
     *         Объекты, задающие бонусы, пересоздаются перед каждым вызовом {@code Strategy.move()}.
     */
    public Bonus[] getBonuses() {
        return Arrays.copyOf(bonuses, bonuses.length);
    }

    /**
     * @return Возвращает двумерный массив типов клеток игрового поля,
     *         где первое измерение --- это координата X, а второе --- Y.
     */
    public CellType[][] getCells() {
        CellType[][] copiedCells = new CellType[cells.length][];
        for (int x = 0; x < cells.length; ++x) {
            copiedCells[x] = Arrays.copyOf(cells[x], cells[x].length);
        }
        return copiedCells;
    }

    /**
     * Метод проверяет, является ли юнит, находящийся в клетке с координатами
     * ({@code objectX}, {@code objectY}) в стойке {@code objectStance},
     * досягаемым для юнита, находящегося в клетке с координатами
     * ({@code viewerX}, {@code viewerY}) в стойке {@code viewerStance}.
     * Может использоваться как для проверки видимости, так и для проверки возможности стрельбы.
     *
     * При проверке видимости бонуса его высота считается равной высоте бойца в стойке {@code TrooperStance.PRONE}.
     *
     * @param maxRange     Дальность обзора/стрельбы наблюдающего юнита ({@code viewer}).
     * @param viewerX      X-координата наблюдающего юнита ({@code viewer}).
     * @param viewerY      Y-координата наблюдающего юнита ({@code viewer}).
     * @param viewerStance Стойка наблюдающего юнита ({@code viewer}).
     * @param objectX      X-координата наблюдаемого юнита ({@code object}).
     * @param objectY      Y-координата наблюдаемого юнита ({@code object}).
     * @param objectStance Стойка наблюдаемого юнита ({@code object}).
     * @return Возвращает {@code true}, если и только если наблюдаемый юнит ({@code object})
     *         является досягаемым для наблюдающего юнита ({@code viewer}).
     */
    public boolean isVisible(
            double maxRange,
            int viewerX, int viewerY, TrooperStance viewerStance,
            int objectX, int objectY, TrooperStance objectStance) {
        int minStanceIndex = min(viewerStance.ordinal(), objectStance.ordinal());
        return hypot(objectX - viewerX, objectY - viewerY) <= maxRange
                && cellVisibilities[viewerX][viewerY][objectX][objectY][minStanceIndex];
    }
}
