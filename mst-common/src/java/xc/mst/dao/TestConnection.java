package xc.mst.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("usage: driver(ex com.mysql.jdbc.Driver) url(ex jdbc:mysql://localhost:3306) username password");
            return;
        }
        try {
            Class.forName(args[0]);
            try {
                Connection db_connection =
                        DriverManager.getConnection(args[1], args[2], args[3]);
                db_connection.close();
                System.out.println("connected successfully");
            } catch (Throwable t) {
                System.out.println("unable to connect");
                t.printStackTrace();
            }
        } catch (Throwable t) {
            System.out.println("unable to load driver");
            t.printStackTrace();
        }
    }
}
