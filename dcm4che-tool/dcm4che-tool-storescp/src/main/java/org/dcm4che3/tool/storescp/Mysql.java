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
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author mathieu
 */
public class Mysql {
    
    public Mysql(){
    
    }
    
    public Boolean exist(String uid){
        boolean exists = false;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            Connection connection = DriverManager.getConnection
               ("jdbc:mysql://localhost:3306/mri?autoReconnect=true&useSSL=false", "root", "yv35j0@n3tt3");

            PreparedStatement statement = connection.prepareStatement("SELECT count(*) FROM session WHERE uid=?");
            statement.setString(1, uid);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                exists = resultSet.getInt(1) > 0;
            }
            statement.close();
            connection.close();            
          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Mysql.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    return exists;
    }


    /*
    String myDriver = "org.gjt.mm.mysql.Driver";
    String myUrl = "jdbc:mysql://localhost/test";
    Class.forName(myDriver);
    Connection conn = DriverManager.getConnection(myUrl, "root", "yv35j0@n3tt3");

    String query = "SELECT * FROM session";



    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery(query);

    while (rs.next())
    {
        int id = rs.getInt("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        Date dateCreated = rs.getDate("date_created");
        boolean isAdmin = rs.getBoolean("is_admin");
        int numPoints = rs.getInt("num_points");
    }
    st.close();
*/

}
