package com.userapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.userapp.entity.UserMaster;

public interface UserMasterRepo extends JpaRepository<UserMaster, Integer> {
    UserMaster findByEmailAndPassword(String email, String pwd);
    UserMaster findByEmail(String email);
}
