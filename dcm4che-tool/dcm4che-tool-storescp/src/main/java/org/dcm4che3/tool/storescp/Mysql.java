/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dcm4che3.tool.storescp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mathieu
 */
public class Mysql {
    
    public void insertOrUpdate(Session session){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            Connection connection = DriverManager.getConnection
               ("jdbc:mysql://localhost:3306/mri?autoReconnect=true&useSSL=false", "root", "yv35j0@n3tt3");

            PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM session WHERE uid=?");
            statement.setString(1, session.getUid());
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                if (resultSet.getInt(1) > 0){
                    statement = connection.prepareStatement("DELETE FROM session WHERE uid=?");
                    statement.setString(1, session.getUid());
                    statement.executeUpdate();    
                }
            }
            
            String query = "INSERT INTO session (uid, "
                    + "patientid, "
                    + "gid, "
                    + "studydate, "
                    + "available, "
                    + "metadata) VALUES(?,?,?,?,?,?)";
            
            statement = connection.prepareStatement(query);        
            statement.setString(1, session.getUid());
            statement.setString(2, session.getPatientId());
            statement.setString(3, session.getReferringPhysicianName());
            statement.setString(4, session.getStudyDate());
            statement.setBoolean(5, session.getAvailable());
            statement.setString(6, session.toJson());
            statement.executeUpdate();       
            
            statement.close();
            connection.close();            
          
        } catch (ClassNotFoundException | SQLException | IllegalAccessException | InstantiationException ex) {
            Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
