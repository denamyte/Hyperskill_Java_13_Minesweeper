package minesweeper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Minesweeper {

    private static final char UNKNOWN_CELL = '.';
    private static final char MINE = 'X';
    private static final char USER_MARK = '*';
    private static final char SAFE_CELL = '/';

    private static final char PIPE = '│';
    private static final String UNDERLINE = "—│—————————│";
    private static final String NUMBER_LINE = "\n │123456789│\n";

    private static final String FREE_CMD = "free";
    private static final String MINE_CMD = "mine";

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

    private int mineCount;
    private int exploredMinesCount;
    private int exploredSavesCount;

    private Set<Integer> mineSet = new HashSet<>();
    private Set<Integer> mineGuessSet = new HashSet<>();

    static class Command {
        public final int row;
        public final int col;
        public final String cmdString;

        public Command(String rawCmd) {
            final String[] cmdAr = rawCmd.split("\\s+");
            col = Integer.parseInt(cmdAr[0]) - 1;
            row = Integer.parseInt(cmdAr[1]) - 1;
            cmdString = cmdAr[2];
        }
    }

    public Minesweeper(int rows, int cols) {
        RANDOM = new Random(System.currentTimeMillis());
        prepareFields(rows, cols);
        this.rows = rows;
        this.cols = cols;
    }

    private static int[] shift(int row, int col) {
        return new int[]{row, col};
    }

    private void prepareFields(int rows, int cols) {
        field = new char[rows][cols];
        for (char[] row : field) {
            Arrays.fill(row, UNKNOWN_CELL);
        }
        this.exploredField = field.clone();
    }

    @Deprecated
    public void mine(int mineCount) {
        this.mineCount = mineCount;
        generateMines(mineCount).forEach(this::placeMine);
    }

    public void mine5(String safeRawCrd, int mineCount) {

        // TODO: 5/26/21 Create a Command and place mines except the coordinates in safeRawCrd

        this.mineCount = mineCount;
        generateMines(mineCount).forEach(this::placeMine);
    }

    @Deprecated
    private Set<Integer> generateMines(int mineCount) {
        final List<Integer> crdList = IntStream.range(0, rows * cols).boxed().collect(Collectors.toList());

        Set<Integer> mines = new HashSet<>();
        int count = Math.min(mineCount, rows * cols);
        while (count-- > 0) {
            mines.add(crdList.remove(RANDOM.nextInt(crdList.size())));
        }
        return mines;
    }

    private Set<Integer> generateMines5(int mineCount) {

        // TODO: 5/26/21 Accept Command and exclude its coordinates from available ones for mine generating

        final List<Integer> crdList = IntStream.range(0, rows * cols).boxed().collect(Collectors.toList());

        Set<Integer> mines = new HashSet<>();
        int count = Math.min(mineCount, rows * cols);
        while (count-- > 0) {
            mines.add(crdList.remove(RANDOM.nextInt(crdList.size())));
        }
        return mines;
    }

    private void placeMine(int flatCrd) {
        int row = flatCrd / rows;
        int col = flatCrd % rows;

        field[row][col] = MINE;
        mineSet.add(crdToHash(row, col));

        ADJACENT_SHIFTS.forEach(shift -> incMineCountAtCell(row + shift[0], col + shift[1]));
    }

    private void incMineCountAtCell(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols
                && !(field[row][col] == MINE)) {

            final char curChar = field[row][col];
            field[row][col] = curChar == UNKNOWN_CELL ? '1' : (char) (curChar + 1);
        }
    }

    public boolean makeUserMove(int row, int col) {
        if (isNumber(row, col)) {
            return false;
        }
        final int hash = crdToHash(row, col);
        if (mineGuessSet.remove(hash)) {
            field[row][col] = UNKNOWN_CELL;
        } else {
            field[row][col] = USER_MARK;
            mineGuessSet.add(hash);
        }
        return true;
    }

    public boolean demined() {
        return mineGuessSet.equals(mineSet);
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
        sb.append(UNDERLINE);
        for (int row = 0; row < rows; row++) {
            sb.append(row + 1).append(PIPE);
            sb.append(field[row]);
            sb.append(PIPE);
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
}
