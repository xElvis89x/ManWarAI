package model;

/**
 * Класс, определяющий бонус --- неподвижный полезный объект. Содержит также все свойства юнита.
 */
public final class Bonus extends Unit {
    private final BonusType type;

    public Bonus(long id, int x, int y, BonusType type) {
        super(id, x, y);
        this.type = type;
    }

    /**
     * @return Возвращает тип бонуса.
     */
    public BonusType getType() {
        return type;
    }
}
