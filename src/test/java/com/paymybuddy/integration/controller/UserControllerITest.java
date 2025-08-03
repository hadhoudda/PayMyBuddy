package com.paymybuddy.integration.controller;

import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class UserControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("GET /login - Affiche le formulaire de connexion")
    void testShowLoginForm() throws Exception {
        // Act
        mockMvc.perform(get("/paymybuddy/login"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("userLoginDto"));
    }

    @Test
    @DisplayName("GET /register - Affiche le formulaire d'inscription")
    void testShowRegisterForm() throws Exception {
        // Act
        mockMvc.perform(get("/paymybuddy/register"))
                // Assert
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegisterDto"));
    }

    @Test
    @DisplayName("POST /register - Inscription réussie")
    void testRegisterUserSuccess() throws Exception {
        // Arrange
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUserName("JohnDoe");
        dto.setEmail("john.doe@example.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        // Act
        mockMvc.perform(post("/paymybuddy/register")
                        .flashAttr("userRegisterDto", dto))
                // Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/register/confirmed"));

        // Assert (BDD)
        User saved = userRepository.findByEmail("john.doe@example.com").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getUserName()).isEqualTo("JohnDoe");
    }

    @Test
    @DisplayName("POST /register - Mots de passe différents")
    void testRegisterUserPasswordMismatch() throws Exception {
        // Arrange
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUserName("Alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("pass1");
        dto.setConfirmPassword("pass2");

        // Act
        mockMvc.perform(post("/paymybuddy/register")
                        .flashAttr("userRegisterDto", dto))
                // Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/register"))
                .andExpect(flash().attributeExists("userRegisterDto"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDto"));
    }

    @Test
    @DisplayName("POST /register - Email déjà utilisé")
    void testRegisterUserEmailAlreadyUsed() throws Exception {
        // Arrange
        User existingUser = new User();
        existingUser.setUserName("ExistingUser");
        existingUser.setEmail("used@example.com");
        existingUser.setPassword(passwordEncoder.encode("anypassword"));
        userRepository.save(existingUser);

        UserRegisterDto dto = new UserRegisterDto();
        dto.setUserName("NewUser");
        dto.setEmail("used@example.com");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");

        // Act
        mockMvc.perform(post("/paymybuddy/register")
                        .flashAttr("userRegisterDto", dto))
                // Assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/register"))
                .andExpect(flash().attribute("emailError", "Un utilisateur avec cet email existe déjà"))
                .andExpect(flash().attributeExists("userRegisterDto"));
    }
}
