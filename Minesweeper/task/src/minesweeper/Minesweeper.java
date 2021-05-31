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

    private static final Collection<Character> EXPLORABLE_CELL_TYPES = List.of(UNKNOWN_CELL, USER_MARK);

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
    private int exploredCellsCount;

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

    public void mine(Command cmd, int mineCount) {
        this.mineCount = mineCount;
        generateMineHashes(cmd, mineCount);
        placeMines();
    }

    private void generateMineHashes(Command excludedCellCmd, int mineCount) {
        final List<Integer> crdHashes = IntStream.range(0, cellsCount).boxed().collect(Collectors.toList());
        crdHashes.remove(crdToHash(excludedCellCmd.row, excludedCellCmd.col));

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

    public MoveResult makeUserMove(Command cmd) {
        final int hash = crdToHash(cmd.row, cmd.col);
        switch (cmd.cmdString) {
            case Command.FREE_CMD:
                if (mineGuessSet.contains(hash)) {
                    break;
                }
                if (field[cmd.row][cmd.col] == MINE) {
                    explodeField();
                    return MoveResult.EXPLODED;
                }
                freeCell(cmd.row, cmd.col);
                break;
            case Command.MINE_CMD:
            default:
                if (mineGuessSet.remove(hash)) {
                    exploredField[cmd.row][cmd.col] = UNKNOWN_CELL;
                } else {
                    exploredField[cmd.row][cmd.col] = USER_MARK;
                    mineGuessSet.add(hash);
                }
        }
        return MoveResult.OK;
    }

    private void explodeField() {
        mineSet.stream().map(this::hashToCrd).forEach(crd -> exploredField[crd[0]][crd[1]] = MINE);
    }

    private void freeCell(int row, int col) {
        if (!crdInRange(row, col) || !EXPLORABLE_CELL_TYPES.contains(exploredField[row][col])) {
            return;
        }
        boolean unknown = field[row][col] == UNKNOWN_CELL;
        exploredField[row][col] = unknown
                ? SAFE_CELL
                : field[row][col];

        exploredCellsCount++;

        if (unknown) {
            ADJACENT_SHIFTS.forEach(shift -> freeCell(row + shift[0], col + shift[1]));
        }
    }

    public boolean isDemined() {
        return exploredCellsCount == cellsCount - mineCount || mineGuessSet.equals(mineSet);
    }

    public String render() {
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
}

