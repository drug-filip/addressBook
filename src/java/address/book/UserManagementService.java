/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package address.book;

import javax.jws.WebService;

/**
 *
 * @author Vladimir
 */
@WebService(serviceName = "UserManagementSOAP", portName = "UserManagementSOAPPort", endpointInterface = "com.filipovic.ws.soap.management.user.UserManagementService", targetNamespace = "http://filipovic.com/ws/soap/management/user", wsdlLocation = "WEB-INF/wsdl/UserManagementService/UserManagementService.wsdl")
public class UserManagementService {

    public com.filipovic.ws.soap.management.user.entity.CreateUserResponse createUser(com.filipovic.ws.soap.management.user.entity.CreateUserRequest createUserRequest) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public com.filipovic.ws.soap.management.user.entity.LoginResponse login(com.filipovic.ws.soap.management.user.entity.LoginRequest loginRequest) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
}
