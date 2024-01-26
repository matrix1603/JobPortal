package repository.jobApplication;

import entity.jobApplication.JobApplication;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface JobApplicationRepository extends JpaRepository<JobApplication, Integer> {
    List<JobApplication> findByUserEmailId(String emailId, Pageable pageable);

    void deleteByJobId(int id);
}
