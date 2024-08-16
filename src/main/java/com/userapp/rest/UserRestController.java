package com.userapp.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.userapp.bindings.ActivateAccount;
import com.userapp.bindings.Login;
import com.userapp.bindings.User;
import com.userapp.service.UserMgmtService;

@RestController
@RequestMapping("/api")
public class UserRestController {

    @Autowired
    private UserMgmtService service;

    @PostMapping("/user")
    public ResponseEntity<String> userReg(@RequestBody User user) {
        boolean saveUser = service.saveUser(user);
        return saveUser ? 
                new ResponseEntity<>("Registration Success", HttpStatus.CREATED) : 
                new ResponseEntity<>("Registration Failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestBody ActivateAccount acc) {
        boolean activateUserAcc = service.activateUserAcc(acc);
        return activateUserAcc ? 
                new ResponseEntity<>("Account activated", HttpStatus.OK) : 
                new ResponseEntity<>("Invalid Temporary Pwd", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> allUsers = service.getAllUser();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
        User user = service.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Integer userId) {
        boolean isDeleted = service.deleteUserById(userId);
        return isDeleted ? 
                new ResponseEntity<>("Deleted", HttpStatus.OK) : 
                new ResponseEntity<>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/status/{userId}/{status}")
    public ResponseEntity<String> statusChange(@PathVariable Integer userId, @PathVariable String status) {
        boolean isChanged = service.changeAccountStatus(userId, status);
        return isChanged ? 
                new ResponseEntity<>("Status changed", HttpStatus.OK) : 
                new ResponseEntity<>("Failed to change", HttpStatus.INTERNAL_SERVER_ERROR);
    }
   
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login) {
        String status = service.login(login);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPwd(@RequestParam String email) {
        String status = service.forgotPwd(email);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
