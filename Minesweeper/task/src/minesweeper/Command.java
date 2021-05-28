package minesweeper;

class Command {

    public static final String FREE_CMD = "free";
    public static final String MINE_CMD = "mine";

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
