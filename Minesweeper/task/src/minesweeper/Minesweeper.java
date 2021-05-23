package minesweeper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Minesweeper {

    private static final char SAFE_CELL = '.';
    private static final char MINE = 'X';
    private static final char PIPE = '│';
    private static final char USER_MARK = '*';
    private static final String UNDERLINE = "—│—————————│";
    private static final String NUMBER_LINE = "\n │123456789│\n";
    private static final List<int[]> ADJACENT_SHIFTS = List.of(
            shift(-1, -1), shift(-1, 0), shift(-1, 1),
            shift( 0, -1), /*         */ shift( 0, 1),
            shift( 1, -1), shift( 1, 0), shift( 1, 1)
    );

    private final Random RANDOM;
    private final char[][] field;
    private final int rows;
    private final int cols;

    private Set<Integer> mineSet = new HashSet<>();
    private Set<Integer> mineGuessSet = new HashSet<>();

    public Minesweeper(int rows, int cols) {
        RANDOM = new Random(System.currentTimeMillis());
        field = prepareField(rows, cols);
        this.rows = rows;
        this.cols = cols;
    }

    private static int[] shift(int row, int col) {
        return new int[]{row, col};
    }

    private static char[][] prepareField(int rows, int cols) {
        char[][] field = new char[rows][cols];
        for (char[] row : field) {
            Arrays.fill(row, SAFE_CELL);
        }
        return field;
    }

    public void mine(int mineCount) {
        generateMines(mineCount).forEach(this::placeMine);
    }

    private Set<Integer> generateMines(int mineCount) {
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
            field[row][col] = curChar == SAFE_CELL ? '1' : (char) (curChar + 1);
        }
    }

    public boolean makeUserMove(int row, int col) {
        if (isNumber(row, col)) {
            return false;
        }
        final int hash = crdToHash(row, col);
        if (mineGuessSet.remove(hash)) {
            field[row][col] = SAFE_CELL;
        } else {
            field[row][col] = USER_MARK;
            mineGuessSet.add(hash);
        }
        return true;
    }

    public boolean demined() {
        return mineGuessSet.equals(mineSet);
    }

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
                    sym = SAFE_CELL;
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

    private int crdToHash(int row, int col) {
        return 100 * row + col;
    }

    private boolean isUserMarked(int row, int col) {
        return mineGuessSet.contains(crdToHash(row, col));
    }

    private boolean isNumber(int row, int col) {
        return field[row][col] >= '1' && field[row][col] <= '8';
    }
}
