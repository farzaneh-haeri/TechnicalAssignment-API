package com.silverrail.technicalassignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silverrail.technicalassignment.controller.CharsToAppend;
import com.silverrail.technicalassignment.controller.StateController;
import com.silverrail.technicalassignment.entity.State;
import com.silverrail.technicalassignment.service.StateService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StateService stateService;

    @InjectMocks
    private StateController stateController;


    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(stateController)
                .build();

    }
    // =========================================== Get Current State ==========================================

    @Test
    public void test_get_current_state() throws Exception {
        MockHttpSession session = new MockHttpSession();
        String userId = String.valueOf(session.getId().hashCode());
        State state = new State(userId, "");

        when(stateService.getState(userId)).thenReturn(state);

        mockMvc.perform(get("/state").session(session).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value", is("")));

        verify(stateService, times(1)).getState(userId);
        verifyNoMoreInteractions(stateService);
    }

    // ===========================================  Append  ==========================================

    @Test
    //TODO Should be revised.
    public void test_append() throws Exception{
        MockHttpSession session = new MockHttpSession();
        String userId = String.valueOf(session.getId().hashCode());
        CharsToAppend charsToAppend = new CharsToAppend('c',2);
        State state = new State(userId, "cc");
        when(stateService.append(userId, charsToAppend)).thenReturn(state);

        mockMvc.perform(
                post("/chars").session(session).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(asJsonString(charsToAppend)))
                .andExpect(status().isOk());

        verify(stateService, times(1)).append(userId, charsToAppend);
        verifyNoMoreInteractions(stateService);
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // =========================================== Sum ==========================================

    @Test
    public void test_get_state_number_sum() throws Exception{
        MockHttpSession session = new MockHttpSession();
        String userId = String.valueOf(session.getId().hashCode());

        when(stateService.getStateNumbersSum(userId)).thenReturn(0L);
        mockMvc.perform(
                get("/sum").session(session).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(stateService, times(1)).getStateNumbersSum(userId);
        verifyNoMoreInteractions(stateService);
    }

    // =========================================== State without Numbers ==========================================
    @Test
    public void test_get_state_without_numbers() throws Exception{
        MockHttpSession session = new MockHttpSession();
        String userId = String.valueOf(session.getId().hashCode());
        when(stateService.getStateWithoutNumbers(userId)).thenReturn("");

        mockMvc.perform(
                get("/chars").session(session).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        verify(stateService, times(1)).getStateWithoutNumbers(userId);
        verifyNoMoreInteractions(stateService);
    }

    // =========================================== Delete ==========================================
    @Test
    //TODO Should be revised.
    public void test_delete_last_occurrence_of_character() throws Exception{
        MockHttpSession session = new MockHttpSession();
        String userId = String.valueOf(session.getId().hashCode());
        State state = new State(userId, "");

        when(stateService.deleteLastOccurrenceOfCharacter(userId, '\0')).thenReturn(state);

        mockMvc.perform(
                post("/chars/{character}", "").session(session).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(stateService, times(1)).deleteLastOccurrenceOfCharacter(userId, '\0');
        verifyNoMoreInteractions(stateService);
    }

    @Test
    public void test_append_fail_because_of_none_alphabetic_char() throws Exception{
        CharsToAppend charsToAppend = new CharsToAppend('@',1);

        mockMvc.perform(
                post("/chars").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(asJsonString(charsToAppend)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_append_fail_because_of_invalid_count() throws Exception{
        CharsToAppend charsToAppend = new CharsToAppend('c',13);

        mockMvc.perform(
                post("/chars").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(asJsonString(charsToAppend)))
                .andExpect(status().isBadRequest());
    }
}
