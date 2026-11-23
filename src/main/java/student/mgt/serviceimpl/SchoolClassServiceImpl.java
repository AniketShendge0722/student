package student.mgt.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.SchoolClassDao;
import student.mgt.entity.SchoolClass;
import student.mgt.service.SchoolClassService;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SchoolClassServiceImpl implements SchoolClassService {

    private final SchoolClassDao schoolClassDao;

    public SchoolClassServiceImpl(SchoolClassDao schoolClassDao) {
        this.schoolClassDao = schoolClassDao;
    }

    @Override
    public List<SchoolClass> findAllActive() {
        return schoolClassDao.findAllActive();
    }
}