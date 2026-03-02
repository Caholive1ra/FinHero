package com.finhero.finhero.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finhero.finhero.dto.AuthResponse;
import com.finhero.finhero.dto.CreateTransactionDTO;
import com.finhero.finhero.dto.LinkDuplaDTO;
import com.finhero.finhero.dto.LoginDTO;
import com.finhero.finhero.dto.RegisterDTO;
import com.finhero.finhero.model.Category;
import com.finhero.finhero.model.Dupla;
import com.finhero.finhero.model.Transaction;
import com.finhero.finhero.repository.CategoryRepository;
import com.finhero.finhero.repository.DuplaRepository;
import com.finhero.finhero.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DuplaRepository duplaRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    private String userToken;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        var user1 = new com.finhero.finhero.model.User();
        user1.setEmail("user1@test.com");
        user1.setPasswordHash(passwordEncoder.encode("password123"));
        user1.setInviteCode("USER1001");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        var savedUser1 = userRepository.save(user1);
        userId = savedUser1.getId();

        var user2 = new com.finhero.finhero.model.User();
        user2.setEmail("user2@test.com");
        user2.setPasswordHash(passwordEncoder.encode("password123"));
        user2.setInviteCode("USER2002");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());
        var savedUser2 = userRepository.save(user2);

        Category category = new Category();
        category.setName("Alimentação");
        category.setUserId(userId);
        category.setCreatedAt(LocalDateTime.now());
        categoryRepository.save(category);

        Dupla dupla = new Dupla();
        dupla.setUserAId(userId);
        dupla.setUserBId(savedUser2.getId());
        dupla.setCreatedAt(LocalDateTime.now());
        duplaRepository.save(dupla);

        LoginDTO loginDTO = new LoginDTO("user1@test.com", "password123");
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andReturn().getResponse().getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);
        userToken = authResponse.getToken();
    }

    @Test
    void testCreateTransaction_WithValidData_ShouldReturn201() throws Exception {
        var categories = categoryRepository.findByUserId(userId);
        Long categoryId = categories.isEmpty() ? 1L : categories.get(0).getId();
        
        CreateTransactionDTO dto = new CreateTransactionDTO();
        dto.setType(Transaction.TransactionType.DESPESA);
        dto.setAmount(BigDecimal.valueOf(100.50));
        dto.setDescription("Supermercado");
        dto.setCategoryId(categoryId);

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value("DESPESA"))
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.description").value("Supermercado"));
    }

    @Test
    void testCreateTransaction_WithoutToken_ShouldReturn401() throws Exception {
        CreateTransactionDTO dto = new CreateTransactionDTO();
        dto.setType(Transaction.TransactionType.DESPESA);
        dto.setAmount(BigDecimal.valueOf(100.50));
        dto.setDescription("Supermercado");
        dto.setCategoryId(1L);

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetTransactions_ShouldReturnPagedTransactions() throws Exception {
        var categories = categoryRepository.findByUserId(userId);
        Long categoryId = categories.isEmpty() ? 1L : categories.get(0).getId();
        
        CreateTransactionDTO dto = new CreateTransactionDTO();
        dto.setType(Transaction.TransactionType.DESPESA);
        dto.setAmount(BigDecimal.valueOf(100.50));
        dto.setDescription("Supermercado");
        dto.setCategoryId(categoryId);

        mockMvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/transactions")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}

