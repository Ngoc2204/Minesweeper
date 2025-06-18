/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Administrator
 */
public class KetNoiCSDL {
    public static Connection openConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        System.out.println("Loading...");
        
        String url = "jdbc:sqlserver://localhost:1433;databaseName=MINESWEEPER;encrypt=true;trustServerCertificate=true";

        String user="sa";
        String password="123";
        
        Connection con = DriverManager.getConnection(url, user, password);            
        System.out.println("Connecting...");
        return con;
    }
}
