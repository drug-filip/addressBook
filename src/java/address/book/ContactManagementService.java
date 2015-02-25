/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package address.book;

import address.book.util.DataBase;
import address.book.util.GetPropertyValue;
import com.filipovic.ws.soap.Response;
import com.filipovic.ws.soap.management.contact.entity.*;
import com.filipovic.ws.soap.management.contact.entity.Contact.ContactItems;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.jws.WebService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author Vladimir
 */
@WebService(serviceName = "ContactManagementSOAP", portName = "ContactManagementSOAPPort", endpointInterface = "com.filipovic.ws.soap.management.contact.ContactManagementService", targetNamespace = "http://filipovic.com/ws/soap/management/contact", wsdlLocation = "WEB-INF/wsdl/ContactManagementService/ContactManagementService.wsdl")
public class ContactManagementService {

    private static final Logger logger = LogManager.getLogger("UserManagement");

    public com.filipovic.ws.soap.management.contact.entity.CreateContactResponse createContact(com.filipovic.ws.soap.management.contact.entity.CreateContactRequest createContactRequest) {
        logger.entry(createContactRequest);
        CreateContactResponse response = new CreateContactResponse();
        DataBase db = null;
        try {
            if( createContactRequest==null || createContactRequest.getFirstName()==null ){
                response.setErrorCode(3);
                response.setErrorMessage("Bad request. Request is null.");
                logger.warn("Bad request. Request is null.");
            } else if( !createContactRequest.getFirstName().isEmpty() && createContactRequest.getUserId() > 0 ) {   

                    GetPropertyValue config = new GetPropertyValue();
                    // database object
                    db = new DataBase( config.getPropValues("database.url"), config.getPropValues("database.username"), config.getPropValues("database.password"));
                    // Sql query - insert new contact
                    String queryString = "INSERT INTO contacts ( user_id, first_name, last_name, city, address, description) VALUES ("
                            + "'" + createContactRequest.getUserId() + "', "
                            + "'" + createContactRequest.getFirstName() + "', "
                            + "'" + createContactRequest.getLastName()+ "', "
                            + "'" + createContactRequest.getCity() + "', "
                            + "'" + createContactRequest.getAddress() + "', "
                            + "'" + createContactRequest.getDescription() + "' "
                            + ")";
                    logger.trace("Query string: " + queryString);
                    db.execIUDQuery(queryString);
                 
                    if( createContactRequest.getContactItems().size() > 0 ){
                        logger.debug("Get Contact id created contact.");
                        // get contact id 
                        queryString = "SELECT contact_id FROM contacts WHERE user_id='" + createContactRequest.getUserId() + "' ORDER BY contact_id DESC LIMIT 1";
                        logger.trace("Query string: " + queryString);
                        ResultSet resultSet = db.execQuery(queryString);
                        resultSet.next();
                        int contact_id = resultSet.getInt("contact_id");
                        logger.trace("Contact ID: " + contact_id);
                        // get contact items list
                        List contactItemList = createContactRequest.getContactItems();
                        // put all contact items to db
                        logger.debug("Inser new contact items into DB");
                        for( int i=0; i < contactItemList.size(); i++  ){
                            ContactItem contactItem = (ContactItem) contactItemList.get(i);
                            queryString = "INSERT INTO contact_items (contact_id, type, item_value, description) VALUES ('" + contact_id + "', '" + contactItem.getType() + "', '" + contactItem.getItemValue() + "', '" + contactItem.getDescription() + "')";
                            logger.trace("Query string: " + queryString);
                            db.execIUDQuery(queryString);
                        }
                    }
                    db.commitTransaction();
                    response.setErrorCode(0);
                    db.CloseDB();
                    db=null;
            } else {
                response.setErrorCode(3);
                response.setErrorMessage("Bad request");
                logger.warn("Bad request");
            }
        } catch (IOException ex) {
            response.setErrorCode(1001);
            response.setErrorMessage( ex.getMessage() );
            logger.error(ex.getMessage());
            db.CloseDB();
            db=null;
        } catch (SQLException ex) {
            db.rollbackTransaction();
            response.setErrorCode(1000);
            response.setErrorMessage( ex.getMessage() );
            logger.error(ex.getMessage());
            db.CloseDB();
            db=null;
        } finally {
            logger.exit(response);
            return response;
        }
    }

    public com.filipovic.ws.soap.management.contact.entity.ReadContactResponse readContact(com.filipovic.ws.soap.management.contact.entity.ReadContactRequest readContactRequest) {
        logger.entry(readContactRequest);
        ReadContactResponse response = new ReadContactResponse();
        DataBase db = null;
        
        try {
            GetPropertyValue config = new GetPropertyValue();
            // database object
            db = new DataBase( config.getPropValues("database.url"), config.getPropValues("database.username"), config.getPropValues("database.password"));
            // check if request is not empty
            if( readContactRequest==null ){
                Response errorResponse = new Response();
                errorResponse.setErrorCode(3);
                errorResponse.setErrorMessage("Bad Request. Request is empty.");
                response.setError(errorResponse);
                db=null;
                logger.warn("Bad Request. Request is empty.");
            }
            // get contact data
            else if( readContactRequest.getContactId() > 0 ){
                // get contact from db
                String queryString = "SELECT * FROM contacts WHERE contact_id=" + readContactRequest.getContactId() + " LIMIT 1";
                ResultSet resultSet = db.execQuery(queryString);
                resultSet.next();
                Contact contact = new Contact();
                contact.setContactId(resultSet.getInt("contact_id"));
                contact.setUserId(resultSet.getInt("user_id"));
                contact.setFirstName(resultSet.getString("first_name"));
                contact.setLastName(resultSet.getString("last_name"));
                contact.setAddress(resultSet.getString("address"));
                contact.setCity(resultSet.getString("city"));
                contact.setDescription(resultSet.getString("description"));
                contact.setPicturePath(resultSet.getString("picture_path"));
                // get contact nubers
                queryString = "SELECT * FROM contact_items WHERE contact_id='" + resultSet.getInt("contact_id") + "'";
                resultSet = db.execQuery(queryString);
                if( resultSet.isBeforeFirst() ){
                    while( resultSet.next() ){
                        ContactItems contactItem= new ContactItems();
                        contactItem.setItemId(resultSet.getInt("item_id"));
                        contactItem.setItemValue(resultSet.getString("item_value"));
                        contactItem.setType(resultSet.getString("type"));
                        contactItem.setDescription(resultSet.getString("description"));
                        contact.getContactItems().add(contactItem);
                    }
                }
                response.getContacts().add(contact);
                resultSet.close();
                resultSet=null;
                db.CloseDB();
                db=null;
            // get data for datagrid
            } else if( readContactRequest.getUserId() > 0 && readContactRequest.getPageNuber() > 0 && readContactRequest.getRecordsPerPage() > 0 ) {
                logger.debug("enter datagrid read mode");
                int startFrom = (readContactRequest.getPageNuber() - 1) * readContactRequest.getRecordsPerPage();
                String queryString = "SELECT DISTINCT\n" +
                    "contacts.contact_id AS contact_id,\n" +
                    "contacts.first_name AS first_name,\n" +
                    "contacts.last_name AS last_name,\n" +
                    "contacts.city AS city,\n" +
                    "contacts.address AS address,\n" +
                    "contacts.description AS description,\n" +
                    "contacts.picture_path AS picture_path,\n" +
                    "(SELECT GROUP_CONCAT(item_value SEPARATOR \"; \") From contact_items where contacts.contact_id = contact_items.contact_id ) AS item_values_string\n" +
                    "FROM contacts\n" +
                    "INNER JOIN contact_items \n" +
                    "ON contacts.contact_id=contact_items.contact_id \n" +
                    "WHERE user_id='" + readContactRequest.getUserId() + "' " +
                    "LIMIT " + startFrom + "," + readContactRequest.getRecordsPerPage();
                logger.trace("Query string: " + queryString);
                ResultSet resultSet = db.execQuery(queryString);
                if( resultSet.isBeforeFirst() ){
                    logger.debug("There is contacts in DB. Start iteration over result set.");
                    while( resultSet.next() ){
                        logger.trace("contact ID: " + resultSet.getInt("contact_id"));
                        Contact contact = new Contact();
                        contact.setContactId(resultSet.getInt("contact_id"));
                        contact.setUserId(readContactRequest.getUserId());
                        contact.setFirstName(resultSet.getString("first_name"));
                        contact.setLastName(resultSet.getString("last_name"));
                        contact.setCity(resultSet.getString("city"));
                        contact.setAddress(resultSet.getString("address"));
                        contact.setDescription(resultSet.getString("description"));
                        contact.setPicturePath(resultSet.getString("picture_path"));
                        contact.setContactItemsAsString(resultSet.getString("item_values_string"));
                        response.getContacts().add(contact);
                    }
                }
                resultSet.close();
                resultSet=null;
                db.CloseDB();
                db=null;
            // bad reqest
            } else {
                Response errorResponse = new Response();
                errorResponse.setErrorCode(3);
                errorResponse.setErrorMessage("Invalid request; User ID or paging data are not set.");
                response.setError(errorResponse);
                logger.warn("Invalid request; User ID or paging data are not set.");
                db=null;
            }
            
        } catch (IOException ex) {
            Response errorResponse = new Response();
            errorResponse.setErrorCode(1001);
            errorResponse.setErrorMessage(ex.getMessage());
            response.setError(errorResponse);
            logger.error(ex.getMessage());
            db.CloseDB();
            db=null;
        } catch (SQLException ex) {
            Response errorResponse = new Response();
            errorResponse.setErrorCode(1001);
            errorResponse.setErrorMessage(ex.getMessage());
            response.setError(errorResponse);
            logger.error(ex.getMessage());
            db.CloseDB();
            db=null;
        } finally {
            logger.exit(response);
            return response;
        }
    }

    public com.filipovic.ws.soap.management.contact.entity.EditContactResponse editContact(com.filipovic.ws.soap.management.contact.entity.EditContactRequest editContactRequest) {
        logger.entry(editContactRequest);
        EditContactResponse response = new EditContactResponse();
        DataBase db = null;
        try {
            GetPropertyValue config = new GetPropertyValue();
            // database object
            db = new DataBase( config.getPropValues("database.url"), config.getPropValues("database.username"), config.getPropValues("database.password"));
            // check if request is empty 
            if(editContactRequest==null){
                response.setErrorCode(3);
                response.setErrorMessage("Bad request. Request is empty.");
                db.CloseDB();
                db=null;
                logger.warn("Bad request. Request is empty.");
            }
            // check if there is contact id
            else if(editContactRequest.getContactId() > 0 && editContactRequest.getFirstName() != null ){
                db.startTransaction();
                // update contact inforamtion
                String querString = "UPDATE contacts SET "
                        + "first_name='" + editContactRequest.getFirstName() + "', "
                        + "last_name='" + editContactRequest.getLastName() + "', "
                        + "city='" + editContactRequest.getCity() + "', "
                        + "address='" + editContactRequest.getAddress() + "', "
                        + "description='" + editContactRequest.getDescription() + "' "
                        + "WHERE contact_id=" + editContactRequest.getContactId();
                logger.trace("Query string: " + querString);
                db.execIUDQuery(querString);
                // delete contact items saved in db
                logger.debug("Delete all contac items for given contact_id.");
                querString = "DELETE FROM contact_items WHERE contact_id=" + editContactRequest.getContactId();
                logger.trace("Query string: " + querString);
                db.execIUDQuery(querString);
                // insert new contat items to db
                if( editContactRequest.getContactItems().size() > 0 ){
                    logger.debug("There is contact items in update request to be saved in DB. Start iteration.");
                    List contactItemList = editContactRequest.getContactItems();
                    for( int i=0; i<contactItemList.size(); i++){
                        ContactItem contactItem = (ContactItem) contactItemList.get(i);
                        querString = "INSERT INTO contact_items ( contact_id, type, item_value, description) VALUES ("
                        + "'" + editContactRequest.getContactId() + "' ,"
                        + "'" + contactItem.getType() + "' ,"
                        + "'" + contactItem.getItemValue() + "' ,"
                        + "'" + contactItem.getDescription() + "'"
                        + ")";
                        logger.trace("Query string: " + querString);
                        db.execIUDQuery(querString);
                    }
                }
                response.setErrorCode(0);
                db.commitTransaction();
                db.CloseDB();
                db=null;
            } else {
                response.setErrorCode(3);
                response.setErrorMessage("Ivalid request. Ther is no contact_id in request.");
                logger.warn("Ivalid request. Ther is no contact_id in request.");
                db.CloseDB();
                db=null;
            }
        } catch (IOException e) {
            response.setErrorCode(1001);
            response.setErrorMessage(e.getMessage());
            logger.error(e.getMessage());
            db = null;
        } catch (SQLException e) {
            response.setErrorCode(1000);
            response.setErrorMessage(e.getMessage());
            logger.error(e.getMessage());            
            db.rollbackTransaction();
            db.CloseDB();
            db=null;
        }finally {
            logger.exit(response);
            return response;
        }
    }

    public com.filipovic.ws.soap.management.contact.entity.DeleteContactResponse deleteContact(com.filipovic.ws.soap.management.contact.entity.DeleteContactRequest deleteContactRequest) {
        logger.entry(deleteContactRequest);
        DeleteContactResponse response = new DeleteContactResponse();
        DataBase db = null;
        try {
            GetPropertyValue config = new GetPropertyValue();
            // database object
            db = new DataBase( config.getPropValues("database.url"), config.getPropValues("database.username"), config.getPropValues("database.password"));
            // check if request is null
            if(deleteContactRequest==null || deleteContactRequest.getContactId()==null){
                response.setErrorCode(3);
                response.setErrorMessage("Bad request. Request is empty");
                db.CloseDB();
                db=null;
                logger.warn("Bad request. Request is empty");
            }
            // delete contact
            else if( !deleteContactRequest.getContactId().isEmpty() ){
                String queryString ="DELETE FROM contacts WHERE contact_id='" + deleteContactRequest.getContactId() + "'";
                logger.trace("Query string: " + queryString);
                db.execIUDQuery(queryString);
                response.setErrorCode(0);
                db.CloseDB();
                db=null;
            } else {
                response.setErrorCode(3);
                response.setErrorMessage("Bad request. Missing Contact ID.");
                logger.warn("Bad request. Missing Contact ID.");
                db.CloseDB();
                db=null;
            }
        } catch (IOException e) {
            response.setErrorCode(1001);
            response.setErrorMessage( e.getMessage() );
            logger.error(e.getMessage());
            db=null;
        } catch (SQLException e) {
            response.setErrorCode(1000);
            response.setErrorMessage(e.getMessage());
            logger.error(e.getMessage());
            db.CloseDB();
            db=null;
        } finally {
            logger.exit(response);
            return response;
        }
    }

    public com.filipovic.ws.soap.management.contact.entity.GetItemTypesResponse getItemTypes(java.lang.Object getItemTypesRequest) {
        logger.entry(getItemTypesRequest);
        GetItemTypesResponse response = new GetItemTypesResponse();
        DataBase db = null;
        
        try {
            GetPropertyValue config = new GetPropertyValue();
            // database object
            db = new DataBase( config.getPropValues("database.url"), config.getPropValues("database.username"), config.getPropValues("database.password"));
            
            String queryString = "SELECT * FROM item_types";
            ResultSet resultSet = db.execQuery(queryString);
            while( resultSet.next() ){
                response.getItemType().add(resultSet.getString("item_type"));
            }
        } catch (IOException e) {
            Response errorResponse = new Response();
            errorResponse.setErrorCode(1001);
            errorResponse.setErrorMessage(e.getMessage());
            db=null;
            logger.error(e.getMessage());
        } catch (SQLException e) {
            Response errorResponse = new Response();
            errorResponse.setErrorCode(1000);
            errorResponse.setErrorMessage(e.getMessage());
            db.CloseDB();
            db=null;
            logger.error(e.getMessage());
        } finally {
            logger.exit(response);
            return response;
        }
    
    }
    
}
