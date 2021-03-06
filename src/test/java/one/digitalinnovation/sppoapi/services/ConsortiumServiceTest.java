package one.digitalinnovation.sppoapi.services;

import one.digitalinnovation.sppoapi.builder.ConsortiumDTOBuilder;
import one.digitalinnovation.sppoapi.dto.mapper.ConsortiumMapper;
import one.digitalinnovation.sppoapi.dto.request.ConsortiumDTO;
import one.digitalinnovation.sppoapi.entities.Consortium;
import one.digitalinnovation.sppoapi.exception.ConsortiumAlreadyRegisteredException;
import one.digitalinnovation.sppoapi.exception.ConsortiumNotFoundException;
import one.digitalinnovation.sppoapi.repositories.ConsortiumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConsortiumServiceTest {

    private static final String INVALID_CONSORTIUM_NAME = "";

    @Mock
    private ConsortiumRepository consortiumRepository;

    private final ConsortiumMapper consortiumMapper = ConsortiumMapper.INSTANCE;

    @InjectMocks
    private ConsortiumService consortiumService;

    @Test
    void whenConsortiumInformedThenItShouldBeCreated() throws ConsortiumAlreadyRegisteredException {
        // given
        ConsortiumDTO expectedConsortiumDTO = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();
        Consortium expectedSavedConsortium = consortiumMapper.toModel(expectedConsortiumDTO);

        // when
        when(consortiumRepository.findByName(expectedConsortiumDTO.getName())).thenReturn(Optional.empty());
        when(consortiumRepository.save(expectedSavedConsortium)).thenReturn(expectedSavedConsortium);

        //then
        ConsortiumDTO createdConsortiumDTO = consortiumService.create(expectedConsortiumDTO);

        assertThat(createdConsortiumDTO.getId(), is(expectedConsortiumDTO.getId()));
        assertThat(createdConsortiumDTO.getName(), is(expectedConsortiumDTO.getName()));
        assertThat(createdConsortiumDTO.getCnpj(), is(expectedConsortiumDTO.getCnpj()));
    }

    @Test
    void whenAlreadyRegisteredConsortiumInformedThenAnExceptionShouldBeThrown() {
        // given
        ConsortiumDTO expectedConsortiumDTO = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();
        Consortium duplicatedConsortium = consortiumMapper.toModel(expectedConsortiumDTO);

        // when
        when(consortiumRepository.findByName(expectedConsortiumDTO.getName())).thenReturn(Optional.of(duplicatedConsortium));

        // then
        assertThrows(ConsortiumAlreadyRegisteredException.class, () -> consortiumService.create(expectedConsortiumDTO));
    }

    @Test
    void whenValidConsortiumNameGivenThenReturnAConsortium() throws ConsortiumNotFoundException {
        // given
        ConsortiumDTO expectedFoundConsortiumDTO = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();
        Consortium expectedFoundConsortium = consortiumMapper.toModel(expectedFoundConsortiumDTO);

        // when
        when(consortiumRepository.findByName(expectedFoundConsortium.getName())).thenReturn(Optional.of(expectedFoundConsortium));

        // then
        ConsortiumDTO foundConsortiumDTO = consortiumService.findByName(expectedFoundConsortiumDTO.getName());

        assertThat(foundConsortiumDTO, is(equalTo(expectedFoundConsortiumDTO)));
    }

    @Test
    void whenNotRegisteredConsortiumNameGivenThenThrowAnException() throws ConsortiumNotFoundException {
        // given
        ConsortiumDTO expectedFoundConsortiumDTO = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();

        // when
        when(consortiumRepository.findByName(expectedFoundConsortiumDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(ConsortiumNotFoundException.class,() -> consortiumService.findByName(expectedFoundConsortiumDTO.getName()));
    }

    @Test
    void whenListConsortiumIsCalledThenReturnAListOfConsortium() {
        // given
        ConsortiumDTO expectedFoundConsortiumDTO = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();
        Consortium expectedFoundConsortium = consortiumMapper.toModel(expectedFoundConsortiumDTO);

        // when
        when(consortiumRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundConsortium));
        List<ConsortiumDTO> consortiaDTO = consortiumService.listAll();

        // then
        assertThat(consortiaDTO, is(not(empty())));
        assertThat(consortiaDTO.get(0), is(expectedFoundConsortiumDTO));
    }

    @Test
    void whenListConsortiumIsCalledThenReturnAEmptyListOfConsortium() {
        // when
        when(consortiumRepository.findAll()).thenReturn(Collections.EMPTY_LIST);
        List<ConsortiumDTO> consortiaDTO = consortiumService.listAll();

        // then
        assertThat(consortiaDTO, is(empty()));
    }

    @Test
    void wenExclusionIsCalledWithValidNameThanAnConsortiumShouldBeDeleted() throws ConsortiumNotFoundException {
        // given
        ConsortiumDTO expectedDeletedConsortiumDTO = ConsortiumDTOBuilder.builder().build().toConsortiumDTO();
        Consortium expectedDeletedConsortium = consortiumMapper.toModel(expectedDeletedConsortiumDTO);

        // when
        when(consortiumRepository.findByName(expectedDeletedConsortiumDTO.getName())).thenReturn(Optional.of(expectedDeletedConsortium));
        doNothing().when(consortiumRepository).deleteByName(expectedDeletedConsortiumDTO.getName());

        // then
        consortiumService.deleteByName(expectedDeletedConsortiumDTO.getName());

        verify(consortiumRepository, times(1)).findByName(expectedDeletedConsortiumDTO.getName());
        verify(consortiumRepository, times(1)).deleteByName(expectedDeletedConsortiumDTO.getName());
    }

    @Test
    void whenExclusionIsCalledWithInvalidNameThenExceptionShouldBeThrown() {
        when(consortiumRepository.findByName(INVALID_CONSORTIUM_NAME)).thenReturn(Optional.empty());

        assertThrows(ConsortiumNotFoundException.class, () -> consortiumService.deleteByName(INVALID_CONSORTIUM_NAME));
    }

}
