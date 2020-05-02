// package com.example.demochat.service;
//
//
// import com.example.demochat.repository.MessageRepository;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
//
// @Service
// public class UserDetailsService implements UserDetailsService {
//
//     @Autowired
//     private UserRepository userRepository;
//
//     @Override
//     public UserDetails loadUserByUsername(String username) {
//         User user = userRepository.findByUsername(username);
//         if (user == null) {
//             throw new UsernameNotFoundException(username);
//         }
//         return new MyUserPrincipal(user);
//     }
// }