import model.*;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class RemoteProcessClient implements Closeable {
    private static final int BUFFER_SIZE_BYTES = 1 << 20;
    private static final ByteOrder PROTOCOL_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
    private static final int INTEGER_SIZE_BYTES = Integer.SIZE / Byte.SIZE;
    private static final int LONG_SIZE_BYTES = Long.SIZE / Byte.SIZE;

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ByteArrayOutputStream outputStreamBuffer;

    private CellType[][] cells;
    private boolean[][][][][] cellVisibilities;

    public RemoteProcessClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        socket.setSendBufferSize(BUFFER_SIZE_BYTES);
        socket.setReceiveBufferSize(BUFFER_SIZE_BYTES);

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        outputStreamBuffer = new ByteArrayOutputStream(BUFFER_SIZE_BYTES);
    }

    public void writeToken(String token) throws IOException {
        writeEnum(MessageType.AUTHENTICATION_TOKEN);
        writeString(token);
        flush();
    }

    public int readTeamSize() throws IOException {
        ensureMessageType(readEnum(MessageType.class), MessageType.TEAM_SIZE);
        return readInt();
    }

    public void writeProtocolVersion() throws IOException {
        writeEnum(MessageType.PROTOCOL_VERSION);
        writeInt(2);
        flush();
    }

    public Game readGameContext() throws IOException {
        ensureMessageType(readEnum(MessageType.class), MessageType.GAME_CONTEXT);
        if (!readBoolean()) {
            return null;
        }

        return new Game(
                readInt(),
                readInt(), readInt(),
                readInt(), readDouble(),
                readInt(), readInt(), readInt(), readInt(),
                readInt(), readDouble(),
                readInt(), readInt(),
                readInt(), readInt(), readInt(),
                readDouble(), readDouble(), readDouble(),
                readDouble(), readDouble(),
                readDouble(), readDouble(),
                readInt(), readDouble(), readInt(), readInt(),
                readInt(), readInt(), readInt(),
                readInt(), readInt()
        );
    }

    public PlayerContext readPlayerContext() throws IOException {
        MessageType messageType = readEnum(MessageType.class);
        if (messageType == MessageType.GAME_OVER) {
            return null;
        }

        ensureMessageType(messageType, MessageType.PLAYER_CONTEXT);
        return readBoolean() ? new PlayerContext(readTrooper(), readWorld()) : null;
    }

    public void writeMove(Move move) throws IOException {
        writeEnum(MessageType.MOVE);

        if (move == null) {
            writeBoolean(false);
        } else {
            writeBoolean(true);

            writeEnum(move.getAction());
            writeEnum(move.getDirection());
            writeInt(move.getX());
            writeInt(move.getY());
        }

        flush();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    private World readWorld() throws IOException {
        if (!readBoolean()) {
            return null;
        }

        return new World(
                readInt(), readInt(), readInt(), readPlayers(), readTroopers(), readBonuses(),
                readCells(), readCellVisibilities()
        );
    }

    private Player[] readPlayers() throws IOException {
        int playerCount = readInt();
        if (playerCount < 0) {
            return null;
        }

        Player[] players = new Player[playerCount];

        for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
            if (readBoolean()) {
                players[playerIndex] = new Player(
                        readLong(), readString(), readInt(), readBoolean(), readInt(), readInt()
                );
            }
        }

        return players;
    }

    private Trooper[] readTroopers() throws IOException {
        int trooperCount = readInt();
        if (trooperCount < 0) {
            return null;
        }

        Trooper[] troopers = new Trooper[trooperCount];

        for (int trooperIndex = 0; trooperIndex < trooperCount; ++trooperIndex) {
            troopers[trooperIndex] = readTrooper();
        }

        return troopers;
    }

    private Trooper readTrooper() throws IOException {
        if (!readBoolean()) {
            return null;
        }

        return new Trooper(
                readLong(), readInt(), readInt(), readLong(),
                readInt(), readBoolean(), readEnum(TrooperType.class), readEnum(TrooperStance.class),
                readInt(), readInt(), readInt(), readInt(),
                readDouble(), readDouble(), readInt(),
                readInt(), readInt(), readInt(), readInt(),
                readBoolean(), readBoolean(), readBoolean()
        );
    }

    private Bonus[] readBonuses() throws IOException {
        int bonusCount = readInt();
        if (bonusCount < 0) {
            return null;
        }

        Bonus[] bonuses = new Bonus[bonusCount];

        for (int bonusIndex = 0; bonusIndex < bonusCount; ++bonusIndex) {
            if (readBoolean()) {
                bonuses[bonusIndex] = new Bonus(
                        readLong(), readInt(), readInt(), readEnum(BonusType.class)
                );
            }
        }

        return bonuses;
    }

    private CellType[][] readCells() throws IOException {
        if (cells != null) {
            return cells;
        }

        int width = readInt();
        if (width < 0) {
            return null;
        }

        cells = new CellType[width][];

        for (int x = 0; x < width; ++x) {
            int height = readInt();
            if (height < 0) {
                continue;
            }

            cells[x] = new CellType[height];

            for (int y = 0; y < height; ++y) {
                cells[x][y] = readEnum(CellType.class);
            }
        }

        return cells;
    }

    private boolean[][][][][] readCellVisibilities() throws IOException {
        if (cellVisibilities != null) {
            return cellVisibilities;
        }

        int worldWidth = readInt();
        if (worldWidth < 0) {
            return null;
        }

        int worldHeight = readInt();
        if (worldHeight < 0) {
            return null;
        }

        int stanceCount = readInt();
        if (stanceCount < 0) {
            return null;
        }

        byte[] rawVisibilities = readBytes(worldWidth * worldHeight * worldWidth * worldHeight * stanceCount);
        cellVisibilities = new boolean[worldWidth][worldHeight][worldWidth][worldHeight][stanceCount];

        int rawVisibilityIndex = 0;

        for (int viewerX = 0; viewerX < worldWidth; ++viewerX) {
            for (int viewerY = 0; viewerY < worldHeight; ++viewerY) {
                for (int objectX = 0; objectX < worldWidth; ++objectX) {
                    for (int objectY = 0; objectY < worldHeight; ++objectY) {
                        for (int stanceIndex = 0; stanceIndex < stanceCount; ++stanceIndex) {
                            cellVisibilities[viewerX][viewerY][objectX][objectY][stanceIndex]
                                    = rawVisibilities[rawVisibilityIndex++] != 0;
                        }
                    }
                }
            }
        }

        return cellVisibilities;
    }

    private static void ensureMessageType(MessageType actualType, MessageType expectedType) {
        if (actualType != expectedType) {
            throw new IllegalArgumentException(String.format(
                    "Received wrong message [actual=%s, expected=%s].", actualType, expectedType
            ));
        }
    }

    private <E extends Enum> E readEnum(Class<E> enumClass) throws IOException {
        byte ordinal = readBytes(1)[0];

        E[] values = enumClass.getEnumConstants();
        int valueCount = values.length;

        for (int valueIndex = 0; valueIndex < valueCount; ++valueIndex) {
            E value = values[valueIndex];
            if (value.ordinal() == ordinal) {
                return value;
            }
        }

        return null;
    }

    private <E extends Enum> void writeEnum(E value) throws IOException {
        writeBytes(new byte[]{value == null ? (byte) -1 : (byte) value.ordinal()});
    }

    private String readString() throws IOException {
        int length = readInt();
        if (length == -1) {
            return null;
        }

        try {
            return new String(readBytes(length), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 is unsupported.", e);
        }
    }

    private void writeString(String value) throws IOException {
        if (value == null) {
            writeInt(-1);
            return;
        }

        byte[] bytes;
        try {
            bytes = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 is unsupported.", e);
        }

        writeInt(bytes.length);
        writeBytes(bytes);
    }

    private boolean readBoolean() throws IOException {
        return readBytes(1)[0] != 0;
    }

    private boolean[] readBooleanArray(int count) throws IOException {
        byte[] bytes = readBytes(count);
        boolean[] booleans = new boolean[count];

        for (int i = 0; i < count; ++i) {
            booleans[i] = bytes[i] != 0;
        }

        return booleans;
    }

    private void writeBoolean(boolean value) throws IOException {
        writeBytes(new byte[]{value ? (byte) 1 : (byte) 0});
    }

    private int readInt() throws IOException {
        return ByteBuffer.wrap(readBytes(INTEGER_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getInt();
    }

    private void writeInt(int value) throws IOException {
        writeBytes(ByteBuffer.allocate(INTEGER_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putInt(value).array());
    }

    private long readLong() throws IOException {
        return ByteBuffer.wrap(readBytes(LONG_SIZE_BYTES)).order(PROTOCOL_BYTE_ORDER).getLong();
    }

    private void writeLong(long value) throws IOException {
        writeBytes(ByteBuffer.allocate(LONG_SIZE_BYTES).order(PROTOCOL_BYTE_ORDER).putLong(value).array());
    }

    private double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    private void writeDouble(double value) throws IOException {
        writeLong(Double.doubleToLongBits(value));
    }

    private byte[] readBytes(int byteCount) throws IOException {
        byte[] bytes = new byte[byteCount];
        int offset = 0;
        int readByteCount;

        while (offset < byteCount && (readByteCount = inputStream.read(bytes, offset, byteCount - offset)) != -1) {
            offset += readByteCount;
        }

        if (offset != byteCount) {
            throw new IOException(String.format("Can't read %d bytes from input stream.", byteCount));
        }

        return bytes;
    }

    private void writeBytes(byte[] bytes) throws IOException {
        outputStreamBuffer.write(bytes);
    }

    private void flush() throws IOException {
        outputStream.write(outputStreamBuffer.toByteArray());
        outputStreamBuffer.reset();
        outputStream.flush();
    }

    private enum MessageType {
        UNKNOWN,
        GAME_OVER,
        AUTHENTICATION_TOKEN,
        TEAM_SIZE,
        PROTOCOL_VERSION,
        GAME_CONTEXT,
        PLAYER_CONTEXT,
        MOVE
    }
}
