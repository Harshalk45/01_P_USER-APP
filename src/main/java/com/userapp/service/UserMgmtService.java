package com.userapp.service;

import java.util.List;

import com.userapp.bindings.ActivateAccount;
import com.userapp.bindings.Login;
import com.userapp.bindings.User;

public interface UserMgmtService {
    
	public boolean saveUser(User user);
	
	public boolean activateUserAcc(ActivateAccount activateAccount);
	
	public List<User> getAllUser();
	
	public User getUserById(Integer userId);
	
	public User getUserByEmail(String email); 
	
	public boolean deleteUserById(Integer userId);
	
	public boolean changeAccountStatus(Integer userId, String accStatus);
	
	public String login(Login login);
	
	public String forgotPwd(String email);
	
}
