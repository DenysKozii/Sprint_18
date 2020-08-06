package com.softserve.marathon.service;

import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.repository.MarathonRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MarathonServiceTest {

    @Autowired
    private MarathonService marathonService;

    @MockBean
    private MarathonRepository marathonRepository;

    @Test
    public void getAllTest() {
        //Given
        List<Marathon> expected = new ArrayList<>();
        Marathon marathon = new Marathon();
        marathon.setTitle("Marathon 1");
        expected.add(marathon);
        //When
        when(marathonRepository.findAll()).thenReturn(expected);
        //Then
        List<Marathon> actual = marathonService.getAll();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getMarathonByIdTest() {
        //Given
        Marathon expected = new Marathon();
        expected.setId(2L);
        expected.setTitle("Marathon 2");
        //When
        when(marathonRepository.findById(2L)).thenReturn(Optional.of(expected));
        //Then
        Marathon actual = marathonService.getMarathonById(2L);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void createOrUpdateMarathonTest() {
        //Given
        Marathon expected = new Marathon();
        expected.setId(3L);
        expected.setTitle("Marathon 3");
        //When
        when(marathonRepository.save(any())).thenReturn(expected);
        //Then
        Marathon actual = marathonService.createOrUpdateMarathon(expected);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteMarathonByIdTest() {
        Marathon expected = new Marathon();
        expected.setId(4L);
        expected.setTitle("Marathon 4");
        marathonRepository.save(expected);
        when(marathonRepository.findById(4L)).thenReturn(Optional.of(expected));
        doNothing().when(marathonRepository).deleteById(any());
        marathonService.deleteMarathonById(4L);
        verify(marathonRepository).deleteById(any());
    }
}
