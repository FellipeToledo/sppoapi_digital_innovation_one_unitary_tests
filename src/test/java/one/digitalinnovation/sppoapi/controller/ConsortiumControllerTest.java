package one.digitalinnovation.sppoapi.controller;

import one.digitalinnovation.sppoapi.builder.ConsortiumDTOBuilder;
import one.digitalinnovation.sppoapi.controllers.ConsortiumController;
import one.digitalinnovation.sppoapi.dto.request.ConsortiumDTO;
import one.digitalinnovation.sppoapi.exception.ConsortiumNotFoundException;
import one.digitalinnovation.sppoapi.services.ConsortiumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static one.digitalinnovation.sppoapi.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Fellipe Toledo
 */

@ExtendWith(MockitoExtension.class)
public class ConsortiumControllerTest {

    private static final String CONSORTIUM_API_URL_PATH = "/api/v1/consortium";
    private static final long VALID_CONSORTIUM_ID = 1L;
    private static final long INVALID_CONSORTIUM_ID = 2L;
    private static final String CONSORTIUM_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String CONSORTIUM_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private ConsortiumService consortiumService;

    @InjectMocks
    private ConsortiumController consortiumController;

    @BeforeEach
    void setUp() {


        mockMvc = MockMvcBuilders.standaloneSetup(consortiumController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenAConsortiumIsCreated() throws Exception {
        // given
        //CompanyDTO companyDTOBuilder = CompanyDTOBuilder.builder().build().toCompanyDTO();
        ConsortiumDTO consortiumDTOBuilder = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();
        //asJsonString(companyDTOBuilder);

        // when
        when(consortiumService.create(consortiumDTOBuilder)).thenReturn(consortiumDTOBuilder);

        // then
        mockMvc.perform(post(CONSORTIUM_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(consortiumDTOBuilder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(consortiumDTOBuilder.getName())))
                .andExpect(jsonPath("$.cnpj", is(consortiumDTOBuilder.getCnpj())));
                //.andExpect(jsonPath("$.companies", is(asJsonString(companyDTOBuilder))));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        ConsortiumDTO consortiumDTOBuilder = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();
        consortiumDTOBuilder.setCnpj(null);

        // then
        mockMvc.perform(post(CONSORTIUM_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(consortiumDTOBuilder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned () throws Exception {
        // given
        ConsortiumDTO consortiumDTOBuilder = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();

        // when
        when(consortiumService.findByName(consortiumDTOBuilder.getName())).thenReturn(consortiumDTOBuilder);

        // then
        mockMvc.perform(get(CONSORTIUM_API_URL_PATH + "/" + consortiumDTOBuilder.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(consortiumDTOBuilder.getName())))
                        .andExpect(jsonPath("$.cnpj", is(consortiumDTOBuilder.getCnpj())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned () throws Exception {
        // given
        ConsortiumDTO consortiumDTOBuilder = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();

        // when
        when(consortiumService.findByName(consortiumDTOBuilder.getName())).thenThrow(ConsortiumNotFoundException.class);

        // then
        mockMvc.perform(get(CONSORTIUM_API_URL_PATH + "/" + consortiumDTOBuilder.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}