package com.rutaexpress.report.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/** Perfil por defecto (dev): sin seguridad; el listener Kafka no arranca sin broker. */
@SpringBootTest(properties = {"spring.kafka.listener.auto-startup=false"})
@AutoConfigureMockMvc
class ReportApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void kpisResponde200() throws Exception {
        mvc.perform(get("/api/reports/kpis")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").exists());
    }

    @Test
    void kpisConRangoResponde200() throws Exception {
        mvc.perform(get("/api/reports/kpis").param("range", "24")).andExpect(status().isOk());
    }

    @Test
    void metricasResponden200() throws Exception {
        mvc.perform(get("/api/reports/hourly")).andExpect(status().isOk());
        mvc.perform(get("/api/reports/leadtime")).andExpect(status().isOk());
        mvc.perform(get("/api/reports/top-services")).andExpect(status().isOk());
    }

    @Test
    void rangoInvalidoRetorna400() throws Exception {
        mvc.perform(get("/api/reports/kpis").param("range", "0")).andExpect(status().isBadRequest());
    }
}
