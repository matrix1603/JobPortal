package services.job.jobService;

import entity.job.Job;
import services.job.models.CreateJobRequest;
import services.job.models.JobBySkillsRequest;
import services.job.models.UpdateJobRequest;
import models.response.ServiceResponse;

import java.util.List;

public interface JobService {

    public ServiceResponse<Job> createJob(CreateJobRequest createJobRequest);

    public ServiceResponse<List<Job>> searchByTitle(String title, int pageNo);

    public ServiceResponse<List<Job>> searchByLocation(String location, int pageNo);

    public ServiceResponse<String> deleteJobById(int id);

    public ServiceResponse<Job> updateJobRecord(UpdateJobRequest updateJobRequest, int jobId);

    public ServiceResponse<List<Job>> searchJobBySkills(JobBySkillsRequest skills, int pageNo);

    ServiceResponse<List<Job>> searchJobByJobMode(String jobMode, int pageNo);
}
