package one.digitalinnovation.sppoapi.services;

import lombok.AllArgsConstructor;
import one.digitalinnovation.sppoapi.dto.mapper.ConsortiumMapper;
import one.digitalinnovation.sppoapi.dto.request.ConsortiumDTO;
import one.digitalinnovation.sppoapi.dto.response.MessageResponseDTO;
import one.digitalinnovation.sppoapi.entities.Consortium;
import one.digitalinnovation.sppoapi.exception.ConsortiumAlreadyRegisteredException;
import one.digitalinnovation.sppoapi.exception.ConsortiumNotFoundException;
import one.digitalinnovation.sppoapi.repositories.ConsortiumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ConsortiumService {

    private final ConsortiumRepository consortiumRepository;
    private final ConsortiumMapper consortiumMapper = ConsortiumMapper.INSTANCE;

    public ConsortiumDTO create(ConsortiumDTO consortiumDTO) throws ConsortiumAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(String.valueOf(consortiumDTO.getName()));
        Consortium consortium = consortiumMapper.toModel(consortiumDTO);
        Consortium savedConsortium = consortiumRepository.save(consortium);
        return consortiumMapper.toDTO(savedConsortium);
    }

    public ConsortiumDTO findByName(String name) throws ConsortiumNotFoundException {
        Consortium foundConsortium = consortiumRepository.findByName(name)
                .orElseThrow(() -> new ConsortiumNotFoundException(name));
        return consortiumMapper.toDTO(foundConsortium);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws ConsortiumAlreadyRegisteredException {
        Optional<Consortium> optSavedConsortium = consortiumRepository.findByName(name);
        if (optSavedConsortium.isPresent()) {
            throw new ConsortiumAlreadyRegisteredException(name);
        }
    }



    public ConsortiumDTO findById(Long id) throws ConsortiumNotFoundException {
        Consortium consortium = consortiumRepository.findById(id)
                .orElseThrow(() -> new ConsortiumNotFoundException(id));

        return consortiumMapper.toDTO(consortium);
    }

    public List<ConsortiumDTO> listAll() {
        List<Consortium> consortium = consortiumRepository.findAll();
        return consortium.stream()
                .map(consortiumMapper::toDTO)
                .collect(Collectors.toList());
    }

    public MessageResponseDTO update(Long id, ConsortiumDTO consortiumDTO) throws ConsortiumNotFoundException {
        consortiumRepository.findById(id)
                .orElseThrow(() -> new ConsortiumNotFoundException(id));

        Consortium updatedConsortium = consortiumMapper.toModel(consortiumDTO);
        Consortium savedConsortium = consortiumRepository.save(updatedConsortium);

        MessageResponseDTO messageResponse = createMessageResponse("Consortium successfully updated with ID ", savedConsortium.getId());

        return messageResponse;
    }

    public void delete(Long id) throws ConsortiumNotFoundException {
        consortiumRepository.findById(id)
                .orElseThrow(() -> new ConsortiumNotFoundException(id));
        consortiumRepository.deleteById(id);
    }

    private MessageResponseDTO createMessageResponse(String s, Long id2) {
        return MessageResponseDTO.builder()
                .message(s + id2)
                .build();
    }
}
