//
//package com.cozentus.pms.serviceImpl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.when;
//
//import java.util.Collections;
//import java.util.Date;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//@SpringBootTest
//public class JwtServiceImplTest {
//
//    @MockBean
//    private JwtServiceImpl jwtService;
//
//    @Mock
//    private UserDetails userDetails;
//
//    private String secretKey = "secretKey";
//    private long jwtExpiration = 10000;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        jwtService = new JwtServiceImpl();
//    }
//
//    @Test
//    public void testGenerateToken() {
//        String username = "testUser";
//        when(userDetails.getUsername()).thenReturn(username);
//        when(userDetails.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//
//        String token = jwtService.generateToken(userDetails);
//        Claims claims = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).parseClaimsJws(token).getBody();
//
//        assertEquals(username, claims.getSubject());
//        assertTrue(claims.getExpiration().after(new Date()));
//    }
//
//    @Test
//    public void testIsTokenValid() {
//        String username = "testUser";
//        when(userDetails.getUsername()).thenReturn(username);
//        when(userDetails.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//
//        String token = jwtService.generateToken(userDetails);
//        assertTrue(jwtService.isTokenValid(token, userDetails));
//
//        when(userDetails.getUsername()).thenReturn("otherUser");
//        assertFalse(jwtService.isTokenValid(token, userDetails));
//    }
//
//    @Test
//    public void testExtractUsername() {
//        String username = "testUser";
//        when(userDetails.getUsername()).thenReturn(username);
//        when(userDetails.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//
//        String token = jwtService.generateToken(userDetails);
//        assertEquals(username, jwtService.extractUsername(token));
//    }
//
//    @Test
//    public void testExtractClaim() {
//        String username = "testUser";
//        when(userDetails.getUsername()).thenReturn(username);
//        when(userDetails.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//
//        String token = jwtService.generateToken(userDetails);
//        Claims claims = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).parseClaimsJws(token).getBody();
//
//        assertEquals(username, jwtService.extractClaim(token, Claims::getSubject));
//        assertEquals(claims.getExpiration(), jwtService.extractClaim(token, Claims::getExpiration));
//    }
//}
