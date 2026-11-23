
package student.mgt.serviceimpl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.ParentDao;
import student.mgt.dto.ParentDTO;
import student.mgt.entity.Parent;
import student.mgt.exception.DuplicateResourceException;
import student.mgt.exception.ResourceNotFoundException;
import student.mgt.mapper.StudentMapper;
import student.mgt.service.ParentService;

@Service
@Transactional
public class ParentServiceImpl implements ParentService {

    private final ParentDao parentDao;
    private final StudentMapper studentMapper;

    public ParentServiceImpl(
            ParentDao parentDao,
            StudentMapper studentMapper) {

        this.parentDao = parentDao;
        this.studentMapper = studentMapper;
    }

    @Override
    public ParentDTO save(ParentDTO dto) {

        if (parentDao.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Parent email already exists: " + dto.getEmail()
            );
        }

        if (parentDao.existsByPhone(dto.getPhone())) {
            throw new DuplicateResourceException(
                    "Phone number already assigned to another parent: "
                            + dto.getPhone()
            );
        }

        Parent parent = studentMapper.toParentEntity(dto);

        Parent savedParent = parentDao.save(parent);

        return studentMapper.toParentDTO(savedParent);
    }

    @Override
    public ParentDTO update(Long id, ParentDTO dto) {

        Parent existingParent = parentDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Parent not found with ID: " + id
                        )
                );

        parentDao.findByEmail(dto.getEmail())
                .filter(parent -> !parent.getId().equals(id))
                .ifPresent(parent -> {
                    throw new DuplicateResourceException(
                            "Parent email already exists: " + dto.getEmail()
                    );
                });

        if (!Objects.equals(existingParent.getPhone(), dto.getPhone())) {

            boolean phoneExists = parentDao.searchParents("")
                    .stream()
                    .anyMatch(parent ->
                            !parent.getId().equals(id)
                                    && Objects.equals(
                                    parent.getPhone(),
                                    dto.getPhone()
                            )
                    );

            if (phoneExists) {
                throw new DuplicateResourceException(
                        "Parent mobile number already exists: "
                                + dto.getPhone()
                );
            }
        }

        studentMapper.updateParentEntity(existingParent, dto);

        Parent updatedParent = parentDao.update(existingParent);

        return studentMapper.toParentDTO(updatedParent);
    }

    @Override
    @Transactional(readOnly = true)
    public ParentDTO findById(Long id) {

        Parent parent = parentDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Parent not found with ID: " + id
                        )
                );

        return studentMapper.toParentDTO(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParentDTO> findAll() {

        return studentMapper.toParentDTOList(
                parentDao.findAll()
        );
    }

    @Override
    public void deleteById(Long id) {

        Parent parent = parentDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Parent not found with ID: " + id
                        )
                );

        if (parent.getStudents() != null
                && !parent.getStudents().isEmpty()) {

            throw new DuplicateResourceException(
                    "Cannot delete parent because students are linked "
                            + "to this parent."
            );
        }

        parentDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ParentDTO findByEmail(String email) {

        Parent parent = parentDao.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Parent not found with email: " + email
                        )
                );

        return studentMapper.toParentDTO(parent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParentDTO> searchParents(String keyword) {

        return studentMapper.toParentDTOList(
                parentDao.searchParents(keyword)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return parentDao.count();
    }
}
