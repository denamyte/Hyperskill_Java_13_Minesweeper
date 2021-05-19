package minesweeper;

import java.util.Scanner;

public class MinesweeperGameplayFacade {

    public static void stage2() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("How many mines do you want on the field? ");
            final int mineCount = scanner.nextInt();

            Minesweeper minesweeper = new Minesweeper(9, 9);
            minesweeper.mine(mineCount);
            System.out.println(minesweeper.render());
        }
    }
}
