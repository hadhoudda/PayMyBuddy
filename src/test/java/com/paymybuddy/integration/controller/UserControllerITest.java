package com.paymybuddy.integration.controller;

import com.paymybuddy.controller.UserController;
import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.User;
import com.paymybuddy.service.contracts.IUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // désactive Spring Security pour les tests
class UserControllerITest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    //@MockBean
//    @Autowired
//    private IUserService userService;
//
//    //@MockBean
//    @MockitoBean
//    private PasswordEncoder passwordEncoder;
//
//    //@MockBean
//    @MockitoBean
//    private AuthenticationManager authenticationManager;
//
//    @Test
//    @DisplayName("GET /login - Affiche le formulaire de connexion")
//    void testShowLoginForm() throws Exception {
//        mockMvc.perform(get("/paymybuddy/login"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("login"))
//                .andExpect(model().attributeExists("userLoginDto"));
//    }
//
//    @Test
//    @DisplayName("GET /register - Affiche le formulaire d'inscription")
//    void testShowRegisterForm() throws Exception {
//        mockMvc.perform(get("/paymybuddy/register"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("register"))
//                .andExpect(model().attributeExists("userRegisterDto"));
//    }
//
//    @Test
//    @DisplayName("GET /register/confirmed - Affiche la confirmation")
//    void testShowConfirmationRegister() throws Exception {
//        mockMvc.perform(get("/paymybuddy/register/confirmed"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("confirmationRegister"));
//    }
//
//    @Test
//    @DisplayName("POST /register - Inscription réussie")
//    void testRegisterUserSuccess() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setEmail("test@example.com");
//        dto.setPassword("password123");
//        dto.setConfirmPassword("password123");
//
//        User mockUser = new User();
//        when(userService.createUser(any(UserRegisterDto.class))).thenReturn(mockUser);
//
//        mockMvc.perform(post("/paymybuddy/register/confirmed")
//                        .flashAttr("userRegisterDto", dto))
//                .andExpect(redirectedUrl("/paymybuddy/register/confirmed"));
//    }
//
//    @Test
//    @DisplayName("POST /register - Mots de passe différents")
//    void testRegisterUserPasswordMismatch() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setEmail("test@example.com");
//        dto.setPassword("password123");
//        dto.setConfirmPassword("different");
//
//        mockMvc.perform(post("/paymybuddy/register")
//                        .flashAttr("userRegisterDto", dto))
//                .andExpect(redirectedUrl("/paymybuddy/register"))
//                .andExpect(flash().attributeExists("userRegisterDto"))
//                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegisterDto"));
//    }
//
//    @Test
//    @DisplayName("POST /register - Email déjà utilisé")
//    void testRegisterUserEmailAlreadyUsed() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setEmail("used@example.com");
//        dto.setPassword("password123");
//        dto.setConfirmPassword("password123");
//
//        doThrow(new IllegalArgumentException("Email déjà utilisé"))
//                .when(userService).createUser(any(UserRegisterDto.class));
//
//        mockMvc.perform(post("/paymybuddy/register")
//                        .flashAttr("userRegisterDto", dto))
//                .andExpect(redirectedUrl("/paymybuddy/register"))
//                .andExpect(flash().attribute("emailError", "Email déjà utilisé"))
//                .andExpect(flash().attributeExists("userRegisterDto"));
//    }
}
