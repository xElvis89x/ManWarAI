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

    @Override
    public void move(Trooper self, World world, Game game, Move move) {
        System.out.println("Move Index =" + world.getMoveIndex());
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
            if (attackTrooper != null &&
                    world.isVisible(self.getShootingRange(), self.getX(), self.getY(), self.getStance(),
                            attackTrooper.getX(), attackTrooper.getY(), attackTrooper.getStance())) {
                move.setAction(ActionType.SHOOT);
                move.setX(attackTrooper.getX());
                move.setY(attackTrooper.getY());
                return;
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
            System.out.println("Strategy crashed");
            throwable.printStackTrace();
        } finally {
            System.out.println("move: type=" + self.getType() + " action=" + move.getAction() + " x;y=(" + move.getX() + " " + move.getY() + ")");
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
        if (self.getHitpoints() * 3 < self.getMaximalHitpoints() * 2) {
            move.setAction(ActionType.HEAL);
            move.setX(self.getX());
            move.setY(self.getY());
        } else if (commander != null && commander.getHitpoints() * 3 < commander.getMaximalHitpoints() * 2) {
            if (Math.abs(self.getX() - commander.getX()) + Math.abs(self.getY() - commander.getY()) == 1) {
                move.setAction(ActionType.HEAL);
                move.setX(commander.getX());
                move.setY(commander.getY());
            } else {
                moveToUnit(self, commander, move, world);
            }
        } else if (soldier != null && soldier.getHitpoints() * 3 < soldier.getMaximalHitpoints() * 2) {
            if (Math.abs(self.getX() - soldier.getX()) + Math.abs(self.getY() - soldier.getY()) == 1) {
                move.setAction(ActionType.HEAL);
                move.setX(soldier.getX());
                move.setY(soldier.getY());
            } else {
                moveToUnit(self, soldier, move, world);
            }
        } else if (commander != null) {
            moveToUnit(self, commander, move, world);
        } else if (soldier != null) {
            moveToUnit(self, soldier, move, world);
        } else {
            commanderStrategy(self, world, move);
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


    private void commanderStrategy(Trooper self, World world, Move move) {
        if (self.getActionPoints() > 6) {
            if (target == null || (self.getX() == target.getX() && self.getY() == target.getY())) {
                target = new Point(world.getWidth() - self.getX(), world.getHeight() - self.getY());
            }
            move.setAction(ActionType.MOVE);
            Point p = findPath(self.getX(), self.getY(), target.getX(), target.getY(), world);
            if (p != null) {
                move.setX(p.getX());
                move.setY(p.getY());
//                System.out.println("commander move to:" + p.getX() + " ; " + p.getY());
            } else {
                move.setAction(ActionType.END_TURN);
            }
        } else {
            move.setAction(ActionType.END_TURN);
//            System.out.println("end turn");
        }
    }


    void moveToUnit(Trooper self, Trooper trooper, Move move, World world) {
        move.setAction(ActionType.MOVE);
        Point nextPoint = findPath(self.getX(), self.getY(), trooper.getX(), trooper.getY(), world, false);
        if (nextPoint != null
                && nextPoint.getX() != trooper.getX()
                && nextPoint.getY() != trooper.getY()) {
            move.setX(nextPoint.getX());
            move.setY(nextPoint.getY());
            System.out.println("move to: (" + nextPoint.getX() + " ; " + nextPoint.getY() + ") "
                    + "current=(" + self.getX() + ";" + self.getY() + ")"
                    + " step able=" + world.getCells()[nextPoint.getX()][nextPoint.getY()]);
        } else {
            move.setAction(ActionType.END_TURN);
        }
    }

    private Point findPath(int x1, int y1, int x2, int y2, World world) {
        return findPath(x1, y1, x2, y2, world, true);
    }

    public Point findPath(int x1, int y1, int x2, int y2, World world, boolean isEndPointFree) {
        CellType[][] cells = world.getCells().clone();
        for (Trooper trooper : world.getTroopers()) {
            if (trooper.getX() != x1 && trooper.getY() != y1
                    && trooper.getX() != x2 && trooper.getY() != 2) {
                cells[trooper.getX()][trooper.getY()] = CellType.HIGH_COVER;
            }
        }
        Point[] path = new PathFinder(cells).find(new Point(x1, y1), new Point(x2, y2));
        if (path != null
                && (isEndPointFree ? path.length > 1 : path.length > 2)
                && cells[path[1].getX()][path[1].getY()] == CellType.FREE) {
            return path[1];
        } else {
//            System.out.println("find return null");
            return null;
        }

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
        List buf = new ArrayList();

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
            for (int i = 0; i < fillmap.length; i++)
                Arrays.fill(fillmap[i], Integer.MAX_VALUE);
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

            List path = new ArrayList();
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
            for (Object point : path)
                result[--t] = (Point) point;
            return result;
        }

    }
}
