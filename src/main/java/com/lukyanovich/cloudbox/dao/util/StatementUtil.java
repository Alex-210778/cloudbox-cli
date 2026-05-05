package com.lukyanovich.cloudbox.dao.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class StatementUtil {

    private StatementUtil() {
    }

    public static PreparedStatement prepare(Connection connection, String sql, Object... args) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        setParameters(preparedStatement, args);

        return preparedStatement;
    }

    public static PreparedStatement prepareSimple(Connection connection, String sql, Object... args) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        setParameters(preparedStatement, args);

        return preparedStatement;
    }

    private static void setParameters(PreparedStatement preparedStatement, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            preparedStatement.setObject(i + 1, args[i]);
        }
    }
}
