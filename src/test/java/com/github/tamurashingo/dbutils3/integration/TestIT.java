package com.github.tamurashingo.dbutils3.integration;

import com.github.tamurashingo.dbutils3.DBConnectionUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;

public class TestIT {

    private static final String DB_USER = System.getenv("MYSQL_DBUSER");
    private static final String DB_PASS = System.getenv("MYSQL_DBPASS");
    private static final String DB_HOST = System.getenv("MYSQL_DBHOST");
    private static final String DB_PORT = System.getenv("MYSQL_DBPORT");
    private static final String DB_NAME = System.getenv("MYSQL_DBNAME");

    private static final String URL = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;


    private static DBConnectionUtil mysqlConnection() throws SQLException {

        Connection conn = DriverManager.getConnection(URL, DB_USER, DB_PASS);
        DBConnectionUtil dbconn = new DBConnectionUtil(conn);
        return dbconn;
    }

    @BeforeClass
    public static void beforeAll() throws Exception {
        try (DBConnectionUtil dbconn = mysqlConnection()) {
            dbconn.prepare(
                    "      CREATE TABLE "
                            + "   DBUTIL "
                            + " ("
                            + "   ID INTEGER NOT NULL, "
                            + "   NAME VARCHAR(20), "
                            + "   CREATE_DATE DATETIME, "
                            + "   UPDATE_DATE DATETIME, "
                            + "   PRIMARY KEY(ID) "
                            + " ) "
            );
            dbconn.executeUpdate();
        }
    }


    @Test
    public void testNormalParameters() throws Exception {
        try (DBConnectionUtil dbconn = mysqlConnection()) {
            dbconn.prepare(
                    "      INSERT INTO "
                            + "   DBUTIL "
                            + " ( "
                            + "   ID, "
                            + "   NAME, "
                            + "   CREATE_DATE, "
                            + "   UPDATE_DATE "
                            + " ) "
                            + " VALUES ( "
                            + "   ?, " // ID
                            + "   ?, " // NAME
                            + "   ?, "  // CREATE_DATE
                            + "   ? "   // UPDATE_DATE
                            + " ) "
            );
            dbconn.executeUpdate("1", "TARO", Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
        }
    }
}
