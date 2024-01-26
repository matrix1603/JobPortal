package repository.job;

import entity.job.Skills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skills, Integer> {
    List<Skills> findBySkillIn(List<String> strings);

}
