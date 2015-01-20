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
@WebService(serviceName = "ContactManagementSOAP", portName = "ContactManagementSOAPPort", endpointInterface = "com.filipovic.ws.soap.management.contact.ContactManagementService", targetNamespace = "http://filipovic.com/ws/soap/management/contact", wsdlLocation = "WEB-INF/wsdl/ContactManagementService/ContactManagementService.wsdl")
public class ContactManagementService {

    public com.filipovic.ws.soap.management.contact.entity.CreateContactResponse createContact(com.filipovic.ws.soap.management.contact.entity.CreateContactRequest createContactRequest) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public com.filipovic.ws.soap.management.contact.entity.ReadContactResponse readContact(com.filipovic.ws.soap.management.contact.entity.ReadContactRequest readContactRequest) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public com.filipovic.ws.soap.management.contact.entity.EditContactResponse editContact(com.filipovic.ws.soap.management.contact.entity.EditContactRequest editContactRequest) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public com.filipovic.ws.soap.management.contact.entity.DeleteContactResponse deleteContact(com.filipovic.ws.soap.management.contact.entity.DeleteContactRequest deleteContactRequest) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public com.filipovic.ws.soap.management.contact.entity.GetItemTypesResponse getItemTypes(java.lang.Object getItemTypesRequest) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
}
