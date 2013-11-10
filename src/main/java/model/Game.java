package model;

/**
 * Предоставляет доступ к различным игровым константам.
 */
public final class Game {
    private final int moveCount;

    private final int lastPlayerEliminationScore;
    private final int playerEliminationScore;
    private final int trooperEliminationScore;
    private final double trooperDamageScoreFactor;

    private final int stanceChangeCost;
    private final int standingMoveCost;
    private final int kneelingMoveCost;
    private final int proneMoveCost;

    private final int commanderAuraBonusActionPoints;
    private final double commanderAuraRange;

    private final int commanderRequestEnemyDispositionCost;
    private final int commanderRequestEnemyDispositionMaxOffset;

    private final int fieldMedicHealCost;
    private final int fieldMedicHealBonusHitpoints;
    private final int fieldMedicHealSelfBonusHitpoints;

    private final double sniperStandingStealthBonus;
    private final double sniperKneelingStealthBonus;
    private final double sniperProneStealthBonus;

    private final double sniperStandingShootingRangeBonus;
    private final double sniperKneelingShootingRangeBonus;
    private final double sniperProneShootingRangeBonus;

    private final double scoutStealthBonusNegation;

    private final int grenadeThrowCost;
    private final double grenadeThrowRange;
    private final int grenadeDirectDamage;
    private final int grenadeCollateralDamage;

    private final int medikitUseCost;
    private final int medikitBonusHitpoints;
    private final int medikitHealSelfBonusHitpoints;

    private final int fieldRationEatCost;
    private final int fieldRationBonusActionPoints;

    public Game(
            int moveCount,
            int lastPlayerEliminationScore, int playerEliminationScore,
            int trooperEliminationScore, double trooperDamageScoreFactor,
            int stanceChangeCost, int standingMoveCost, int kneelingMoveCost, int proneMoveCost,
            int commanderAuraBonusActionPoints, double commanderAuraRange,
            int commanderRequestEnemyDispositionCost, int commanderRequestEnemyDispositionMaxOffset,
            int fieldMedicHealCost, int fieldMedicHealBonusHitpoints, int fieldMedicHealSelfBonusHitpoints,
            double sniperStandingStealthBonus, double sniperKneelingStealthBonus, double sniperProneStealthBonus,
            double sniperStandingShootingRangeBonus, double sniperKneelingShootingRangeBonus,
            double sniperProneShootingRangeBonus, double scoutStealthBonusNegation,
            int grenadeThrowCost, double grenadeThrowRange, int grenadeDirectDamage, int grenadeCollateralDamage,
            int medikitUseCost, int medikitBonusHitpoints, int medikitHealSelfBonusHitpoints,
            int fieldRationEatCost, int fieldRationBonusActionPoints) {
        this.moveCount = moveCount;
        this.lastPlayerEliminationScore = lastPlayerEliminationScore;
        this.playerEliminationScore = playerEliminationScore;
        this.trooperEliminationScore = trooperEliminationScore;
        this.trooperDamageScoreFactor = trooperDamageScoreFactor;
        this.stanceChangeCost = stanceChangeCost;
        this.standingMoveCost = standingMoveCost;
        this.kneelingMoveCost = kneelingMoveCost;
        this.proneMoveCost = proneMoveCost;
        this.commanderAuraBonusActionPoints = commanderAuraBonusActionPoints;
        this.commanderAuraRange = commanderAuraRange;
        this.commanderRequestEnemyDispositionCost = commanderRequestEnemyDispositionCost;
        this.commanderRequestEnemyDispositionMaxOffset = commanderRequestEnemyDispositionMaxOffset;
        this.fieldMedicHealCost = fieldMedicHealCost;
        this.fieldMedicHealBonusHitpoints = fieldMedicHealBonusHitpoints;
        this.fieldMedicHealSelfBonusHitpoints = fieldMedicHealSelfBonusHitpoints;
        this.sniperStandingStealthBonus = sniperStandingStealthBonus;
        this.sniperKneelingStealthBonus = sniperKneelingStealthBonus;
        this.sniperProneStealthBonus = sniperProneStealthBonus;
        this.sniperStandingShootingRangeBonus = sniperStandingShootingRangeBonus;
        this.sniperKneelingShootingRangeBonus = sniperKneelingShootingRangeBonus;
        this.sniperProneShootingRangeBonus = sniperProneShootingRangeBonus;
        this.scoutStealthBonusNegation = scoutStealthBonusNegation;
        this.grenadeThrowCost = grenadeThrowCost;
        this.grenadeThrowRange = grenadeThrowRange;
        this.grenadeDirectDamage = grenadeDirectDamage;
        this.grenadeCollateralDamage = grenadeCollateralDamage;
        this.medikitUseCost = medikitUseCost;
        this.medikitBonusHitpoints = medikitBonusHitpoints;
        this.medikitHealSelfBonusHitpoints = medikitHealSelfBonusHitpoints;
        this.fieldRationEatCost = fieldRationEatCost;
        this.fieldRationBonusActionPoints = fieldRationBonusActionPoints;
    }

    /**
     * @return Возвращает длительность игры в ходах --- циклах,
     *         когда каждый боец на игровом поле совершит ход один раз.
     *         Юнит считается совершившим ход, если закончились отведённые ему очки действия
     *         либо ход был передан принудительно ({@code ActionType.END_TURN}).
     *         В некоторых случаях игра может закончиться ранее.
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * @return Количество дополнительных баллов (помимо баллов, начисляемых за урон)
     *         за уничтожение последнего бойца противника.
     */
    public int getLastPlayerEliminationScore() {
        return lastPlayerEliminationScore;
    }

    /**
     * @return Количество дополнительных баллов (помимо баллов, начисляемых за урон)
     *         за уничтожение последнего бойца игрока.
     *         Неприменимо в случае начисления баллов,
     *         оговорённом в комментарии к методу {@code getLastPlayerEliminationScore()}.
     */
    public int getPlayerEliminationScore() {
        return playerEliminationScore;
    }

    /**
     * @return Количество дополнительных баллов (помимо баллов, начисляемых за урон)
     *         за уничтожение бойца противника.
     *         Неприменимо в случаях начисления баллов,
     *         оговорённых в комментариях к методам {@code getLastPlayerEliminationScore()}
     *         и {@code getPlayerEliminationScore()}.
     */
    public int getTrooperEliminationScore() {
        return trooperEliminationScore;
    }

    /**
     * @return Возвращает коэффициент начисления баллов за урон, нанесённый бойцу противника.
     *         В случае дробных величин значение всегда округляется вниз.
     */
    public double getTrooperDamageScoreFactor() {
        return trooperDamageScoreFactor;
    }

    /**
     * @return Возвращает количество очков действия, необходимое для смены стойки бойца на один уровень.
     */
    public int getStanceChangeCost() {
        return stanceChangeCost;
    }

    /**
     * @return Возвращает количество очков действия, необходимое для перемещения на одну клетку бойца,
     *         находящегося в положении стоя.
     */
    public int getStandingMoveCost() {
        return standingMoveCost;
    }

    /**
     * @return Возвращает количество очков действия, необходимое для перемещения на одну клетку бойца,
     *         находящегося в положении сидя.
     */
    public int getKneelingMoveCost() {
        return kneelingMoveCost;
    }

    /**
     * @return Возвращает количество очков действия, необходимое для перемещения на одну клетку бойца,
     *         находящегося в положении лёжа.
     */
    public int getProneMoveCost() {
        return proneMoveCost;
    }

    /**
     * @return Возвращает количество дополнительных очков действия, которые получает юнит игрока в начале своего хода,
     *         если рядом с ним находится командир этого же игрока.
     */
    public int getCommanderAuraBonusActionPoints() {
        return commanderAuraBonusActionPoints;
    }

    /**
     * @return Возвращает максимальную дальность от командира,
     *         при которой юнит игрока получает дополнительные очки действия в начале своего хода.
     */
    public double getCommanderAuraRange() {
        return commanderAuraRange;
    }

    /**
     * @return Возвращает количество очков действия, необходимое командиру, для запроса в штаб.
     */
    public int getCommanderRequestEnemyDispositionCost() {
        return commanderRequestEnemyDispositionCost;
    }

    /**
     * @return Возвращает модуль максимально возможного отклонения каждой из координат расположения противника,
     *         полученных в результате вызова самолёта-разведчика.
     */
    public int getCommanderRequestEnemyDispositionMaxOffset() {
        return commanderRequestEnemyDispositionMaxOffset;
    }

    /**
     * @return Возвращает количество очков действия, затрачиваемое полевым медиком на одно действие лечения.
     */
    public int getFieldMedicHealCost() {
        return fieldMedicHealCost;
    }

    /**
     * @return Возвращает количество очков здоровья, которое полевой медик может восполнить
     *         дружественному бойцу (исключая себя) за одно действие лечения.
     */
    public int getFieldMedicHealBonusHitpoints() {
        return fieldMedicHealBonusHitpoints;
    }

    /**
     * @return Возвращает количество очков здоровья, которое полевой медик может восполнить
     *         себе за одно действие лечения.
     */
    public int getFieldMedicHealSelfBonusHitpoints() {
        return fieldMedicHealSelfBonusHitpoints;
    }

    /**
     * @return Возвращает бонус к маскировке снайпера, находящегося в положении стоя.
     */
    public double getSniperStandingStealthBonus() {
        return sniperStandingStealthBonus;
    }

    /**
     * @return Возвращает бонус к маскировке снайпера, находящегося в положении сидя.
     */
    public double getSniperKneelingStealthBonus() {
        return sniperKneelingStealthBonus;
    }

    /**
     * @return Возвращает бонус к маскировке снайпера, находящегося в положении лёжа.
     */
    public double getSniperProneStealthBonus() {
        return sniperProneStealthBonus;
    }

    /**
     * @return Возвращает бонус к дальности стрельбы снайпера, находящегося в положении стоя.
     */
    public double getSniperStandingShootingRangeBonus() {
        return sniperStandingShootingRangeBonus;
    }

    /**
     * @return Возвращает бонус к дальности стрельбы снайпера, находящегося в положении сидя.
     */
    public double getSniperKneelingShootingRangeBonus() {
        return sniperKneelingShootingRangeBonus;
    }

    /**
     * @return Возвращает бонус к дальности стрельбы снайпера, находящегося в положении лёжа.
     */
    public double getSniperProneShootingRangeBonus() {
        return sniperProneShootingRangeBonus;
    }

    /**
     * @return Возвращает величину бонуса к маскировке юнита, которую разведчик может проигнорировать.
     */
    public double getScoutStealthBonusNegation() {
        return scoutStealthBonusNegation;
    }

    /**
     * @return Возвращает количество очков действия, необходимое для броска гранаты.
     */
    public int getGrenadeThrowCost() {
        return grenadeThrowCost;
    }

    /**
     * @return Возвращает дальность броска гранаты.
     */
    public double getGrenadeThrowRange() {
        return grenadeThrowRange;
    }

    /**
     * @return Возвращет урон от прямого попадания гранаты.
     */
    public int getGrenadeDirectDamage() {
        return grenadeDirectDamage;
    }

    /**
     * @return Возвращает урон от осколков гранаты.
     */
    public int getGrenadeCollateralDamage() {
        return grenadeCollateralDamage;
    }

    /**
     * @return Возвращает количество очков действия, необходимое для использования аптечки.
     */
    public int getMedikitUseCost() {
        return medikitUseCost;
    }

    /**
     * @return Возвращает количество очков здоровья, которое боец, применивший аптечку,
     *         может восполнить дружественному бойцу (исключая себя).
     */
    public int getMedikitBonusHitpoints() {
        return medikitBonusHitpoints;
    }

    /**
     * @return Возвращает количество очков здоровья, которое боец, применивший аптечку,
     *         может восполнить себе.
     */
    public int getMedikitHealSelfBonusHitpoints() {
        return medikitHealSelfBonusHitpoints;
    }

    /**
     * @return Возвращает количество очков действия, необходимое для употребления сухого пайка.
     */
    public int getFieldRationEatCost() {
        return fieldRationEatCost;
    }

    /**
     * @return Возвращает количество бонусных очков действия, полученных после употребления сухого пайка.
     */
    public int getFieldRationBonusActionPoints() {
        return fieldRationBonusActionPoints;
    }
}
