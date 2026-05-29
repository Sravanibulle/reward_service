package com.reward.services.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RewardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRewardsForValidCustomer() throws Exception {

        mockMvc.perform(get("/api/rewards/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(101))
                .andExpect(jsonPath("$.customerName").value("Sravani Bulle"))
                .andExpect(jsonPath("$.totalPoints").exists())
                .andExpect(jsonPath("$.monthlyRewards").isArray());
    }

    @Test
    void shouldReturn404ForUnknownCustomer() throws Exception {

        mockMvc.perform(get("/api/rewards/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnCorrectTotalPointsForKnownCustomer() throws Exception {

        mockMvc.perform(get("/api/rewards/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints").value(365));
    }

    @Test
    void shouldReturnMonthlyBreakdownWithAtLeastOneEntry() throws Exception {

        mockMvc.perform(get("/api/rewards/102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyRewards").isArray())
                .andExpect(jsonPath("$.monthlyRewards[0].month").exists())
                .andExpect(jsonPath("$.monthlyRewards[0].points").exists());
    }

    @Test
    void shouldNotReturnCustomerWithOnlyOutOfWindowTransactions() throws Exception {

        mockMvc.perform(get("/api/rewards/106"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllActiveCustomerRewards() throws Exception {

        mockMvc.perform(get("/api/rewards/allCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].customerId").exists())
                .andExpect(jsonPath("$[0].customerName").exists())
                .andExpect(jsonPath("$[0].totalPoints").exists())
                .andExpect(jsonPath("$[0].monthlyRewards").isArray());
    }

    @Test
    void shouldNotIncludeOldCustomerInAllCustomerRewards() throws Exception {

        mockMvc.perform(get("/api/rewards/allCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.customerId == 106)]").doesNotExist());
    }
}
