package com.softserve.marathon.controller;

import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.model.Marathon;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MarathonControllerTest {
    private final MockMvc mockMvc;
    private final MarathonService marathonService;

    @Autowired
    public MarathonControllerTest(MockMvc mockMvc, MarathonService marathonService) {
        this.mockMvc = mockMvc;
        this.marathonService = marathonService;
    }

    @Test
    public void showAllMarathonsTest() throws Exception {
        List<Marathon> expected = marathonService.getAll();
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("marathons"))
                .andExpect(MockMvcResultMatchers.model().attribute("marathons", expected));
    }

    @Test
    public void addMarathonTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/marathons/add")
                .param("title", "marathon1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
        assertEquals(1, marathonService.getAll().stream()
                .filter(m -> m.getTitle().equals("marathon1"))
                .count());
    }

    @Test
    public void closeMarathonTest() throws Exception {
        //given
        Marathon marathon = new Marathon("marathon test");
        marathonService.createOrUpdateMarathon(marathon);
        assertEquals(false, marathon.isClosed());
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons/delete/" + marathon.getId()));
        //then
        Marathon marathonFromDb = marathonService.getMarathonById(marathon.getId());
        assertEquals(true, marathonFromDb.isClosed());
        assertEquals(marathon.getTitle(), marathonFromDb.getTitle());
    }

    @Test
    public void updateMarathonTest() throws Exception {
        //given
        Marathon marathon = new Marathon("marathon test");
        marathonService.createOrUpdateMarathon(marathon);
        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/marathons/add")
                .param("id", String.valueOf(marathon.getId()))
                .param("title", "new marathon"));
        //then
        Marathon marathonFromDb = marathonService.getMarathonById(marathon.getId());
        assertEquals("new marathon", marathonFromDb.getTitle());
    }

    @Test
    public void updateOrCreateTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons/add"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateTest() throws Exception {
        Marathon expected = new Marathon("marathon test");
        marathonService.createOrUpdateMarathon(expected);
        mockMvc.perform(MockMvcRequestBuilders.get("/marathons/edit/" + expected.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("marathon", expected));
    }
}
