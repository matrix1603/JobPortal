package repository.job;

import entity.job.Job;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer>, JpaSpecificationExecutor<Job> {

    List<Job> findBySkillsSkillIn(List<String> skills, Pageable pageable);

    List<Job> findByJobMode(String jobMode, Pageable pageable);

}
