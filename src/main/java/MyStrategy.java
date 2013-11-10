import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class MyStrategy implements Strategy {
    private final Random random = new Random();

    private static int atackX = -1;
    private static int atackY = -1;

    private static Trooper medic;
    private static Trooper commander;
    private static Trooper soldier;

    private static Point target;

    private void sout(String s) {
        System.out.println(s);
    }

    @Override
    public void move(Trooper self, World world, Game game, Move move) {
        sout("Move Index =" + world.getMoveIndex());
        if (self.getActionPoints() < game.getStandingMoveCost()) {
            return;
        }

        try {
            if (self.getActionPoints() == 2) {
                return;
            }

            soldier = null;
            commander = null;
            medic = null;

            Trooper attackTrooper = findTrooperByXY(atackX, atackY, world);
            if (attackTrooper != null && !attackTrooper.isTeammate() &&
                    world.isVisible(self.getShootingRange(), self.getX(), self.getY(), self.getStance(),
                            attackTrooper.getX(), attackTrooper.getY(), attackTrooper.getStance())) {
                move.setAction(ActionType.SHOOT);
                move.setX(attackTrooper.getX());
                move.setY(attackTrooper.getY());
                return;
            } else if (attackTrooper == null) {
                atackX = -1;
                atackY = -1;
            }

            for (Trooper trooper : world.getTroopers()) {
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

            if (self.getType() == TrooperType.COMMANDER) {
                commanderStrategy(self, world, move);
            } else if (self.getType() == TrooperType.FIELD_MEDIC) {
                medicStrategy(self, world, move);
            } else if (self.getType() == TrooperType.SOLDIER) {
                soldierStrategy(self, world, move);
            }
        } catch (Throwable throwable) {
            sout("Strategy crashed");
            throwable.printStackTrace();
        } finally {
            sout("self = (" + self.getX() + ";" + self.getY() + "); type =" + self.getType());
            sout("move: action=" + move.getAction() + " x;y=(" + move.getX() + " " + move.getY() + ")");
            if (target != null) {
                sout("target = (" + target.getX() + ";" + target.getY() + ");");
            }
        }
    }

    /**
     * определить цель команды(куда идти если никого рядом не видно)
     *
     * @param self
     * @param world
     */
    private void defineTarget(Trooper self, World world) {
        if (target == null || isTargetComplete(self)) {
            target = new Point(world.getWidth() - self.getX(), world.getHeight() - self.getY());
            int dx, dy, step = 0;
            Point tmp = target;
            while (!checkPointFree(tmp, world, world.getCells())) {
                dx = (int) Math.sin(Math.toRadians(step)) * (step / 360);
                dy = (int) Math.cos(Math.toRadians(step)) * (step / 360);
                step += 90;
                tmp = new Point(target.getX() + dx, target.getY() + dy);
            }
            target = tmp;
        }
    }

    private void soldierStrategy(Trooper self, World world, Move move) {
        if (commander != null) {
            move.setAction(ActionType.MOVE);
            moveToUnit(self, commander, move, world);
        } else {
            commanderStrategy(self, world, move);
        }
    }

    private void medicStrategy(Trooper self, World world, Move move) {
        // 2/3
        if (self.getHitpoints() < self.getMaximalHitpoints() - 3) {
            move.setAction(ActionType.HEAL);
            move.setX(self.getX());
            move.setY(self.getY());
        } else if (commander != null && commander.getHitpoints() < commander.getMaximalHitpoints() - 5) {
            moveAndHeal(self, commander, world, move);
        } else if (soldier != null && soldier.getHitpoints() < soldier.getMaximalHitpoints() - 5) {
            moveAndHeal(self, soldier, world, move);
        } else if (commander != null) {
            moveToUnit(self, commander, move, world);
        } else if (soldier != null) {
            moveToUnit(self, soldier, move, world);
        } else {
            commanderStrategy(self, world, move);
        }
    }

    private void moveAndHeal(Trooper self, Trooper healTarget, World world, Move move) {
        if (Math.abs(self.getX() - healTarget.getX()) + Math.abs(self.getY() - healTarget.getY()) == 1) {
            move.setAction(ActionType.HEAL);
            move.setX(healTarget.getX());
            move.setY(healTarget.getY());
        } else {
            moveToUnit(self, healTarget, move, world);
        }
    }

    Trooper findTrooperByXY(int x, int y, World world) {
        for (Trooper trooper : world.getTroopers()) {
            if (trooper.getX() == x && trooper.getY() == y) {
                return trooper;
            }
        }
        return null;
    }

    Trooper findTrooperById(long id, World world) {
        for (Trooper trooper : world.getTroopers()) {
            if (trooper.getId() == id) {
                return trooper;
            }
        }
        return null;
    }

    CellType[][] getCells(World world, int x1, int y1) {
        CellType[][] cells = world.getCells().clone();
        for (Trooper trooper : world.getTroopers()) {
            if (trooper.getX() != x1 || trooper.getY() != y1) {
                cells[trooper.getX()][trooper.getY()] = CellType.HIGH_COVER;
            }
        }
        return cells;
    }


    private void commanderStrategy(Trooper self, World world, Move move) {
        if (self.getActionPoints() > 4) {
            if ((medic == null && soldier == null)
                    || ((medic != null && self.getDistanceTo(medic) < 4)
                    || (soldier != null && self.getDistanceTo(soldier) < 4))) {
                defineTarget(self, world);
                move.setAction(ActionType.MOVE);
                Point p = findPath(self.getX(), self.getY(), target.getX(), target.getY(), world);
                if (p != null) {
                    move.setX(p.getX());
                    move.setY(p.getY());
                } else {
                    move.setAction(ActionType.END_TURN);
                }
            } else {
            }
        } else {
            if (medic != null) {
                moveToUnit(self, medic, move, world);
            } else if (soldier != null) {
                moveToUnit(self, soldier, move, world);
            }
        }
    }

    private Point findFreeCell(Point p, CellType[][] cellTypes, World world) {
        Point tmp = p;
        int dx, dy, step = 0;
        while (!checkPointFree(tmp, world, cellTypes)) {
            dx = (int) Math.sin(Math.toRadians(step)) * (step / 360);
            dy = (int) Math.cos(Math.toRadians(step)) * (step / 360);
            step += 90;
            tmp = new Point(p.getX() + dx, p.getY() + dy);
        }

        System.out.println("point = " + p.getX() + ";" + p.getY() + "  freePoint = " + tmp.getX() + ";" + tmp.getY());
        return tmp;
    }

    private boolean checkPointFree(Point point, World world, CellType[][] cells) {
        if (point.getX() < 0 || point.getX() > world.getWidth() - 1
                || point.getY() < 0 || point.getY() > world.getHeight() - 1) {
            return false;
        }
        return cells[point.getX()][point.getY()] == CellType.FREE;
    }

    private boolean isTargetComplete(Trooper self) {
        return self.getX() == target.getX() && self.getY() == target.getY();
    }


    void moveToUnit(Trooper self, Trooper trooper, Move move, World world) {
        move.setAction(ActionType.MOVE);
        Point nextPoint = findPath(self.getX(), self.getY(), trooper.getX(), trooper.getY(), world);
        if (nextPoint != null && nextPoint.getX() != trooper.getX() && nextPoint.getY() != trooper.getY()) {
            move.setX(nextPoint.getX());
            move.setY(nextPoint.getY());
            sout("move to: (" + nextPoint.getX() + " ; " + nextPoint.getY() + ") "
                    + "current=(" + self.getX() + ";" + self.getY() + ")"
                    + " step able=" + world.getCells()[nextPoint.getX()][nextPoint.getY()]);
        } else {
            move.setAction(ActionType.END_TURN);
        }
    }


    public Point findPath(int x1, int y1, int x2, int y2, World world) {
        CellType[][] cells = getCells(world, x1, y1);
        Point[] path = new PathFinder(cells).find(new Point(x1, y1), findFreeCell(new Point(x2, y2), cells, world));
        return path != null ? path[1] : null;
    }

    class Point {
        private int x;
        private int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            return (((Point) o).getX() == x) && (((Point) o).getY() == y);
        }
    }

    class PathFinder {
        int[][] fillmap; // Pазмеp == pазмеpу лабиpинта !
        CellType[][] labyrinth;
        List<Point> buf = new ArrayList<Point>();

        PathFinder(CellType[][] labyrinth) {
            this.labyrinth = labyrinth;
            fillmap = new int[labyrinth.length][labyrinth[0].length];
        }

        /* Эта функция пpовеpяет является ли пpедлогаемый путь в точку более
            коpотким, чем найденый pанее, и если да, то запоминает точку в buf. */
        void push(Point p, int n) {
            if (fillmap[p.getX()][p.getY()] <= n) return; // Если новый путь не коpоче, то он нам не нужен
            fillmap[p.getX()][p.getY()] = n; // Иначе запоминаем новую длину пути
            buf.add(p); // Запоминаем точку
        }

        /* Сдесь беpется первая точка из buf, если она есть*/
        Point pop() {
            if (buf.isEmpty()) return null;
            return (Point) buf.remove(0);
        }

        int getLab(int x, int y) {
            return labyrinth[x][y] == CellType.FREE ? 1 : 0;
        }

        Point[] find(Point start, Point end) {
            int tx = 0, ty = 0, n = 0, t = 0;
            Point p;
            // Вначале fillmap заполняется max значением
            for (int[] aFillmap : fillmap) {
                Arrays.fill(aFillmap, Integer.MAX_VALUE);
            }
            push(start, 0); // Путь в начальную точку =0, логично ?
            while ((p = pop()) != null) { // Цикл, пока есть точки в буфеpе
                // n=длина пути до любой соседней клетки
                n = fillmap[p.getX()][p.getY()] + getLab(p.getX(), p.getY());

                //Пеpебоp 4-х соседних клеток
                if ((p.getY() + 1 < labyrinth[p.getX()].length) && getLab(p.getX(), p.getY() + 1) != 0) {
                    push(new Point(p.getX(), p.getY() + 1), n);
                }
                if ((p.getY() - 1 >= 0) && getLab(p.getX(), p.getY() - 1) != 0) {
                    push(new Point(p.getX(), p.getY() - 1), n);
                }

                if ((p.getX() + 1 < labyrinth.length) && (getLab(p.getX() + 1, p.getY()) != 0)) {
                    push(new Point(p.getX() + 1, p.getY()), n);
                }
                if ((p.getX() - 1 >= 0) && (getLab(p.getX() - 1, p.getY()) != 0)) {
                    push(new Point(p.getX() - 1, p.getY()), n);
                }

            }

            if (fillmap[end.getX()][end.getY()] == Integer.MAX_VALUE) {
                return null;
            }

            List<Point> path = new ArrayList<Point>();
            path.add(end);
            int x = end.getX();
            int y = end.getY();
            n = Integer.MAX_VALUE; // Мы начали заливку из начала пути, значит по пути пpидется идти из конца
            while ((x != start.getX()) || (y != start.getY())) { // Пока не пpидем в начало пути
                // Здесь ищется соседняя
                if (y + 1 < fillmap[0].length && fillmap[x][y + 1] < n) {
                    tx = x;
                    ty = y + 1;
                    t = fillmap[x][y + 1];
                }
                // клетка, содеpжащая
                if (y - 1 >= 0 && fillmap[x][y - 1] < n) {
                    tx = x;
                    ty = y - 1;
                    t = fillmap[x][y - 1];
                }
                // минимальное значение
                if (x + 1 < fillmap.length && fillmap[x + 1][y] < n) {
                    tx = x + 1;
                    ty = y;
                    t = fillmap[x + 1][y];
                }
                if (x - 1 >= 0 && fillmap[x - 1][y] < n) {
                    tx = x - 1;
                    ty = y;
                    t = fillmap[x - 1][y];
                }
                x = tx;
                y = ty;
                n = t; // Пеpеходим в найденую клетку
                path.add(new Point(x, y));

            }
            //Мы получили путь, только задом наперед, теперь нужно его перевернуть
            Point[] result = new Point[path.size()];
            t = path.size();
            for (Point point : path) {
                System.out.print(point.getX() + ";" + point.getY() + " - ");
                result[--t] = point;
            }
            System.out.println();
            return result;
        }

    }
}
