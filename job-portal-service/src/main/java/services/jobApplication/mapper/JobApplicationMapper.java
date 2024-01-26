package services.jobApplication.mapper;

import entity.job.Job;
import entity.jobApplication.JobApplication;
import entity.user.User;
import services.jobApplication.models.UserAppliedJobsResponse;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JobApplicationMapper {
    public static JobApplication buildJobApplicationEntity(Job job, User user) {
        return JobApplication.builder()
                .job(job)
                .user(user)
                .createdDate(new Date(System.currentTimeMillis()))
                .build();
    }

    public static List<UserAppliedJobsResponse> buildUserAppliedJobResponse(List<JobApplication> jobApplications) {

        return jobApplications.stream().map(application ->
                UserAppliedJobsResponse.builder()
                        .applicationId(application.getId())
                        .job(application.getJob())
                        .appliedOn(application.getCreatedDate())
                        .build()
        ).collect(Collectors.toList());

    }

}
