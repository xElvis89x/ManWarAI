package model;

import static java.lang.StrictMath.hypot;

/**
 * Базовый класс для определения объектов (<<юнитов>>) на игровом поле.
 */
public abstract class Unit {
    private final long id;
    private final int x;
    private final int y;

    protected Unit(long id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    /**
     * @return Возвращает уникальный идентификатор объекта.
     */
    public final long getId() {
        return id;
    }

    /**
     * @return Возвращает X-координату центра объекта. Ось абсцисс направлена слева направо.
     */
    public final int getX() {
        return x;
    }

    /**
     * @return Возвращает Y-координату центра объекта. Ось ординат направлена свеху вниз.
     */
    public final int getY() {
        return y;
    }

    /**
     * @param x X-координата клетки.
     * @param y Y-координата клетки.
     * @return Возвращает расстояние между центрами указанной клетки и текущей клетки объекта.
     */
    public double getDistanceTo(int x, int y) {
        return hypot(x - this.x, y - this.y);
    }

    /**
     * @param unit Объект, до центра которого необходимо определить расстояние.
     * @return Возвращает расстояние между центрами клеток текущего и указанного объекта.
     */
    public double getDistanceTo(Unit unit) {
        return getDistanceTo(unit.x, unit.y);
    }
}
