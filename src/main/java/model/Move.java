package model;

/**
 * Стратегия игрока может управлять бойцом посредством установки свойств объекта данного класса.
 */
public final class Move {
    private ActionType action = ActionType.END_TURN;
    private Direction direction;
    private int x = -1;
    private int y = -1;

    /**
     * @return Возвращает текущее действие бойца.
     */
    public ActionType getAction() {
        return action;
    }

    /**
     * Устанавливает действие бойца.
     *
     * @param action Действие бойца.
     */
    public void setAction(ActionType action) {
        this.action = action;
    }

    /**
     * @return Возвращает текущее направление действия бойца.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Устанавливает направление действия бойца, если это необходимо.
     * При обработке симулятором игры является более приоритетным, чем поля {@code x} и {@code y}.
     *
     * @param direction Направление действия.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * @return Возвращает текущую абсциссу цели действия.
     */
    public int getX() {
        return x;
    }

    /**
     * Задаёт абсциссу цели действия. Значение будет проигнорировано, если установлено поле {@code direction}.
     *
     * @param x Абсцисса цели.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return Возвращает текущую ординату цели действия.
     */
    public int getY() {
        return y;
    }

    /**
     * Задаёт ординату цели действия. Значение будет проигнорировано, если установлено поле {@code direction}.
     *
     * @param y Ордината цели.
     */
    public void setY(int y) {
        this.y = y;
    }
}
