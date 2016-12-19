/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dcm4che3.tool.storescp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathieu
 */
public class Mysql {
    
    public void insertOrUpdate(Session session){
        Properties prop = new Properties();

        try {
                        
            InputStream input = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
            prop.load(input);
            
            Class.forName(prop.getProperty("jdbc.driverClassName")).newInstance();
            
            try (Connection connection = DriverManager.getConnection
                       (prop.getProperty("jdbc.url"), prop.getProperty("jdbc.username"), prop.getProperty("jdbc.password"))) {
                PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM session WHERE uid=?");
                statement.setString(1, session.getUid());
                ResultSet resultSet = statement.executeQuery();
                
                if(resultSet.next()) {
                    if (resultSet.getInt(1) > 0){
                        String query = "UPDATE session "
                                + "SET uid=?, "
                                + "patient_name=?, "
                                + "patient_id=?, "
                                + "gid=?, "
                                + "studydate=?, "
                                + "available=?, "
                                + "metadata=? "
                                + "WHERE  uid=?";

                        statement = connection.prepareStatement(query);
                        statement.setString(1, session.getUid());
                        statement.setString(2, session.getPatientName());
                        statement.setString(3, session.getPatientId());
                        statement.setString(4, session.getGid());
                        statement.setString(5, session.getStudyDate());
                        statement.setBoolean(6,session.getAvailable());
                        statement.setString(7, session.toJson());
                        statement.setString(8, session.getUid());
                        statement.executeUpdate();                        
                    }
                    else{
                        String query = "INSERT INTO session (uid, "
                                + "patient_name, "                                
                                + "patient_id, "
                                + "gid, "
                                + "studydate, "
                                + "available, "
                                + "metadata) VALUES(?,?,?,?,?,?,?)";

                        statement = connection.prepareStatement(query);
                        statement.setString(1, session.getUid());
                        statement.setString(2, session.getPatientName());
                        statement.setString(3, session.getPatientId());
                        statement.setString(4, session.getGid());
                        statement.setString(5, session.getStudyDate());
                        statement.setBoolean(6, session.getAvailable());
                        statement.setString(7, session.toJson());
                        statement.executeUpdate();                                        
                    }
                }
                statement.close();
                connection.close();
            }
          
        } catch (ClassNotFoundException | SQLException | IllegalAccessException | InstantiationException | IOException ex) {
            Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
