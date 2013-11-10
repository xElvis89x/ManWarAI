import model.Game;
import model.Move;
import model.PlayerContext;
import model.Trooper;

import java.io.IOException;

public final class Runner {
    private final RemoteProcessClient remoteProcessClient;
    private final String token;

    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
            new Runner(args).run();
        } else {
            new Runner(new String[]{"localhost", "31001", "0000000000000000"}).run();
        }
    }

    private Runner(String[] args) throws IOException {
        remoteProcessClient = new RemoteProcessClient(args[0], Integer.parseInt(args[1]));
        token = args[2];
    }

    public void run() throws IOException {
        try {
            remoteProcessClient.writeToken(token);
            int teamSize = remoteProcessClient.readTeamSize();
            remoteProcessClient.writeProtocolVersion();
            Game game = remoteProcessClient.readGameContext();

            Strategy[] strategies = new Strategy[teamSize];

            for (int strategyIndex = 0; strategyIndex < teamSize; ++strategyIndex) {
                strategies[strategyIndex] = new MyStrategy();
            }

            PlayerContext playerContext;

            while ((playerContext = remoteProcessClient.readPlayerContext()) != null) {
                Trooper playerTrooper = playerContext.getTrooper();

                Move move = new Move();
                strategies[playerTrooper.getTeammateIndex()].move(playerTrooper, playerContext.getWorld(), game, move);
                remoteProcessClient.writeMove(move);
            }
        } finally {
            remoteProcessClient.close();
        }
    }
}
