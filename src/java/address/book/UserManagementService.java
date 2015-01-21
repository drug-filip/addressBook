/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package address.book;

import address.book.util.DataBase;
import address.book.util.GetPropertyValue;
import javax.jws.WebService;
import com.filipovic.ws.soap.management.user.entity.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author Vladimir
 */
@WebService(serviceName = "UserManagementSOAP", portName = "UserManagementSOAPPort", endpointInterface = "com.filipovic.ws.soap.management.user.UserManagementService", targetNamespace = "http://filipovic.com/ws/soap/management/user", wsdlLocation = "WEB-INF/wsdl/UserManagementService/UserManagementService.wsdl")
public class UserManagementService {
    
    private static final Logger logger = LogManager.getLogger("UserManagement");

    public com.filipovic.ws.soap.management.user.entity.CreateUserResponse createUser(com.filipovic.ws.soap.management.user.entity.CreateUserRequest createUserRequest) {
       logger.entry(createUserRequest);
        
        CreateUserResponse response = new CreateUserResponse();
        DataBase db = null;
        try {
            if(createUserRequest==null){
                response.setErrorCode(3);
                response.setErrorMessage("Bad request. Request is empty");
                logger.warn("Bad request. Request is empty");
            } else if( !createUserRequest.getUsername().isEmpty() && !createUserRequest.getPassword().isEmpty() ){
                // configuration object
                GetPropertyValue config = new GetPropertyValue();
                // database object
                db = new DataBase( config.getPropValues("database.url"), config.getPropValues("database.username"), config.getPropValues("database.password"));
                // insert query too create new user
                String queryString = "INSER INTO 'users'('user_id', 'user_name', 'password', 'session_id', ''last_activity) VALUES ('', '" + createUserRequest.getUsername() + "', '" + createUserRequest.getPassword() + "', '', '')";
                db.execIUDQuery(queryString);
                // if there is no error in execute statement set response object
                response.setErrorCode(0);
                // destroy db
                db.CloseDB();
                db = null;
            } else {
                response.setErrorCode(3);
                response.setErrorMessage("Bad request. Username or password is empty");
                logger.warn("Bad request. Username or password is empty");
            }
        } catch ( SQLException e ) {
            response.setErrorCode(1000);
            response.setErrorMessage(e.getMessage());
            db.CloseDB();
            db = null;
            logger.error(e.getMessage());
        } catch ( IOException e) {
            response.setErrorCode(1001);
            response.setErrorMessage(e.getMessage());
            db = null;
            logger.error(e.getMessage());
        } finally {
            logger.exit(response);
            return response;
        }
    }

    public com.filipovic.ws.soap.management.user.entity.LoginResponse login(com.filipovic.ws.soap.management.user.entity.LoginRequest loginRequest) {
        logger.entry(loginRequest);
        LoginResponse response = new LoginResponse();
        DataBase db = null;

        try {
            if(loginRequest==null){
                response.setErrorCode(3);
                response.setErrorMessage("Bad request. Request is empty");
                logger.warn("Bad request. Request is empty");
            }
            else if( !loginRequest.getUsername().isEmpty() && !loginRequest.getPassword().isEmpty() ){
                // configuration object
                GetPropertyValue config = new GetPropertyValue();
                // database object
                db = new DataBase( config.getPropValues("database.url"), config.getPropValues("database.username"), config.getPropValues("database.password"));
                // insert query too create new user
                String queryString = "SELECT 'user_id' FROM 'users' WHERE 'user_name'='" + loginRequest.getUsername() +"' AND 'password'='" + loginRequest.getPassword() +"'";
                ResultSet resultSet = db.execQuery(queryString);
                // if there is no error in execute statement set response object
                if(!resultSet.isBeforeFirst()){
                    response.setErrorCode(2);
                    response.setErrorMessage("There is no user with given credentials.");
                    logger.debug("There is no user with given credentials.");
                } else {
                    resultSet.next();
                    response.setErrorCode(0);
                    response.setErrorMessage(resultSet.getString("user_id"));
                    logger.debug("User '" + resultSet.getString("user_id") + "' is logged in." );
                }
                //destroy db
                db.CloseDB();
                db = null;
            }
            else {
                response.setErrorCode(3);
                response.setErrorMessage("Bad request");
                logger.warn("Bad request.");
            }
        } catch ( SQLException e ) {
            response.setErrorCode(1000);
            response.setErrorMessage(e.getMessage());
            //destroy db
            db.CloseDB();
            db = null;
            logger.error(e.getMessage());
        } catch ( IOException e) {
            response.setErrorCode(1001);
            response.setErrorMessage(e.getMessage());
            //destroy db
            db.CloseDB();
            db = null;
            logger.error(e.getMessage());
        } finally {
            logger.exit(response);
            return response;
        }
        
    }
    
    
}
