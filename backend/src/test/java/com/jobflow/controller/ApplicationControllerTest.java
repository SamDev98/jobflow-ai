package com.jobflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.request.CreateApplicationRequest;
import com.jobflow.dto.response.ApplicationResponse;
import com.jobflow.entity.enums.Stage;
import com.jobflow.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
@SuppressWarnings("null")
class ApplicationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ApplicationService applicationService;

        @Test
        @WithMockUser
        void listApplications_returns200() throws Exception {
                ApplicationResponse response = buildResponse();
                when(applicationService.listApplications(isNull(), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(List.of(response)));

                mockMvc.perform(get("/applications"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].company").value("Acme Corp"));
        }

        @Test
        @WithMockUser
        void createApplication_returns201() throws Exception {
                CreateApplicationRequest request = new CreateApplicationRequest(
                                "Acme Corp", "Backend Engineer",
                                null, null, null, Stage.APPLIED,
                                null, null, null, null);

                ApplicationResponse response = buildResponse();
                when(applicationService.create(any(CreateApplicationRequest.class))).thenReturn(response);

                mockMvc.perform(post("/applications")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.company").value("Acme Corp"));
        }

        private ApplicationResponse buildResponse() {
                return new ApplicationResponse(
                                UUID.randomUUID(),
                                "Acme Corp",
                                "Backend Engineer",
                                null, null, null,
                                Stage.APPLIED,
                                null, null, null,
                                false, null, null,
                                Instant.now(), Instant.now());
        }
}
