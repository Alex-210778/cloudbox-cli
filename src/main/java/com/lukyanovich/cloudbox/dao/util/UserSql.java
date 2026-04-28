package main.java.com.lukyanovich.cloudbox.dao.util;

public final class UserSql {

    public final static String SAVE_USER_SQL = """
            INSERT INTO users (user_name, password_hash) 
            VALUES (?, ?);
            """;
    public final static String FIND_ALL_USERS_SQL = """
                    SELECT id, user_name, password_hash
                    FROM users
            """;
    public final static String FIND_USER_BY_ID_SQL = FIND_ALL_USERS_SQL + """
                      WHERE id = ?
            """;
    public final static String UPDATE_USER_BY_ID_SQL = """
            UPDATE users
            SET user_name = ?,
                password_hash = ?
            WHERE id = ?            
            """;
    public final static String DELETE_USER_BY_ID_SQL = """
            DELETE FROM users
            WHERE id = ?
            """;
    public final static String FIND_USER_BY_USERNAME_SQL = FIND_ALL_USERS_SQL + """
             WHERE user_name = ?
            """;
    public final static String EXISTS_BY_USERNAME_SQL = """
            SELECT count(*)
            FROM users
            WHERE user_name = ?
            """;


    private UserSql() {
    }
}
