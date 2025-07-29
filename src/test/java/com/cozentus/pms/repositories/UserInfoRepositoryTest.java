//
//package com.cozentus.pms.repositories;
//
//import com.cozentus.pms.dto.*;
//import com.cozentus.pms.entites.UserInfo;
//import com.cozentus.pms.helpers.ApprovalStatus;
//import com.cozentus.pms.helpers.Roles;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.time.LocalDate;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//public class UserInfoRepositoryTest {
//
//    @MockBean
//    private UserInfoRepository userInfoRepository;
//
//    @Test
//    void testFindByUsernameAndEnabledTrue() {
//        String username = "testUser";
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUsername(username);
//        when(userInfoRepository.findByUsernameAndEnabledTrue(username)).thenReturn(Optional.of(userInfo));
//
//        Optional<UserInfo> result = userInfoRepository.findByUsernameAndEnabledTrue(username);
//
//        assertTrue(result.isPresent());
//        assertEquals(username, result.get().getUsername());
//    }
//
//    @Test
//    void testFindByUsernameAndEnabledTrue_NotFound() {
//        String username = "testUser";
//        when(userInfoRepository.findByUsernameAndEnabledTrue(username)).thenReturn(Optional.empty());
//
//        Optional<UserInfo> result = userInfoRepository.findByUsernameAndEnabledTrue(username);
//
//        assertFalse(result.isPresent());
//    }
//
//
//}
