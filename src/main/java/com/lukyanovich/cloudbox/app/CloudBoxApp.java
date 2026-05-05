package com.lukyanovich.cloudbox.app;

import com.lukyanovich.cloudbox.cli.CLIService;
import com.lukyanovich.cloudbox.db.ConnectionPool;
import com.lukyanovich.cloudbox.db.DatabaseInitializer;
import com.lukyanovich.cloudbox.service.UserService;
import com.lukyanovich.cloudbox.service.StoredFileService;
import com.lukyanovich.cloudbox.service.impl.UserServiceImpl;
import com.lukyanovich.cloudbox.service.impl.StoredFileServiceImpl;

public class CloudBoxApp {

    public static void main(String[] args) {
        boolean connectionPoolInitialized = false;


        try {
            DatabaseInitializer.init();
            connectionPoolInitialized = true;

            UserService userService = UserServiceImpl.getInstance();
            StoredFileService storedFileService = StoredFileServiceImpl.getInstance();

            CLIService cliService = new CLIService(userService, storedFileService);
            cliService.run();
        } catch (Throwable e) {
            System.out.println("Ошибка запуска приложения:");
            e.printStackTrace();
        } finally {
            if (connectionPoolInitialized) {
                ConnectionPool.closePool();
            }        }
    }
}
