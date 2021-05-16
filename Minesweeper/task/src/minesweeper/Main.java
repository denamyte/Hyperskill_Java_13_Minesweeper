package minesweeper;

public class Main {
    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper(9, 9);
        minesweeper.putMines(10);
        System.out.println(minesweeper.render());
    }
}
