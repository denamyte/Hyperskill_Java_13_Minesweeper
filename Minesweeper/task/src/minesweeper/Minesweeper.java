package minesweeper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public void mine(int mineCount) {
        generateMines(mineCount).forEach(flatCrd -> field[flatCrd / rows][flatCrd % rows] = MINE);
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
