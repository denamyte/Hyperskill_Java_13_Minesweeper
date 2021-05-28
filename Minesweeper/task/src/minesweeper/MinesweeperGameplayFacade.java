package minesweeper;

import minesweeper.Minesweeper.MoveResult;

import java.util.Scanner;

public class MinesweeperGameplayFacade {

    private final static String SET_UNSET_PROMPT = "Set/unset mines marks or claim a cell as free: ";

    public static void stage5() {
        try (Scanner scanner = new Scanner(System.in)) {
            Minesweeper minesweeper = new Minesweeper(9, 9);

            firstPromptsAndMineField(scanner, minesweeper);

            MoveResult result = regularGameplay(scanner, minesweeper);

            endgame(minesweeper, result);
        }
    }

    private static void firstPromptsAndMineField(Scanner scanner, Minesweeper minesweeper) {
        System.out.print("How many mines do you want on the field? ");
        final int mineCount = Integer.parseInt(scanner.nextLine());

        System.out.println(minesweeper.render5());
        System.out.print(SET_UNSET_PROMPT);

        Command cmd = new Command(scanner.nextLine());
        minesweeper.mine5(cmd, mineCount);

        minesweeper.makeUserMove5(cmd);
    }

    private static MoveResult regularGameplay(Scanner scanner, Minesweeper minesweeper) {
        MoveResult result = MoveResult.OK;
        while (!minesweeper.demined() && result != MoveResult.EXPLODED) {
            System.out.println(minesweeper.render5());

            System.out.print(SET_UNSET_PROMPT);
            Command cmd = new Command(scanner.nextLine());
            result = minesweeper.makeUserMove5(cmd);
        }
        return result;
    }

    private static void endgame(Minesweeper minesweeper, MoveResult result) {
        System.out.println(minesweeper.render5());

        String finalMessage = result.equals(MoveResult.EXPLODED)
                ? "You stepped on a mine and failed!"
                : "Congratulations! You found all the mines!";
        System.out.println(finalMessage);
    }
}
