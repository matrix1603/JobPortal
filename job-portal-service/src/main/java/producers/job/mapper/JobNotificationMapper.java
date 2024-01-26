package producers.job.mapper;

import entity.job.Job;
import entity.jobApplication.JobApplication;
import producers.job.models.JobApplicationCreatedNotification;
import producers.job.models.JobCreatedNotification;

public class JobNotificationMapper {
    public static JobCreatedNotification getJobCreatedNotification(Job job) {
        return JobCreatedNotification.builder()
                .jobId(job.getId())
                .jobTitle(job.getJobTitle())
                .message("JOB CREATED SUCCESSFULLY")
                .build();
    }

    public static JobApplicationCreatedNotification getJobApplicationCreatedNotification(JobApplication jobApplication) {
        return JobApplicationCreatedNotification.builder()
                .jobId(jobApplication.getJob().getId())
                .jobTitle(jobApplication.getJob().getJobTitle())
                .companyName(jobApplication.getJob().getCompany())
                .userEmail(jobApplication.getUser().getEmailId())
                .message(String.format("Job application record created for Job Post : %s by email id : %s",
                        jobApplication.getJob().getJobTitle(),
                        jobApplication.getUser().getEmailId()))
                .build();
    }
}
