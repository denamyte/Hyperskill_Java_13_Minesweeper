package minesweeper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Minesweeper {

    private static final char UNKNOWN_CELL = '.';
    private static final char MINE = 'X';
    private static final char USER_MARK = '*';
    private static final char SAFE_CELL = '/';

    private static final char PIPE = '|';
    private static final String UNDERLINE = "-|---------|";
    private static final String NUMBER_LINE = "\n |123456789|\n";

    private static final List<int[]> ADJACENT_SHIFTS = List.of(
            shift(-1, -1), shift(-1, 0), shift(-1, 1),
            shift( 0, -1), /*         */ shift( 0, 1),
            shift( 1, -1), shift( 1, 0), shift( 1, 1)
    );

    private final Random RANDOM;
    private char[][] field;
    private char[][] exploredField;
    private final int rows;
    private final int cols;
    private final int cellsCount;

    private int mineCount;
    private int exploredMinesCount;
    private int exploredSavesCount;

    private Set<Integer> mineSet;
    private Set<Integer> mineGuessSet = new HashSet<>();

    enum MoveResult {
        OK, EXPLODED
    }

    public Minesweeper(int rows, int cols) {
        RANDOM = new Random(System.currentTimeMillis());
        prepareFields(rows, cols);
        this.rows = rows;
        this.cols = cols;
        cellsCount = rows * cols;
    }

    private static int[] shift(int row, int col) {
        return new int[]{row, col};
    }

    private void prepareFields(int rows, int cols) {
        field = new char[rows][cols];
        exploredField = new char[rows][cols];
        Stream.of(field, exploredField).forEach(f -> {
            for (char[] row : f) {
                Arrays.fill(row, UNKNOWN_CELL);
            }
        });
    }

    public void mine5(Command cmd, int mineCount) {
        this.mineCount = mineCount;
        generateMineHashes(cmd, mineCount);
        placeMines();
    }

    private void generateMineHashes(Command cmd, int mineCount) {
        final List<Integer> crdHashes = IntStream.range(0, cellsCount).boxed().collect(Collectors.toList());
        crdHashes.remove(crdToHash(cmd.row, cmd.col));

        int count = Math.min(mineCount, cellsCount - 1);
        this.mineSet = IntStream.generate(() -> crdHashes.remove(RANDOM.nextInt(crdHashes.size())))
                .limit(count)
                .boxed()
                .collect(Collectors.toSet());
    }

    private void placeMines() {
        mineSet.forEach(hash -> {
            int[] crd = hashToCrd(hash);
            int row = crd[0];
            int col = crd[1];

            field[row][col] = MINE;
            ADJACENT_SHIFTS.forEach(shift -> incMineCountAtCell(row + shift[0], col + shift[1]));
        });
    }

    private void incMineCountAtCell(int row, int col) {
        if (crdInRange(row, col) && !(field[row][col] == MINE)) {

            final char curChar = field[row][col];
            field[row][col] = curChar == UNKNOWN_CELL ? '1' : (char) (curChar + 1);
        }
    }

    private boolean crdInRange(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public MoveResult makeUserMove5(Command cmd) {
        final int hash = crdToHash(cmd.row, cmd.col);
        switch (cmd.cmdString) {
            case Command.FREE_CMD: {
                if (mineGuessSet.contains(hash)) {
                    break;
                }
                if (isMine(cmd.row, cmd.col)) {
                    explodeField();
                    return MoveResult.EXPLODED;
                }
                freeCells(cmd.row, cmd.col);
            }
            break;
            case Command.MINE_CMD:
            default: {
                if (mineGuessSet.remove(hash)) {
                    exploredField[cmd.row][cmd.col] = UNKNOWN_CELL;
                } else {
                    exploredField[cmd.row][cmd.col] = USER_MARK;
                    mineGuessSet.add(hash);
                }
            }
        }
        return MoveResult.OK;
    }

    private void explodeField() {
        mineSet.stream().map(this::hashToCrd).forEach(crd -> {
            exploredField[crd[0]][crd[1]] = MINE;
        });
    }

    private void freeCells(int row, int col) {
        if (!crdInRange(row, col) || exploredField[row][col] != UNKNOWN_CELL) {
            return;
        }
        boolean unknown = field[row][col] == UNKNOWN_CELL;
        exploredField[row][col] = unknown
                ? SAFE_CELL
                : field[row][col];

        exploredSavesCount++;

        // TODO: 5/28/21
        //  Get rid of this error:
        //  The last grid contains '*' and '/' characters that are next to each other. This situation is impossible. If there is '*' character that is next to '/' it should be replaced to '/' or to a number....
//   |123456789|
//  -|---------|
//  1|/////////|
//  2|/////////|
//  3|/////////|
//  4|////////*|
//  5|/////////|
//  6|///*/////|
//  7|////////*|
//  8|11///////|
//  9|.1///////|
//  -|---------|

        if (unknown) {
            ADJACENT_SHIFTS.forEach(shift -> freeCells(row + shift[0], col + shift[1]));
        }
    }

    public boolean demined() {
        return exploredSavesCount == cellsCount - mineCount || mineGuessSet.equals(mineSet);
    }

    @Deprecated
    public String render() {
        StringBuilder sb = new StringBuilder(NUMBER_LINE);
        sb.append(UNDERLINE).append('\n');
        for (int row = 0; row < rows; row++) {
            sb.append(row + 1).append(PIPE);
            for (int col = 0; col < cols; col++) {
                char sym;
                if (isUserMarked(row, col)) {
                    sym = USER_MARK;
                } else if (field[row][col] == MINE) {
                    sym = UNKNOWN_CELL;
                } else {
                    sym = field[row][col];
                }
                sb.append(sym);
            }
            sb.append(PIPE).append('\n');
        }
        sb.append(UNDERLINE);
        return sb.toString();
    }

    public String render5() {
        StringBuilder sb = new StringBuilder(NUMBER_LINE);
        sb.append(UNDERLINE).append('\n');
        for (int row = 0; row < rows; row++) {
            sb.append(row + 1).append(PIPE);
            sb.append(exploredField[row]);
            sb.append(PIPE);
            sb.append('\n');
        }
        sb.append(UNDERLINE);
        return sb.toString();
    }

    private int crdToHash(int row, int col) {
        return row * this.cols + col;
    }

    private int[] hashToCrd(int hash) {
        return new int[]{hash / this.cols, hash % this.cols};
    }

    private boolean isUserMarked(int row, int col) {
        return mineGuessSet.contains(crdToHash(row, col));
    }

    private boolean isNumber(int row, int col) {
        return field[row][col] >= '1' && field[row][col] <= '8';
    }

    private boolean isMine(int row, int col) {
        return field[row][col] == MINE;
    }

    private boolean isSafeCell(int row, int col) {
        return field[row][col] == SAFE_CELL;
    }
}

