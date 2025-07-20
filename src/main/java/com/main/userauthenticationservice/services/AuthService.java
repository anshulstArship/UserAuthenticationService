package com.main.userauthenticationservice.services;

import com.main.userauthenticationservice.dtos.LoginRequestDto;
import com.main.userauthenticationservice.dtos.UserDto;
import com.main.userauthenticationservice.models.Session;
import com.main.userauthenticationservice.models.SessionStatus;
import com.main.userauthenticationservice.models.User;
import com.main.userauthenticationservice.repositories.SessionRepository;
import com.main.userauthenticationservice.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    public AuthService(UserRepository userRepository,SessionRepository sessionRepository,BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository=userRepository;
        this.sessionRepository=sessionRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }
    public ResponseEntity<UserDto> login(LoginRequestDto request){
        Optional<User> userDetails =userRepository.findByEmail(request.getEmail());
        if(userDetails.isEmpty()){
            return null;
        }
        User user = userDetails.get();
        if(!bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Wrong Password");
        }
       // String token = RandomStringUtils.randomAlphanumeric(30);

        SecretKey key = Jwts.SIG.HS256.key().build();

//        String message = "{\n" +
//                "  \"email\": \"abc\",\n" +
//               "  \"name\": \"John Doe\",\n"+
//               "}";
//        byte[] content = message.getBytes(StandardCharsets.UTF_8);
//
//        String token = Jwts.builder().signWith(testKey)
//                .content(content)
//                .compact();

        Map<String, Object> jsonForJwt = new HashMap<>();
        jsonForJwt.put("name",user.getEmail());
        jsonForJwt.put("roles",user.getRoles());
        jsonForJwt.put("expiryAt", LocalDate.now().plusDays(3).toEpochDay());
        String token = Jwts.builder().claims(jsonForJwt).signWith(key, SignatureAlgorithm.HS256).compact();

        Session session = new Session();
        session.setId(user.getId());
        session.setUser(user);
        session.setStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        sessionRepository.save(session);
        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE,"auth-token:"+token);
        UserDto userDto = new UserDto();
        userDto.setEmail(request.getEmail());

        return new ResponseEntity<>(userDto,headers, HttpStatus.OK);
    }
    public UserDto signUp(String email, String password){
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRoles(new HashSet<>());
        userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        return userDto;
    }
    public SessionStatus validate(Long userId,String token){
        Optional<Session> session=sessionRepository.findByTokenAndUser_Id(token,userId);
        if(session.isEmpty()){
            return SessionStatus.ENDED;
        }
        if(session.get().getStatus().equals(SessionStatus.ENDED)){
            return SessionStatus.ENDED;
        }
        Jws<Claims> claims = Jwts.parser().build().parseSignedClaims(token);
        Date createdAt = (Date) claims.getPayload().get("expiryAt");
        if(createdAt.before(new Date())){
            return SessionStatus.ENDED;
        }
        return SessionStatus.ACTIVE;

    }
}
