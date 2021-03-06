package com.JavaAPI_SpringBoot.Home.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.JavaAPI_SpringBoot.Home.model.account;
import com.JavaAPI_SpringBoot.Home.model.response;
import com.JavaAPI_SpringBoot.Home.repository.accountRepository;
import com.JavaAPI_SpringBoot.Home.security.EncryptionDecryption;

/**
 * Used by {@link ExceptionTranslationFilter} to commence an authentication scheme.
 *
 * @author Tushar Malakar
 */



@RestController
public class userController {
	
//	MongoClientURI uri = new MongoClientURI( "mongodbites=true&w=majority");
//	MongoClient mongoClient = new MongoClient(uri);
//	MongoDatabase database = mongoClient.getDatabase("users");
	
	
	
	
	@Autowired
	private accountRepository accountRepo;
	
	//passing reponse on successful or faluire 
	private response Response = new response(false, null);
	EncryptionDecryption EncryptionDecryptionInstance = null;
	
	//default response
	@RequestMapping(method = RequestMethod.GET, value="")
	public String welcomePage() {
		/**
		 * If you run on loacalhost:8080
		 * http://localhost:8080
		 * */
		//System.out.println(accountRepo.findAll().size());
		return "Welome to Java Microservices";
	}
	
	
	
	@RequestMapping(method = RequestMethod.GET, value ="/login")
	public response login_endpoint(@RequestParam String username, @RequestParam String password) { 
		
		/**
		 * If you run on loacalhost:8080
		 * http://localhost:8080/login?username=testuser1&password=password
	     */
		try {
		
				if(accountRepo.findByUsername(username).getUsername().equals(username) 
				&& EncryptionDecryptionInstance.decrypt(accountRepo.findByUsername(username).getPassword()).equals(password)) {
					Response = new response(true, "You successfully logged in!" );
				}
				
				else {		
				if(!EncryptionDecryptionInstance.decrypt(accountRepo.findByUsername(username).getPassword()).equals(password)) {
					Response = new response(false,"password didn't match!");
					}
				}
				return Response;
		
		}
		catch (Exception e) {
			return Response = new response(false,"Exception found");
		}
	}
			
	
	
	@RequestMapping(method = RequestMethod.POST, value= "/createAccount")
	public response create_account(@RequestBody account accountBody) {
		/**
	    http://localhost:8080/createAccount 
		accountBody =>{
				  	"username":"testuser1",
			     	"password":"password"
		}
		return/response => {
			    "success": true,
			    "token": "A new account has been created"
		}
		 * */
		try {
		
				String username = accountBody.getUsername();
				String password = accountBody.getPassword();
				if(username==null || password==null || username== "" || password=="") 
				{Response = new response(false, "username or password not provided"); return Response;}
				account accountEntity = accountRepo.findByUsername(username);
				
				if(accountEntity == null) {
					String encrytedPass = EncryptionDecryptionInstance.encrypt(password);
					account Account = new account(username, encrytedPass);
					accountRepo.save(Account);
					Response = new response(true, "A new account has been created");
				}
				else {Response = new response(false, "User already exist");}
			
				return Response;
		}
		catch (Exception e) {
			return Response = new response(false,"Exception found");
		}
	
	}
	

	@RequestMapping(method = RequestMethod.PUT, value = "/updatePassword")
	public response update_user(@RequestBody account accountBody) {
		/**
		 * http://localhost:8080/update_password 
		   accountBody =>{
				   "username":"testuser1",
				   "password":"new_password"
		   }
		   return/response => {
					"success": true,
			    	"token": "password has been successfully updated"
		   }
		 * */
		try {
				String username = accountBody.getUsername();
				String password = accountBody.getPassword();
				if(username==null || password==null || username== "" || password=="") 
				{Response = new response(false, "username or password not provided"); return Response;}
				
				account accountEntity = accountRepo.findByUsername(username);
				if(accountEntity!=null) {
					accountEntity.setPassword(EncryptionDecryptionInstance.encrypt(password));
					accountEntity.setUsername(username);
					accountRepo.save(accountEntity);
					Response = new response(true, "user account updated successfully");
				}
				else {Response = new response(false, "User does not exist");}
				
				return Response; 
		}
		catch (Exception e) {
			return Response = new response(false,"Exception found");
		}
	}
	
	

	@RequestMapping(method = RequestMethod.DELETE, value = "/deleteUser")
	public response delete_user(@RequestParam String username) { 
		
		try {
			account accountEntity = accountRepo.findByUsername(username);
			if(accountEntity == null) {return Response = new response(false,"No user found"); }
			else {accountRepo.delete(accountEntity);return Response = new response(true,"Successfully deleted");}
		}
		catch (Exception e) {
			return Response = new response(false,"Exception found");
		}

	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/deleteAllUsers")
	public response delete_all_users() { 
		try {
			accountRepo.deleteAll();
			return Response = new response(true,"Successfully deleted all users");}
		catch (Exception e) {
			return Response = new response(false,"Exception found");
		}
			
			
			
	}
	
}