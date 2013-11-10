package model;

/**
 * Содержит данные о текущем состоянии игрока.
 */
public final class Player {
    private final long id;
    private final String name;
    private final int score;
    private final boolean strategyCrashed;
    private final int approximateX;
    private final int approximateY;

    public Player(long id, String name, int score, boolean strategyCrashed, int approximateX, int approximateY) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.strategyCrashed = strategyCrashed;
        this.approximateX = approximateX;
        this.approximateY = approximateY;
    }

    /**
     * @return Возвращает уникальный идентификатор игрока.
     */
    public long getId() {
        return id;
    }

    /**
     * @return Возвращает имя игрока.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Возвращает текущее количество баллов, набранных игроком.
     */
    public int getScore() {
        return score;
    }

    /**
     * @return Возвращает специальный флаг --- показатель того, что стратегия игрока <<упала>>.
     *         Более подробную информацию можно найти в документации к игре.
     */
    public boolean isStrategyCrashed() {
        return strategyCrashed;
    }

    /**
     * @return Возвращает примерное значение координаты X расположения противника согласно данным, полученным
     *         в результате вылета самолёта-разведчика, или {@code -1} в следующих случаях: вылет не производился;
     *         боец, совершающий ход, не является командиром; у данного игрока не осталось в живых ни одного бойца.
     */
    public int getApproximateX() {
        return approximateX;
    }

    /**
     * @return Возвращает примерное значение координаты Y расположения противника согласно данным, полученным
     *         в результате вылета самолёта-разведчика, или {@code -1} в следующих случаях: вылет не производился;
     *         боец, совершающий ход, не является командиром; у данного игрока не осталось в живых ни одного бойца.
     */
    public int getApproximateY() {
        return approximateY;
    }
}
