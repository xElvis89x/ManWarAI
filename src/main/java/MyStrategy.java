import model.*;

import java.util.Random;

public final class MyStrategy implements Strategy {
    private final Random random = new Random();

    private static int atackX = -1;
    private static int atackY = -1;

    private static Trooper medic;
    private static Trooper commander;
    private static Trooper soldier;

    @Override
    public void move(Trooper self, World world, Game game, Move move) {
        if (self.getActionPoints() < game.getStandingMoveCost()) {
            return;
        }

        if (atackX != -1 && atackY != -1) {
            if (world.isVisible(self.getShootingRange(), self.getX(), self.getY(), self.getStance(),
                    atackX, atackY, TrooperStance.PRONE)) {
                move.setAction(ActionType.SHOOT);
                move.setX(atackX);
                move.setY(atackY);
                return;
            }
        }


        for (Trooper trooper : world.getTroopers()) {
            System.out.println(trooper.getPlayerId());
            if (!trooper.isTeammate()) {
                if (world.isVisible(self.getShootingRange(), self.getX(), self.getY(), self.getStance(),
                        trooper.getX(), trooper.getY(), trooper.getStance())) {
                    move.setAction(ActionType.SHOOT);
                    move.setX(atackX = trooper.getX());
                    move.setY(atackY = trooper.getY());
                    return;
                }
            } else {
                switch (trooper.getType()) {
                    case SOLDIER:
                        soldier = trooper;
                        break;
                    case COMMANDER:
                        commander = trooper;
                        break;
                    case FIELD_MEDIC:
                        medic = trooper;
                        break;
                }
            }
        }
        atackX = -1;
        atackY = -1;

        if (move.getAction() == ActionType.END_TURN) {
            if (self.getType() == TrooperType.COMMANDER) {
                move.setAction(ActionType.MOVE);
                move.setDirection(Direction.NORTH);


            } else if (self.getType() == TrooperType.FIELD_MEDIC) {
                if (commander.getHitpoints() < commander.getMaximalHitpoints() / 2) {
                    moveToUnit(self, commander, move);
                } else if (soldier.getHitpoints() < soldier.getMaximalHitpoints() / 2) {
                    moveToUnit(self, soldier, move);
                } else {
                    moveToUnit(self, commander, move);
                }
            } else if (self.getType() == TrooperType.SOLDIER) {
                move.setAction(ActionType.MOVE);
                moveToUnit(self, commander, move);
            }
        }
    }

    void moveToUnit(Trooper self, Trooper trooper, Move move) {
        move.setAction(ActionType.MOVE);
        int xdir = trooper.getX() - self.getX();
        int ydir = trooper.getY() - self.getY();
        if (Math.abs(xdir) > Math.abs(ydir)) {
            if (xdir > 0) {
                move.setDirection(Direction.EAST);
            } else if (xdir < 0) {
                move.setDirection(Direction.WEST);
            }
        } else {
            if (ydir > 0) {
                move.setDirection(Direction.SOUTH);
            } else if (ydir < 0) {
                move.setDirection(Direction.NORTH);
            }
        }

    }


}
