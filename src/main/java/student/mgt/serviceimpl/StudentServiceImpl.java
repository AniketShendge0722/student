package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.StudentDao;
import student.mgt.dao.ParentDao;
import student.mgt.dao.BusDao;

import student.mgt.dto.StudentDTO;
import student.mgt.entity.Student;
import student.mgt.entity.Parent;
import student.mgt.entity.Bus;

import student.mgt.mapper.StudentMapper;
import student.mgt.service.StudentService;

import student.mgt.exception.ResourceNotFoundException;
import student.mgt.exception.DuplicateResourceException;
import student.mgt.exception.BusCapacityExceededException;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;
    private final ParentDao parentDao;
    private final BusDao busDao;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(
            StudentDao studentDao,
            ParentDao parentDao,
            BusDao busDao,
            StudentMapper studentMapper) {

        this.studentDao = studentDao;
        this.parentDao = parentDao;
        this.busDao = busDao;
        this.studentMapper = studentMapper;
    }

    @Override
    public StudentDTO save(StudentDTO dto) {

        if (studentDao.existsByRollNo(dto.getRollNo())) {
            throw new DuplicateResourceException(
                    "Student roll number already exists: " + dto.getRollNo()
            );
        }

        validateParent(dto.getParentId());

        if (dto.getBusId() != null) {
            validateBusCapacity(dto.getBusId());
        }

        Student student = studentMapper.toStudentEntity(dto);

        Student savedStudent = studentDao.save(student);

        return studentMapper.toStudentDTO(savedStudent);
    }

    @Override
    public StudentDTO update(Long id, StudentDTO dto) {

        Student existingStudent = studentDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: " + id
                        )
                );

        if (studentDao.existsByRollNoAndIdNot(
                dto.getRollNo(), id)) {

            throw new DuplicateResourceException(
                    "Roll number already assigned to another student: "
                            + dto.getRollNo()
            );
        }

        validateParent(dto.getParentId());

        if (dto.getBusId() != null) {

            Long oldBusId = existingStudent.getBus() == null
                    ? null
                    : existingStudent.getBus().getId();

            boolean changingBus =
                    !dto.getBusId().equals(oldBusId);

            if (changingBus) {
                validateBusCapacity(dto.getBusId());
            }
        }

        studentMapper.updateStudentEntity(existingStudent, dto);

        Student updatedStudent = studentDao.update(existingStudent);

        return studentMapper.toStudentDTO(updatedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO findById(Long id) {

        Student student = studentDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: " + id
                        )
                );

        return studentMapper.toStudentDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {

        return studentMapper.toStudentDTOList(
                studentDao.findAll()
        );
    }

    @Override
    public void deleteById(Long id) {

        Student student = studentDao.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with ID: " + id
                        )
                );

        studentDao.deleteById(student.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDTO findByRollNo(String rollNo) {

        Student student = studentDao.findByRollNo(rollNo)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with roll number: " + rollNo
                        )
                );

        return studentMapper.toStudentDTO(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findByClassName(String className) {

        return studentMapper.toStudentDTOList(
                studentDao.findByClassName(className)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findByName(String name) {

        return studentMapper.toStudentDTOList(
                studentDao.findByName(name)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findByBusId(Long busId) {

        return studentMapper.toStudentDTOList(
                studentDao.findByBusId(busId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> searchStudents(String keyword) {

        return studentMapper.toStudentDTOList(
                studentDao.searchStudents(keyword)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return studentDao.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveStudents() {
        return studentDao.countActiveStudents();
    }

    private void validateParent(Long parentId) {

        if (parentId == null || !parentDao.findById(parentId).isPresent()) {

            throw new ResourceNotFoundException(
                    "Valid parent is required for student registration."
            );
        }
    }

    private void validateBusCapacity(Long busId) {

        Bus bus = busDao.findById(busId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Bus not found with ID: " + busId
                        )
                );

        long assignedStudents =
                busDao.countStudentsAssignedToBus(busId);

        if (assignedStudents >= bus.getCapacity()) {

            throw new BusCapacityExceededException(
                    "Bus " + bus.getBusNumber()
                            + " is full. Maximum capacity: "
                            + bus.getCapacity()
            );
        }
    }
}