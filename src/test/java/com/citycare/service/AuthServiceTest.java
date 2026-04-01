package com.citycare.service;

import com.citycare.dto.request.LoginRequest;
import com.citycare.dto.request.RegisterRequest;
import com.citycare.dto.response.AuthResponse;
import com.citycare.entity.Citizen;
import com.citycare.entity.User;
import com.citycare.exception.BadRequestException;
import com.citycare.repository.CitizenRepository;
import com.citycare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthServiceTest – Unit tests for AuthService
 * Tests registration and login functionality with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CitizenRepository citizenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private Citizen testCitizen;

    @BeforeEach
    void setUp() {
        // Setup test data for registration
        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("SecurePass123");
        registerRequest.setPhone("9876543210");

        // Setup test data for login
        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("SecurePass123");

        // Setup test user entity
        testUser = User.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("hashedPassword123")
                .role(User.Role.CITIZEN)
                .status(User.Status.ACTIVE)
                .phone("9876543210")
                .build();

        // Setup test citizen entity
        testCitizen = Citizen.builder()
                .citizenId(1L)
                .name("John Doe")
                .contactInfo("9876543210")
                .user(testUser)
                .status(Citizen.Status.ACTIVE)
                .build();
    }

    // ============================================================
    // REGISTRATION TESTS
    // ============================================================

    @Test
    @DisplayName("Should successfully register a new user")
    @Commit
    void testRegisterSuccess() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response, "AuthResponse should not be null");
        assertEquals("John Doe", response.getName(), "Name should match");
        assertEquals("john@example.com", response.getEmail(), "Email should match");
        assertEquals(User.Role.CITIZEN, response.getRole(), "Role should be CITIZEN");
        assertEquals(1L, response.getUserId(), "User ID should match");

        // Verify interactions
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(passwordEncoder, times(1)).encode("SecurePass123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(citizenRepository, times(1)).save(any(Citizen.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when email already exists")
    void testRegisterWithDuplicateEmail() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.register(registerRequest),
                "Should throw BadRequestException for duplicate email"
        );

        assertTrue(
                exception.getMessage().contains("Email already registered"),
                "Exception message should mention email already registered"
        );

        // Verify that save was never called
        verify(userRepository, never()).save(any(User.class));
        verify(citizenRepository, never()).save(any(Citizen.class));
    }

    @Test
    @DisplayName("Should encode password before saving user")
    void testRegisterPasswordEncoding() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("hashedPassword123", savedUser.getPassword(), "Password should be encoded");
        assertEquals("SecurePass123", registerRequest.getPassword(), "Original request password should remain unchanged");
    }

    @Test
    @DisplayName("Should set default role as CITIZEN during registration")
    void testRegisterDefaultRoleAsCitizen() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(User.Role.CITIZEN, savedUser.getRole(), "Default role should be CITIZEN");
    }

    @Test
    @DisplayName("Should create Citizen record linked to User during registration")
    void testRegisterCreatesLinkedCitizen() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        ArgumentCaptor<Citizen> citizenCaptor = ArgumentCaptor.forClass(Citizen.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(citizenRepository).save(citizenCaptor.capture());
        Citizen savedCitizen = citizenCaptor.getValue();
        assertEquals("John Doe", savedCitizen.getName(), "Citizen name should match user name");
        assertEquals("9876543210", savedCitizen.getContactInfo(), "Citizen contact should match phone");
        assertEquals(testUser, savedCitizen.getUser(), "Citizen should be linked to user");
        assertEquals(Citizen.Status.ACTIVE, savedCitizen.getStatus(), "Citizen status should be ACTIVE");
    }

    @Test
    @DisplayName("Should set status to ACTIVE during registration")
    void testRegisterSetStatusActive() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(User.Status.ACTIVE, savedUser.getStatus(), "Status should be ACTIVE");
    }

    // ============================================================
    // LOGIN TESTS
    // ============================================================

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void testLoginSuccess() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testUser));

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response, "AuthResponse should not be null");
        assertEquals("John Doe", response.getName(), "Name should match");
        assertEquals("john@example.com", response.getEmail(), "Email should match");
        assertEquals(User.Role.CITIZEN, response.getRole(), "Role should match");
        assertEquals(1L, response.getUserId(), "User ID should match");

        // Verify interactions
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found during login")
    void testLoginUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest),
                "Should throw RuntimeException when user not found"
        );

        assertEquals("User not found", exception.getMessage(), "Exception message should be 'User not found'");

        // Verify interactions
        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    @DisplayName("Should return correct user details on login")
    void testLoginReturnsCorrectUserDetails() {
        // Arrange
        User doctorUser = User.builder()
                .userId(2L)
                .name("Dr. Jane Smith")
                .email("jane@hospital.com")
                .password("hashedPassword456")
                .role(User.Role.DOCTOR)
                .status(User.Status.ACTIVE)
                .phone("9876543211")
                .build();

        LoginRequest doctorLogin = new LoginRequest();
        doctorLogin.setEmail("jane@hospital.com");
        doctorLogin.setPassword("SecurePass456");

        when(userRepository.findByEmail("jane@hospital.com"))
                .thenReturn(Optional.of(doctorUser));

        // Act
        AuthResponse response = authService.login(doctorLogin);

        // Assert
        assertEquals(2L, response.getUserId(), "User ID should be 2");
        assertEquals("Dr. Jane Smith", response.getName(), "Name should be Dr. Jane Smith");
        assertEquals("jane@hospital.com", response.getEmail(), "Email should be jane@hospital.com");
        assertEquals(User.Role.DOCTOR, response.getRole(), "Role should be DOCTOR");
    }

    // ============================================================
    // EDGE CASE AND INTEGRATION TESTS
    // ============================================================

    @Test
    @DisplayName("Should handle special characters in email")
    void testRegisterWithSpecialCharactersInEmail() {
        // Arrange
        registerRequest.setEmail("john+test@example.co.uk");
        when(userRepository.existsByEmail("john+test@example.co.uk")).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response, "Should handle special characters in email");
        verify(userRepository).existsByEmail("john+test@example.co.uk");
    }

    @Test
    @DisplayName("Should preserve phone number during registration")
    void testRegisterPreservesPhoneNumber() {
        // Arrange
        String testPhone = "9999888877";
        registerRequest.setPhone(testPhone);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(testPhone, savedUser.getPhone(), "Phone number should be preserved");
    }

    @Test
    @DisplayName("Should handle login for different user roles")
    void testLoginForMultipleRoles() {
        // Test for NURSE role
        User nurseUser = User.builder()
                .userId(3L)
                .name("Nurse Alice")
                .email("alice@clinic.com")
                .password("hashedPassword789")
                .role(User.Role.NURSE)
                .status(User.Status.ACTIVE)
                .build();

        LoginRequest nurseLogin = new LoginRequest();
        nurseLogin.setEmail("alice@clinic.com");
        nurseLogin.setPassword("Pass789");

        when(userRepository.findByEmail("alice@clinic.com"))
                .thenReturn(Optional.of(nurseUser));

        // Act
        AuthResponse response = authService.login(nurseLogin);

        // Assert
        assertEquals(User.Role.NURSE, response.getRole(), "Role should be NURSE");
    }

    @Test
    @DisplayName("Should verify repository calls are transactional during registration")
    void testRegisterTransactionality() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(citizenRepository.save(any(Citizen.class))).thenReturn(testCitizen);

        // Act
        authService.register(registerRequest);

        // Assert - verify both repositories were called in correct order
        InOrder inOrder = inOrder(userRepository, citizenRepository);
        inOrder.verify(userRepository).existsByEmail(anyString());
        inOrder.verify(userRepository).save(any(User.class));
        inOrder.verify(citizenRepository).save(any(Citizen.class));
    }
}