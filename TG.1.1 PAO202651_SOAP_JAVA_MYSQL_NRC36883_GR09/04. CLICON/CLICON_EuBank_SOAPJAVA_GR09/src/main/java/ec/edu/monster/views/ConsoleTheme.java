package ec.edu.monster.views;

public final class ConsoleTheme {
    private static final String NAVY = "\u001B[97m";
    private static final String RESET = "\u001B[0m";

    private ConsoleTheme() {
    }

    public static void banner(String title) {
        String text = " " + title + " ";
        String border = "-".repeat(text.length());
        System.out.println("\n" + NAVY + border + RESET);
        System.out.println(NAVY + text + RESET);
        System.out.println(NAVY + border + RESET);
    }

    public static void title(String title) {
        System.out.println("\n" + NAVY + " " + title + " " + RESET);
    }
}
