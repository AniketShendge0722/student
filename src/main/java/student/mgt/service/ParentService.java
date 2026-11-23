package student.mgt.service;

import student.mgt.dto.ParentDTO;

import java.util.List;

public interface ParentService {

    ParentDTO save(ParentDTO parentDTO);

    ParentDTO update(Long id, ParentDTO parentDTO);

    ParentDTO findById(Long id);

    List<ParentDTO> findAll();

    void deleteById(Long id);

    ParentDTO findByEmail(String email);

    List<ParentDTO> searchParents(String keyword);

    long count();
}