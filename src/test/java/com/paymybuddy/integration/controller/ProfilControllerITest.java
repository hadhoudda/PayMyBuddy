package com.paymybuddy.integration.controller;

import com.paymybuddy.controller.ProfilController;
import com.paymybuddy.model.User;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.contracts.IUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithUserDetails(value = "user1@yahoo.fr", userDetailsServiceBeanName = "customUserDetailsService")
@Transactional
public class ProfilControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IUserService userService;

    @Autowired
    private ContactService contactService;

    @Test
    @WithUserDetails(value = "user1@yahoo.fr", userDetailsServiceBeanName = "customUserDetailsService")
    void shouldDisplayProfilPage() throws Exception {
        mockMvc.perform(get("/paymybuddy/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("contacts"));
    }

    @Test
    @WithUserDetails(value = "user1@yahoo.fr", userDetailsServiceBeanName = "customUserDetailsService")
    void shouldAddSoldeSuccessfully() throws Exception {
        mockMvc.perform(post("/paymybuddy/profil/solde")
                        .param("montant", "50.0")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/profil"))
                .andExpect(flash().attribute("success", "Solde ajouté avec succès !"));
    }


    @Test
    @WithUserDetails(value = "user1@yahoo.fr", userDetailsServiceBeanName = "customUserDetailsService")
    void shouldUpdateUserNameSuccessfully() throws Exception {
        mockMvc.perform(post("/paymybuddy/profil/username")
                        .param("username", "NewName")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/profil"))
                .andExpect(flash().attribute("success", "UserName modifié avec succès !"));

        // Optionnel : vérifier en base que le username a été mis à jour
        Optional<User> updatedUser = userService.findUserByEmail("user1@yahoo.fr");
        assertTrue(updatedUser.isPresent());
        assertEquals("NewName", updatedUser.get().getUserName());
    }


    @Test
    @WithUserDetails(value = "user1@yahoo.fr", userDetailsServiceBeanName = "customUserDetailsService")
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(post("/paymybuddy/profil/delete")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy"))
                .andExpect(flash().attribute("successMessage", "Utilisateur supprimé avec succès !"));

        // Vérifier que l'utilisateur a bien été supprimé en base
        Optional<User> deletedUser = userService.findUserByEmail("user1@yahoo.fr");
        assertTrue(deletedUser.isEmpty());
    }


//    @Test
//    void shouldRedirectToHomeIfNotAuthenticated() throws Exception {
//        mockMvc.perform(get("/paymybuddy/profil"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrlPattern("**/login"));
//    }

}
