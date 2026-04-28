package main.java.com.lukyanovich.cloudbox.cli;

public class CLIPrinter {

    public static void printAllCommands() {
        System.out.println("Доступные команды: ");
        for (CLICommand command : CLICommand.values()) {
            System.out.printf(" %-10s - %s%n", command.getCommand(), command.getDescription());
        }
    }
}
