package minesweeper;

import java.util.Scanner;

public class MinesweeperGameplayFacade {

    public static void stage3() {
        try (Scanner scanner = new Scanner(System.in)) {
            Minesweeper minesweeper = new Minesweeper(9, 9);

            System.out.print("How many mines do you want on the field? ");
            minesweeper.mine(scanner.nextInt());
            System.out.println(minesweeper.render());
        }
    }
}
