package minesweeper;

import java.util.Scanner;

public class MinesweeperGameplayFacade {

    public static void stage4() {
        try (Scanner scanner = new Scanner(System.in)) {
            Minesweeper minesweeper = new Minesweeper(9, 9);

            System.out.print("How many mines do you want on the field? ");
            minesweeper.mine(scanner.nextInt());

            while (!minesweeper.demined()) {
                System.out.println(minesweeper.render());
                boolean correctMove = false;

                while (!correctMove) {
                    System.out.print("Set/delete mines marks (x and y coordinates): ");
                    int col = scanner.nextInt() - 1;
                    int row = scanner.nextInt() - 1;
                    correctMove = minesweeper.makeUserMove(row, col);
                    if (!correctMove) {
                        System.out.println("There is a number here!");
                    }
                }
            }

            System.out.println(minesweeper.render());
            System.out.println("Congratulations! You found all the mines!");
        }
    }

    private final static String SET_UNSET_PROMPT = "Set/unset mines marks or claim a cell as free: ";

    public static void stage5() {
        try (Scanner scanner = new Scanner(System.in)) {
            Minesweeper minesweeper = new Minesweeper(9, 9);

            firstPromptsAndMineField(scanner, minesweeper);

        }
    }

    private static void firstPromptsAndMineField(Scanner scanner, Minesweeper minesweeper) {
        System.out.print("How many mines do you want on the field? ");
        final int mineCount = scanner.nextInt();

        System.out.println(minesweeper.render5());
        System.out.println(SET_UNSET_PROMPT);
        String rawCrd = scanner.nextLine();

        minesweeper.mine5(rawCrd, mineCount);
    }
}
