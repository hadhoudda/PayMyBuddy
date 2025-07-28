package com.paymybuddy.integration.controller;

import com.paymybuddy.dto.ContactDto;
import com.paymybuddy.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ContactControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactService contactService;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldDisplayRelationPage() throws Exception {
        mockMvc.perform(get("/paymybuddy/relation"))
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attributeExists("contactDto"));
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldRedirectBackOnValidationError() throws Exception {
        MockHttpServletRequestBuilder request = post("/paymybuddy/relation")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "") // provoque une erreur de validation
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/relation"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.contactDto"))
                .andExpect(flash().attributeExists("contactDto"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldDeleteContact() throws Exception {
        MockHttpServletRequestBuilder request = post("/paymybuddy/contact/delete")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "user2@yahoo.fr")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/profil"));
    }




    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldDisplayRelationPageWithContactDtoFromFlash() throws Exception {
        mockMvc.perform(get("/paymybuddy/relation")
                        .flashAttr("contactDto", new ContactDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(model().attributeExists("contactDto"));
    }

}
