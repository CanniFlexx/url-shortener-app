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

    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}
