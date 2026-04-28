package main.java.com.lukyanovich.cloudbox.cli;

import main.java.com.lukyanovich.cloudbox.dto.UserLoginDto;
import main.java.com.lukyanovich.cloudbox.exception.StoredFileServiceException;
import main.java.com.lukyanovich.cloudbox.exception.UserServiceException;
import main.java.com.lukyanovich.cloudbox.model.User;
import main.java.com.lukyanovich.cloudbox.model.StoredFile;
import main.java.com.lukyanovich.cloudbox.service.UserService;
import main.java.com.lukyanovich.cloudbox.service.StoredFileService;

import java.io.IOException;
import java.util.*;

import static main.java.com.lukyanovich.cloudbox.cli.CLIMessages.*;

public class CLIService {

    private final UserService userService;
    private final StoredFileService storedFileService;
    private final Map<CLICommand, Runnable> commandMap = new HashMap<>();

    private User currentUser;
    private boolean running = true;

    public CLIService(UserService userService, StoredFileService storedFileService) {
        this.userService = userService;
        this.storedFileService = storedFileService;
        initCommands();
        validateCommand();
    }

    public void run() {
        System.out.println(WELCOME_TO_APP);
        CLIPrinter.printAllCommands();

        while (running) {
            String input = ConsoleHelper.readTrimmedString("> ");

            if (input.isBlank()) {
                continue;
            }

            handleInput(input);

            if (running) {
                CLIPrinter.printAllCommands();
            }
        }
        System.out.println(GOODBYE);
    }

    private void handleInput(String input) {
        String commandInput = input.split("\\s+")[0].toLowerCase();

        CLICommand.findByCommand(commandInput)
                .ifPresentOrElse(
                        this::executeCommand,
                        () -> {
                            System.out.println(UNKNOWN_COMMAND + commandInput);
                        }
                );
    }

    private void initCommands() {
        commandMap.put(CLICommand.REGISTER, this::register);
        commandMap.put(CLICommand.LOGIN, this::login);
        commandMap.put(CLICommand.LOGOUT, this::logout);
        commandMap.put(CLICommand.UPLOAD, this::upload);
        commandMap.put(CLICommand.LIST, this::list);
        commandMap.put(CLICommand.DELETE, this::delete);
        commandMap.put(CLICommand.DOWNLOAD, this::download);
        commandMap.put(CLICommand.RENAME, this::rename);
        commandMap.put(CLICommand.EXIT, this::exit);
    }

    private void validateCommand() {
        for (CLICommand command : CLICommand.values()) {
            if (!commandMap.containsKey(command)) {
                System.out.printf(COMMAND_HANDLER_NOT_FOUND, command.getCommand());
            }
        }
    }

    private void executeCommand(CLICommand cliCommand) {
        Runnable commandAction = commandMap.get(cliCommand);

        if (commandAction == null) {
            System.out.printf(COMMAND_NOT_IMPLEMENTED, cliCommand.getCommand());
            return;
        }
        commandAction.run();
    }

    private void register() {
        String username = ConsoleHelper.readTrimmedString(USER_NAME);
        String password = ConsoleHelper.readString(PASSWORD);

        try {
            currentUser = userService.save(new User(username, password));
            System.out.println(REGISTRATION_SUCCESS + currentUser.getId());
        } catch (UserServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void login() {
        String username = ConsoleHelper.readTrimmedString(USER_NAME);
        String password = ConsoleHelper.readString(PASSWORD);

        userService.login(new UserLoginDto(username, password))
                .ifPresentOrElse(
                        user -> {
                            currentUser = user;
                            System.out.println(LOGGED_IN_AS + currentUser.getUserName());
                        },
                        () -> System.out.println(WRONG_USERNAME_OR_PASSWORD)
                );
    }

    private void logout() {
        currentUser = null;
        System.out.println(LOGOUT_MESSAGE);
    }

    private void upload() {
        if (!isAuthorized()) {
            return;
        }
        String sourcePath = ConsoleHelper.readTrimmedString(ENTER_THE_PATH_TO_DOWNLOADED_FILE);

        if (sourcePath.isBlank()) {
            System.out.println(WRONG_PATH_TO_FILE);
            return;
        }

        try {
            storedFileService.upload(sourcePath, currentUser.getId());
            System.out.println(FILE_UPLOADED);
        } catch (StoredFileServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void list() {
        if (!isAuthorized()) {
            return;
        }
        List<StoredFile> storedFiles = getUserFiles();

        if (storedFiles.isEmpty()) {
            System.out.printf(FILES_NOT_FOUND, currentUser.getUserName());
        } else {
            storedFiles.stream()
                    .sorted(Comparator.comparingLong(StoredFile::getId))
                    .forEach(System.out::println);
        }
    }

    private void download() {
        if (!isAuthorized()) {
            return;
        }
        String actionName = CLICommand.DOWNLOAD.getShotDescription();

        selectFile(actionName).ifPresent(selected -> {
                    String promptForGetPath = String.format(ENTER_THE_PATH_TO_FILE, actionName);
                    String targetPath = ConsoleHelper.readTrimmedString(promptForGetPath);

                    if (targetPath.isBlank()) {
                        System.out.println(WRONG_PATH_TO_FILE);
                        return;
                    }

                    try {
                        storedFileService.download(
                                selected.getId(),
                                currentUser.getId(),
                                targetPath);
                        System.out.printf(FILE_DOWNLOADED, selected.getFileName());
                    } catch (StoredFileServiceException | IOException e) {
                        System.out.printf(DOWNLOAD_ERROR, e.getMessage());
                    }
                }
        );
    }

    private void rename() {
        if (!isAuthorized()) {
            return;
        }
        String actionName = CLICommand.RENAME.getShotDescription();

        selectFile(actionName).ifPresent(selected -> {
                    String newName = ConsoleHelper.readTrimmedString(ENTER_NEW_FILE_NAME + selected.getFileName());

                    if (newName.isBlank()) {
                        System.out.println(WRONG_FILE_NAME);
                        return;
                    }

                    try {
                        storedFileService.rename(
                                selected.getId(),
                                currentUser.getId(),
                                newName);
                        System.out.printf(FILE_RENAMED, newName);
                    } catch (StoredFileServiceException e) {
                        System.out.println(RENAME_ERROR + e.getMessage());
                    }
                }
        );
    }

    private void delete() {
        if (!isAuthorized()) {
            return;
        }
        String actionName = CLICommand.DELETE.getShotDescription();

        selectFile(actionName).ifPresent(selected -> {
                    try {
                        boolean deleted = storedFileService.delete(
                                selected.getId(),
                                currentUser.getId());
                        System.out.println(deleted ? FILE_DELETED : FILE_NOT_DELETED);
                    } catch (StoredFileServiceException e) {
                        System.out.println(DELETE_ERROR + e.getMessage());
                    }
                }
        );
    }

    private void exit() {
        running = false;
    }

    private boolean isAuthorized() {
        if (currentUser == null) {
            System.out.println(LOG_IN_FIRST);
            return false;
        }
        return true;
    }

    private List<StoredFile> getUserFiles() {
        return storedFileService.findAllByUserId(currentUser.getId());
    }

    private Optional<StoredFile> selectFile(String actionName) {
        List<StoredFile> storedFiles = getUserFiles();

        if (storedFiles.isEmpty()) {
            System.out.printf(NO_FILES, actionName);
            return Optional.empty();
        }

        System.out.println(FILES_FOR + actionName);
        int size = storedFiles.size();

        for (int i = 0; i < size; i++) {
            StoredFile currentStoredFile = storedFiles.get(i);

            System.out.printf("%2d. %-40s (id: %d)%n",
                    i + 1,
                    currentStoredFile.getFileName(),
                    currentStoredFile.getId());
        }
        int indexFile = ConsoleHelper.readInt(ENTER_NUMBER_OF_FILE + actionName, 1, size);

        return Optional.of(storedFiles.get(indexFile - 1));
    }
}
