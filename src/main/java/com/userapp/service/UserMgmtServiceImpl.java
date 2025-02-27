package com.userapp.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.userapp.bindings.ActivateAccount;
import com.userapp.bindings.Login;
import com.userapp.bindings.User;
import com.userapp.entity.UserMaster;
import com.userapp.repo.UserMasterRepo;
import com.userapp.utils.EmailUtils;

@Service
public class UserMgmtServiceImpl implements UserMgmtService {
    
	
	private Logger logger = LoggerFactory.getLogger(UserMgmtServiceImpl.class);
	
	@Autowired
	private UserMasterRepo userMasterRepo;

	@Autowired
	private EmailUtils emailUtils;
	
	Random random = new Random();
	

	@Override
	public boolean saveUser(User user) {

		UserMaster entity = new UserMaster();
		BeanUtils.copyProperties(user, entity);

		entity.setPassword(generateRandomPwd());
		entity.setAccStatus("In-active");

		UserMaster save = userMasterRepo.save(entity);

		String subject = " Your Registration success";
		String filename = "REG-EMAIL-BODY.txt";
		String body = readEmailBody(entity.getFullname(), entity.getPassword(), filename);

		emailUtils.sendEmail(user.getEmail(), subject, body);

		return save.getUserId() != null;

	}

	@Override
	public boolean activateUserAcc(ActivateAccount activateAcc) {

		UserMaster entity = new UserMaster();
		entity.setEmail(activateAcc.getEmail());
		entity.setPassword(activateAcc.getTempPwd());

		Example<UserMaster> of = Example.of(entity);

		List<UserMaster> findAll = userMasterRepo.findAll(of);

		if (findAll.isEmpty()) {
			return false;
		} else {
			UserMaster userMaster = findAll.get(0);
			userMaster.setPassword(activateAcc.getNewPwd());
			userMaster.setAccStatus("Active");
			return true;
		}
	}

	@Override
	public List<User> getAllUser() {

		List<UserMaster> findAll = userMasterRepo.findAll();
		List<User> users = new ArrayList<>();
		for (UserMaster entity : findAll) {
			User user = new User();
			BeanUtils.copyProperties(entity, user);
			users.add(user);
		}
		return users;
	}

	@Override
	public User getUserById(Integer userId) {

		Optional<UserMaster> findById = userMasterRepo.findById(userId);
		if (findById.isPresent()) {
			User user = new User();
			UserMaster userMaster = findById.get();
			BeanUtils.copyProperties(userMaster, user);
			return user;
		}
		return null;
	}

	@Override
	public User getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteUserById(Integer userId) {
		try {
			userMasterRepo.deleteById(userId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String accStatus) {

		Optional<UserMaster> findById = userMasterRepo.findById(userId);
		if (findById.isPresent()) {
			UserMaster userMaster = findById.get();
			userMaster.setAccStatus(accStatus);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {

//		UserMaster entity = new UserMaster();
//
//		entity.setEmail(login.getEmail());
//		entity.setPassword(login.getPassword());
//
//		Example<UserMaster> of = Example.of(entity);
//		
//		List<UserMaster> findall = userMasterRepo.findAll(of);
		UserMaster entity = userMasterRepo.findByEmailAndPassword(login.getEmail(), login.getPassword());

		if (entity == null) {
			return "Invalid Credentials";
		}
		if (entity.getAccStatus().equals("Active")) {
			return "SUCCESS";
		} else {
			return "Account not activated";
		}
	}

	@Override
	public String forgotPwd(String email) {

		UserMaster entity = userMasterRepo.findByEmail(email);

		if (entity == null) {
			return "Invalid Email";
		}

		String subject = "Forgot Password";
		String filename = "RECOVER-MAIL-BODY.txt";
		String body = readEmailBody(entity.getFullname(), entity.getPassword(), filename);

		boolean sendEmail = emailUtils.sendEmail(email, subject, body);

		if (sendEmail) {
			return "Password send to your registered email";
		}
		return null;
	}

	private String generateRandomPwd() {

		String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;
		
		StringBuilder sb = new StringBuilder(); 
		Random random = new Random();
		int length = 10;
		for (int i = 0; i < length; i++) {
			int index = this.random.nextInt(alphaNumeric.length());
			char randomChar = alphaNumeric.charAt(index);
			sb.append(randomChar);
		}
		return sb.toString();

	}

	private String readEmailBody(String fullname, String pwd, String filename) {
		String url = "";
		String mailBody = null;
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			StringBuilder buffer = new StringBuilder();

			String line = br.readLine();
			while (line != null) {
				buffer.append(line);
				line = br.readLine();
			}
			mailBody = buffer.toString();
			mailBody = mailBody.replace("{FULLNAME}", fullname);
			mailBody = mailBody.replace("{TEMP-PWD}", pwd);
			mailBody = mailBody.replace("{URL}", url);
			mailBody = mailBody.replace("{PWD}", pwd);

		} catch (Exception e) {
			logger.error("Exception Occured", e);
		}
		return mailBody;
	}

}
