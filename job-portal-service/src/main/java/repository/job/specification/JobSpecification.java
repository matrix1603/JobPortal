package repository.job.specification;

import entity.job.Job;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecification {

    private JobSpecification() {
    }

    ;

    public static Specification<Job> titleLike(String title) {
        return (root, query, builder) -> builder.like(root.get("jobTitle"), "%" + title + "%");
    }

    public static Specification<Job> locationLike(String location) {
        return ((root, query, builder) -> builder.like(root.get("location"), "%" + location + "%"));
    }

}
