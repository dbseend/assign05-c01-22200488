package com.webservice.week05;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MovieApiTest {

    @Autowired
    MockMvc mvc;

    static final String VALID = """
            {"title":"Interstellar","director":"Christopher Nolan","genre":"SF",
             "releaseYear":2014,"rating":8.7,"runningTime":169}""";

    @Test
    void createGetUpdateDeleteAnd404() throws Exception {
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Interstellar"));

        mvc.perform(get("/api/movies")).andExpect(status().isOk());
        mvc.perform(get("/api/movies/1")).andExpect(status().isOk());

        mvc.perform(put("/api/movies/1").contentType(MediaType.APPLICATION_JSON)
                        .content(VALID.replace("8.7", "9.0")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(9.0));

        mvc.perform(delete("/api/movies/1")).andExpect(status().isNoContent());
        mvc.perform(get("/api/movies/1")).andExpect(status().isNotFound());
        mvc.perform(put("/api/movies/1").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/movies/1")).andExpect(status().isNotFound());
    }

    @Test
    void invalidInputReturns400() throws Exception {
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                        .content(VALID.replace("\"Interstellar\"", "\"\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                        .content(VALID.replace("8.7", "11.5")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.rating").exists());
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                        .content(VALID.replace("169", "-5")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON).content("{bad json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void genreFilter() throws Exception {
        mvc.perform(post("/api/movies").contentType(MediaType.APPLICATION_JSON)
                .content(VALID.replace("\"SF\"", "\"Drama\"").replace("Interstellar", "FilterDrama")));
        mvc.perform(get("/api/movies?genre=drama"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.genre != 'Drama')]").isEmpty());
        mvc.perform(get("/api/movies?genre=NoSuchGenre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
