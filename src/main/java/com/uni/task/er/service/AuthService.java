package com.uni.task.er.service;

import com.uni.task.er.dto.request.AuthLoginRequest;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.exception.custom.UnauthorizedException;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.UserRepository;
import com.uni.task.er.utils.JwtUtils;
import com.uni.task.er.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtBlocklistService jwtBlocklistService;

    @Autowired
    public AuthService(UserRepository userRepository, JwtBlocklistService jwtBlocklistService) {
        this.userRepository = userRepository;
        this.jwtBlocklistService = jwtBlocklistService;
    }

    public String login(AuthLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new NotFoundException("User not found"));
        boolean validPassword = PasswordUtils.checkPassword(request.getPassword(), user.getPassword());
        if(!validPassword) throw new UnauthorizedException("Invalid email or password");

        return JwtUtils.generateToken(user.getEmail());
    }

    public void logout(String bearerToken) {
        String token = bearerToken.split("Bearer ")[1].trim();
        jwtBlocklistService.blocklist(token);
    }

    public User validateToken(String token) {
        String email = JwtUtils.validateTokenAndGetUsername(token);

        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User getUserInfo() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return (User) attrs.getAttribute("user", RequestAttributes.SCOPE_REQUEST);
        }
        return null;
    }
}
