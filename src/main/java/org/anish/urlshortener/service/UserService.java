package org.anish.urlshortener.service;

import lombok.AllArgsConstructor;
import org.anish.urlshortener.dtos.LoginRequest;
import org.anish.urlshortener.models.User;
import org.anish.urlshortener.security.jwt.JwtUtils;

import org.anish.urlshortener.repository.UserRepository;
import org.anish.urlshortener.security.jwt.JwtAuthenticationResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
      private PasswordEncoder passwordEncoder;
      private UserRepository userRepository;
        private AuthenticationManager authenticationManager;
        private JwtUtils jwtUtils;

      public User registerUser(User user){
          user.setPassword(passwordEncoder.encode(user.getPassword()));
          return userRepository.save(user);
      }
      //The registerUser method takes a User object as input, encodes the password using the
      // passwordEncoder bean, and saves the user to the database using the userRepository bean.

    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication); // Set the authentication object in the SecurityContextHolder
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); // Get the UserDetailsImpl object from the authentication object
        String jwt = jwtUtils.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
    //The authenticateUser method takes a LoginRequest object as input, authenticates the user using the
    // authenticationManager bean, and generates a JWT token using the jwtUtils bean.

    public User findByUsername(String name){
          return userRepository.findByUsername(name).orElseThrow(
                () -> new UsernameNotFoundException("User not found with usergame: "+ name));
      }
    // The findByUsername method takes a username as input and returns the User object from the database using the userRepository bean.
}
