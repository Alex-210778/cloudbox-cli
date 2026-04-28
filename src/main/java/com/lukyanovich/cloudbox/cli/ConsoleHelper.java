package main.java.com.lukyanovich.cloudbox.cli;

import java.util.Scanner;

import static main.java.com.lukyanovich.cloudbox.cli.CLIMessages.*;

public final class ConsoleHelper {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleHelper() {
    }

    public static String readString(String prompt) {
        System.out.println(prompt);
        return SCANNER.nextLine();
    }

    public static String readTrimmedString(String prompt) {
        System.out.println(prompt);
        return SCANNER.nextLine().trim();
    }

    public static int readInt(String prompt, int minValue, int maxValue) {
        while (true) {
            try {
                int value = Integer.parseInt(readTrimmedString(prompt));

                if (value >= minValue && value <= maxValue) {
                    return value;
                }
                System.out.println(INCORRECT_VALUE);
            } catch (NumberFormatException e) {
                System.out.println(ENTER_VALUE);
            }
        }
    }
}
