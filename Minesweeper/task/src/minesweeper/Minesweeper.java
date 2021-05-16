package minesweeper;

import java.util.*;

public class Minesweeper {

    private static final String SAFE_CELL = ".";
    private static final String MINE = "X";

    private final Random RANDOM;
    private final String[][] field;
    private final int rows;
    private final int cols;

    public Minesweeper(int rows, int cols) {
        RANDOM = new Random(System.currentTimeMillis());
        field = prepareField(rows, cols);
        this.rows = rows;
        this.cols = cols;
    }

    private static String[][] prepareField(int rows, int cols) {
        String[][] field = new String[rows][cols];
        for (String[] row : field) {
            Arrays.fill(row, SAFE_CELL);
        }
        return field;
    }

    public void putMines(int mineCount) {
        if (mineCount > rows * cols / 3) {  // Just not that many
            throw new IllegalArgumentException("Too many mines");
        }

        generateMines(mineCount).forEach(flatCrd -> field[flatCrd / rows][flatCrd % rows] = MINE);
    }

    private Set<Integer> generateMines(int mineCount) {
        Set<Integer> mines = new HashSet<>();
        final int cellCount = rows * cols;
        for (int i = 1; i <= mineCount; i++) {
            while (mines.size() < i) {
                mines.add(RANDOM.nextInt(cellCount));
            }
        }
        return mines;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        for (String[] row : field) {
            sb.append(String.join("", row));
            sb.append('\n');
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }
}
