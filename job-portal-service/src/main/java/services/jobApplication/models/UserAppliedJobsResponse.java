package services.jobApplication.models;

import entity.job.Job;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class UserAppliedJobsResponse {
    private int applicationId;
    private Job job;
    private Date appliedOn;
}
