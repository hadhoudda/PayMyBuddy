package com.paymybuddy.integration.controller;

import com.paymybuddy.dto.TransactionDto;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.ITransactionService;
import com.paymybuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithUserDetails;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithUserDetails(value = "user1@yahoo.fr", userDetailsServiceBeanName = "customUserDetailsService")
@Transactional
public class TransactionControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User sender;
    private User recipient;

    @BeforeEach
    void setUp() {
        // Récupère les utilisateurs déjà en base
        sender = userRepository.findByEmail("user1@yahoo.fr").orElseThrow();
        recipient = userRepository.findByEmail("user2@yahoo.fr").orElseThrow();

        // Initialise un solde de test (optionnel selon ta logique)
        sender.setSolde(new BigDecimal("100.00"));
        recipient.setSolde(new BigDecimal("50.00"));
        userRepository.save(sender);
        userRepository.save(recipient);
    }

    @Test
    void testShowTransfertForm() throws Exception {
        mockMvc.perform(get("/paymybuddy/transfert"))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeExists("transactionDto"))
                .andExpect(model().attributeExists("contacts"))
                .andExpect(model().attributeExists("transactions"));
    }

    @Test
    void testTransfertSoldeSuccess() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setEmail("user2@yahoo.fr");
        dto.setTransactionAmount(20.0);
        dto.setTransactionDescription("Payment");

        mockMvc.perform(post("/paymybuddy/transfert")
                        .with(csrf())   // <--- Ajoute ce token CSRF ici
                        .flashAttr("transactionDto", dto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/paymybuddy/transfert"))
                .andExpect(flash().attributeExists("successMessage"));

        User reloadedSender = userRepository.findByEmail("user1@yahoo.fr").orElseThrow();
        User reloadedRecipient = userRepository.findByEmail("user2@yahoo.fr").orElseThrow();

        // prends en compte les frais (exemple 0.05% frais)
        BigDecimal expectedSenderSolde = new BigDecimal("79.90"); // 100 - 20 - 0.1 (frais)
        BigDecimal expectedRecipientSolde = new BigDecimal("70.00"); // 50 + 20

        assertThat(reloadedSender.getSolde()).isEqualByComparingTo(expectedSenderSolde);
        assertThat(reloadedRecipient.getSolde()).isEqualByComparingTo(expectedRecipientSolde);
    }



    @Test
    @DisplayName("POST /paymybuddy/transfert - montant invalide")
    @WithMockUser(username = "user1@yahoo.fr", roles = {"USER"})
    void testTransfertSoldeInvalidAmount() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setEmail("recipient@example.com");
        dto.setTransactionAmount(-5.0);
        dto.setTransactionDescription("Desc");

        mockMvc.perform(post("/paymybuddy/transfert")
                        .with(csrf())
                        .flashAttr("transactionDto", dto))
                .andExpect(status().isOk())
                .andExpect(view().name("transfert"))
                .andExpect(model().attributeHasFieldErrors("transactionDto", "transactionAmount"));
    }


}
