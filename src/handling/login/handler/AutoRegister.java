package handling.login.handler;

import client.LoginCrypto;
import database.DatabaseConnection;

import java.sql.*;
import java.util.Calendar;

public class AutoRegister {

    private static final int ACCOUNTS_PER_MAC = 2;
    public static int registeredId = -1;
    public static boolean success = false;
    public static boolean macAllowed = true;

    public static boolean getAccountExists(String login) {
        boolean accountExists = false;
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM accounts WHERE name = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                accountExists = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return accountExists;
    }

    public static void createAccount(String login, String pwd, String sockAddr, String macData) {
        Connection con;

        try {
            con = DatabaseConnection.getConnection();
        } catch (Exception ex) {
            System.out.println(ex);
            return;
        }

        try {
            ResultSet rs;
            try (PreparedStatement ipc = con.prepareStatement("SELECT Macs FROM accounts WHERE macs = ?")) {
                ipc.setString(1, macData);
                rs = ipc.executeQuery();
                macAllowed = rs.getRow() < ACCOUNTS_PER_MAC;
                if (rs.first() == false || rs.last() == true && macAllowed) {
                    try {
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO accounts (name, password, email, birthday, macs, SessionIP) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                            Calendar c = Calendar.getInstance();
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH) + 1;
                            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
                            ps.setString(1, login);
                            ps.setString(2, LoginCrypto.hexSha1(pwd));
                            ps.setString(3, "autoregister@mail.com");
                            ps.setString(4, year + "-" + month + "-" + dayOfMonth);//Created day
                            ps.setString(5, macData);
                            ps.setString(6, sockAddr.substring(1, sockAddr.lastIndexOf(':')));
                            ps.executeUpdate();

                            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    registeredId = generatedKeys.getInt(1);
                                }
                                else {
                                    throw new SQLException("Creating user failed, no ID obtained.");
                                }
                            }
                        }
                        success = true;

                    } catch (SQLException ex) {
                        System.out.println(ex);
                        return;
                    }
                }
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
}
