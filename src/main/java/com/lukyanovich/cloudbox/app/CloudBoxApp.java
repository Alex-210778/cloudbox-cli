package main.java.com.lukyanovich.cloudbox.app;

import main.java.com.lukyanovich.cloudbox.cli.CLIService;
import main.java.com.lukyanovich.cloudbox.db.ConnectionPool;
import main.java.com.lukyanovich.cloudbox.db.DatabaseInitializer;
import main.java.com.lukyanovich.cloudbox.service.UserService;
import main.java.com.lukyanovich.cloudbox.service.StoredFileService;
import main.java.com.lukyanovich.cloudbox.service.impl.UserServiceImpl;
import main.java.com.lukyanovich.cloudbox.service.impl.StoredFileServiceImpl;

public class CloudBoxApp {

    public static void main(String[] args) {
        try {
            DatabaseInitializer.init();

            UserService userService = UserServiceImpl.getInstance();
            StoredFileService storedFileService = StoredFileServiceImpl.getInstance();

            CLIService cliService = new CLIService(userService, storedFileService);
            cliService.run();
        } finally {
            ConnectionPool.closePool();
        }
    }
}
