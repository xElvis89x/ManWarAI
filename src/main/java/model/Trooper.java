package model;

/**
 * Класс, определяющий бойца. Содержит также все свойства юнита.
 */
public final class Trooper extends Unit {
    private final long playerId;
    private final int teammateIndex;
    private final boolean teammate;

    private final TrooperType type;
    private final TrooperStance stance;

    private final int hitpoints;
    private final int maximalHitpoints;

    private final int actionPoints;
    private final int initialActionPoints;

    private final double visionRange;
    private final double shootingRange;

    private final int shootCost;
    private final int standingDamage;
    private final int kneelingDamage;
    private final int proneDamage;
    private final int damage;

    private final boolean holdingGrenade;
    private final boolean holdingMedikit;
    private final boolean holdingFieldRation;

    public Trooper(
            long id, int x, int y, long playerId,
            int teammateIndex, boolean teammate, TrooperType type, TrooperStance stance,
            int hitpoints, int maximalHitpoints, int actionPoints, int initialActionPoints,
            double visionRange, double shootingRange, int shootCost,
            int standingDamage, int kneelingDamage, int proneDamage, int damage,
            boolean holdingGrenade, boolean holdingMedikit, boolean holdingFieldRation) {
        super(id, x, y);
        this.playerId = playerId;
        this.teammateIndex = teammateIndex;
        this.teammate = teammate;
        this.type = type;
        this.stance = stance;
        this.hitpoints = hitpoints;
        this.maximalHitpoints = maximalHitpoints;
        this.actionPoints = actionPoints;
        this.initialActionPoints = initialActionPoints;
        this.visionRange = visionRange;
        this.shootingRange = shootingRange;
        this.shootCost = shootCost;
        this.standingDamage = standingDamage;
        this.kneelingDamage = kneelingDamage;
        this.proneDamage = proneDamage;
        this.damage = damage;
        this.holdingGrenade = holdingGrenade;
        this.holdingMedikit = holdingMedikit;
        this.holdingFieldRation = holdingFieldRation;
    }

    /**
     * @return Возвращает идентификатор игрока, в команду которого входит боец.
     */
    public long getPlayerId() {
        return playerId;
    }

    /**
     * @return Возвращает 0-индексированный номер бойца в команде.
     */
    public int getTeammateIndex() {
        return teammateIndex;
    }

    /**
     * @return Возвращает {@code true}, если и только если данный боец является дружественным.
     */
    public boolean isTeammate() {
        return teammate;
    }

    /**
     * @return Возвращает тип бойца.
     */
    public TrooperType getType() {
        return type;
    }

    /**
     * @return Возвращает стойку бойца.
     */
    public TrooperStance getStance() {
        return stance;
    }

    /**
     * @return Возвращает текущее количество очков здоровья у бойца.
     */
    public int getHitpoints() {
        return hitpoints;
    }

    /**
     * @return Возвращает максимальное количество очков здоровья у бойца.
     */
    public int getMaximalHitpoints() {
        return maximalHitpoints;
    }

    /**
     * @return Возвращает текущее количество очков действия у бойца.
     */
    public int getActionPoints() {
        return actionPoints;
    }

    /**
     * @return Возвращает количество очков действия, которое даётся бойцу в начале хода.
     */
    public int getInitialActionPoints() {
        return initialActionPoints;
    }

    /**
     * @return Возвращает дальность обзора бойца.
     */
    public double getVisionRange() {
        return visionRange;
    }

    /**
     * @return Возвращает дальность стрельбы бойца.
     */
    public double getShootingRange() {
        return shootingRange;
    }

    /**
     * @return Возвращает количество очков действия, необходимое бойцу для совершения выстрела.
     */
    public int getShootCost() {
        return shootCost;
    }

    /**
     * @return Возвращает урон одного выстрела бойца, находящегося в положении стоя.
     */
    public int getStandingDamage() {
        return standingDamage;
    }

    /**
     * @return Возвращает урон одного выстрела бойца, находящегося в положении сидя.
     */
    public int getKneelingDamage() {
        return kneelingDamage;
    }

    /**
     * @return Возвращает урон одного выстрела бойца, находящегося в положении лёжа.
     */
    public int getProneDamage() {
        return proneDamage;
    }

    /**
     * @param stance Стойка бойца для подсчёта урона.
     * @return Возвращает урон одного выстрела бойца в указанной стойке.
     */
    public int getDamage(TrooperStance stance) {
        switch (stance) {
            case PRONE:
                return proneDamage;
            case KNEELING:
                return kneelingDamage;
            case STANDING:
                return standingDamage;
            default:
                throw new IllegalArgumentException("Unsupported stance: " + stance + '.');
        }
    }

    /**
     * @return Возвращает урон одного выстрела бойца в данной стойке.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return Возвращает {@code true}, если и только если данный боец несёт с собой гранату.
     */
    public boolean isHoldingGrenade() {
        return holdingGrenade;
    }

    /**
     * @return Возвращает {@code true}, если и только если данный боец несёт с собой аптечку.
     */
    public boolean isHoldingMedikit() {
        return holdingMedikit;
    }

    /**
     * @return Возвращает {@code true}, если и только если данный боец несёт с собой сухой паёк.
     */
    public boolean isHoldingFieldRation() {
        return holdingFieldRation;
    }
}
