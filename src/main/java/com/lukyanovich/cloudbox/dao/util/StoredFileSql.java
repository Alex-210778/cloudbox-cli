package main.java.com.lukyanovich.cloudbox.dao.util;

public final class StoredFileSql {

    public final static String SAVE_STORED_FILE_SQL = """
            INSERT INTO stored_files (file_name, path, upload_date, user_id) 
            VALUES (?, ?, ?, ?);
            """;
    public final static String FIND_ALL_STORED_FILES_SQL = """
            SELECT id, file_name, path, upload_date, user_id
            FROM stored_files
            """;
    public final static String FIND_STORED_FILE_BY_ID_SQL = FIND_ALL_STORED_FILES_SQL + """
            WHERE id = ?
            """;
    public final static String FIND_ALL_STORED_FILES_BY_USER_ID_SQL = FIND_ALL_STORED_FILES_SQL + """
            WHERE user_id = ? 
            ORDER BY upload_date DESC 
            """;
    public static final String FIND_STORED_FILE_BY_ID_AND_USER_ID_SQL = FIND_ALL_STORED_FILES_SQL + """
            WHERE id = ?
             AND user_id = ?
            """;
    public final static String UPDATE_STORED_FILE_BY_ID_SQL = """
            UPDATE stored_files
            SET file_name = ?,
                path = ?,
                upload_date = ?,
                user_id = ?
            WHERE id = ?            
            """;
    public static final String RENAME_STORED_FILE_BY_ID_AND_USER_ID_SQL = """
            UPDATE stored_files
            SET file_name = ?
            WHERE id = ?
             AND user_id = ?
            """;
    public final static String DELETE_STORED_FILE_BY_ID_SQL = """
            DELETE FROM stored_files
            WHERE id = ?
            """;
    public static final String DELETE_STORED_FILE_BY_ID_AND_USER_ID_SQL = DELETE_STORED_FILE_BY_ID_SQL + """
             AND user_id = ?
            """;

    private StoredFileSql() {
    }
}
