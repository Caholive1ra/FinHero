package com.finhero.finhero.service;

import com.finhero.finhero.dto.DuplaResponse;
import com.finhero.finhero.exception.*;
import com.finhero.finhero.model.Dupla;
import com.finhero.finhero.model.User;
import com.finhero.finhero.repository.DuplaRepository;
import com.finhero.finhero.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DuplaServiceTest {

    @Mock
    private DuplaRepository duplaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DuplaService duplaService;

    private User currentUser;
    private User partnerUser;
    private Dupla dupla;
    private static final String INVITE_CODE = "ABC12345";
    private static final Long CURRENT_USER_ID = 1L;
    private static final Long PARTNER_USER_ID = 2L;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setEmail("current@example.com");
        currentUser.setInviteCode("CURRENT1");

        partnerUser = new User();
        partnerUser.setId(PARTNER_USER_ID);
        partnerUser.setEmail("partner@example.com");
        partnerUser.setInviteCode(INVITE_CODE);

        dupla = new Dupla();
        dupla.setId(1L);
        dupla.setUserAId(CURRENT_USER_ID);
        dupla.setUserBId(PARTNER_USER_ID);
        dupla.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testLinkDupla_WithValidData_ShouldCreateDupla() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.of(partnerUser));
        when(duplaRepository.existsByUserId(CURRENT_USER_ID)).thenReturn(false);
        when(duplaRepository.existsByUserId(PARTNER_USER_ID)).thenReturn(false);
        when(duplaRepository.save(any(Dupla.class))).thenReturn(dupla);

        DuplaResponse response = duplaService.linkDupla(CURRENT_USER_ID, INVITE_CODE);

        assertNotNull(response);
        assertEquals(dupla.getId(), response.getId());
        verify(duplaRepository, times(1)).save(any(Dupla.class));
    }

    @Test
    void testLinkDupla_UserNotFound_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(
            UserNotFoundException.class,
            () -> duplaService.linkDupla(CURRENT_USER_ID, INVITE_CODE)
        );

        verify(duplaRepository, never()).save(any(Dupla.class));
    }

    @Test
    void testLinkDupla_InvalidInviteCode_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.empty());

        assertThrows(
            InvalidInviteCodeException.class,
            () -> duplaService.linkDupla(CURRENT_USER_ID, INVITE_CODE)
        );

        verify(duplaRepository, never()).save(any(Dupla.class));
    }

    @Test
    void testLinkDupla_SelfLink_ShouldThrowException() {
        partnerUser.setId(CURRENT_USER_ID);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.of(partnerUser));

        assertThrows(
            SelfLinkException.class,
            () -> duplaService.linkDupla(CURRENT_USER_ID, INVITE_CODE)
        );

        verify(duplaRepository, never()).save(any(Dupla.class));
    }

    @Test
    void testLinkDupla_CurrentUserAlreadyInDupla_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.of(partnerUser));
        when(duplaRepository.existsByUserId(CURRENT_USER_ID)).thenReturn(true);

        assertThrows(
            UserAlreadyInDuplaException.class,
            () -> duplaService.linkDupla(CURRENT_USER_ID, INVITE_CODE)
        );

        verify(duplaRepository, never()).save(any(Dupla.class));
    }

    @Test
    void testLinkDupla_PartnerAlreadyInDupla_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.of(partnerUser));
        when(duplaRepository.existsByUserId(CURRENT_USER_ID)).thenReturn(false);
        when(duplaRepository.existsByUserId(PARTNER_USER_ID)).thenReturn(true);

        assertThrows(
            UserAlreadyInDuplaException.class,
            () -> duplaService.linkDupla(CURRENT_USER_ID, INVITE_CODE)
        );

        verify(duplaRepository, never()).save(any(Dupla.class));
    }

    @Test
    void testGetDuplaByUserId_WhenExists_ShouldReturnDuplaResponse() {
        when(duplaRepository.findByUserId(anyLong())).thenReturn(Optional.of(dupla));

        DuplaResponse response = duplaService.getDuplaByUserId(CURRENT_USER_ID);

        assertNotNull(response);
        assertEquals(dupla.getId(), response.getId());
        assertEquals(dupla.getUserAId(), response.getUserAId());
        assertEquals(dupla.getUserBId(), response.getUserBId());
    }

    @Test
    void testGetDuplaByUserId_WhenNotExists_ShouldReturnNull() {
        when(duplaRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        DuplaResponse response = duplaService.getDuplaByUserId(CURRENT_USER_ID);

        assertNull(response);
    }

    @Test
    void testLinkDupla_ShouldOrderUserIds() {
        Long smallerId = 1L;
        Long largerId = 3L;
        partnerUser.setId(smallerId);
        currentUser.setId(largerId);

        Dupla orderedDupla = new Dupla();
        orderedDupla.setUserAId(smallerId);
        orderedDupla.setUserBId(largerId);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.of(partnerUser));
        when(duplaRepository.existsByUserId(anyLong())).thenReturn(false);
        when(duplaRepository.save(any(Dupla.class))).thenReturn(orderedDupla);

        DuplaResponse response = duplaService.linkDupla(largerId, INVITE_CODE);

        assertNotNull(response);
        assertTrue(response.getUserAId() < response.getUserBId());
    }

    @Test
    void testLinkDupla_ContainsUserMethod_ShouldWork() {
        assertTrue(dupla.containsUser(CURRENT_USER_ID));
        assertTrue(dupla.containsUser(PARTNER_USER_ID));
        assertFalse(dupla.containsUser(999L));
    }

    @Test
    void testLinkDupla_GetPartnerIdMethod_ShouldWork() {
        assertEquals(PARTNER_USER_ID, dupla.getPartnerId(CURRENT_USER_ID));
        assertEquals(CURRENT_USER_ID, dupla.getPartnerId(PARTNER_USER_ID));
        assertNull(dupla.getPartnerId(999L));
    }
}


