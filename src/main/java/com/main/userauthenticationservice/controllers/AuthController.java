package com.main.userauthenticationservice.controllers;

import com.main.userauthenticationservice.dtos.LoginRequestDto;
import com.main.userauthenticationservice.dtos.SignUpRequestDto;
import com.main.userauthenticationservice.dtos.UserDto;
import com.main.userauthenticationservice.dtos.ValidateRequestDto;
import com.main.userauthenticationservice.models.SessionStatus;
import com.main.userauthenticationservice.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
     AuthController(AuthService service){
         this.authService=service;
     }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request){
         return authService.login(request);

    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LoginRequestDto request){
        return null;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignUpRequestDto request){
        UserDto userDto=authService.signUp(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(userDto,HttpStatus.OK);
    }
    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody ValidateRequestDto request){
         SessionStatus status = authService.validate(request.getUserId(),request.getToken());
         return new ResponseEntity<>(status,HttpStatus.OK);
    }


}
