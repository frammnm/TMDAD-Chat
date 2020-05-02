package com.example.demochat.repository;

import com.example.demochat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    User getUserByUsername(String username);

}