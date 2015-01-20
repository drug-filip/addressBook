/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package address.book.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DataBase {
    
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    
    /**
     * Database class to connect and manipulate with database 
     * @param dbUrl - Url of the database
     * @param dbUsername - user name to connect to data base
     * @param dbPassword - password to connect to data base
     * @throws SQLException 
     */
    public DataBase( String dbUrl, String dbUsername, String dbPassword ) throws SQLException{
        try {
            dbUrl = "jdbc:mysql://" + dbUrl;
            connection = (Connection) DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            statement = (Statement) connection.createStatement();
        } catch(SQLException e) {
            throw e;
        }  
    }
    
    
    /**
     * Execute query. Most appropriate for SELECT statement
     * @param queryString - Select * from table_name 
     * @return ResultSet
     * @throws SQLException 
     */
    public ResultSet execQuery(String queryString) throws SQLException{
        resultSet=null;
        try {
            resultSet=statement.executeQuery(queryString);
        } catch(SQLException e) {
            throw e;
        }
        return resultSet;
    }
    
    
    
   /**
    * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement or an SQL statement that returns nothing
    * @param querString
    * @throws SQLException 
    */
    public void execIUDQuery(String querString) throws SQLException{
        try{
            statement.executeUpdate(querString);
        } catch(SQLException e) {
            throw e;
        }
       
    }
    
    /**
     * Start transaction in MySql
     * @throws SQLException 
     */
    public void startTransaction() throws SQLException {
        try {
            statement.executeUpdate("START TRANSACTION");
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Commit MySql transaction
     * @throws SQLException 
     */
    public void commitTransaction() throws SQLException{
        try {
            statement.executeUpdate("COMMIT");
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Rollback MySql transaction
     * @throws SQLException 
     */
    public void rollbackTransaction() throws SQLException{
        try {
            statement.executeUpdate("ROLLBACK");
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Close all connection, statement and resultset
     * @throws SQLException 
     */
    public void CloseDB () throws SQLException{
        try {
            if( !connection.isClosed() ){
                connection.close();
            }
            if( !statement.isClosed() ){
                statement.close();
            }
            if( !resultSet.isClosed() ){
                resultSet.close();
            }
            connection = null;
            statement = null;
            resultSet = null;
        } catch (SQLException e) {
            throw e;
        }
       
    }
    
}
