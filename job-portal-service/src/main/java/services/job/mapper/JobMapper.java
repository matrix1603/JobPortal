package services.job.mapper;


import entity.job.Job;
import entity.job.Skills;
import services.job.models.CreateJobRequest;
import services.job.models.UpdateJobRequest;

import java.sql.Date;
import java.util.List;

public class JobMapper {
    public static Job buildJobEntity(CreateJobRequest request, List<Skills> jobSkills) {
        return Job.builder()
                .jobTitle(request.getJobTitle().toUpperCase())
                .company(request.getCompany())
                .numberOfOpenings(request.getNumberOfOpenings())
                .country(request.getCountry())
                .language(request.getLanguage())
                .jobType(request.getJobType())
                .schedule(request.getSchedule())
                .minPay(request.getMinPay())
                .maxPay(request.getMaxPay())
                .supplementalPay(request.getSupplementalPay())
                .benefits(request.getBenefits())
                .jobDescription(request.getJobDescription())
                .isRemote(request.isRemote())
                .location(request.getLocation())
                .skills(jobSkills)
                .jobCategory(request.getJobCategory())
                .jobMode(request.getJobMode())
                .createdDate(new Date(System.currentTimeMillis()))
                .updatedDate(new Date(System.currentTimeMillis()))
                .build();
    }

    public static Job updateExistingJobRecord(UpdateJobRequest updateJobRequest, Job existingRecord, List<Skills> jobSkills) {
        existingRecord.setJobTitle(updateJobRequest.getJobTitle());
        existingRecord.setCompany(updateJobRequest.getCompany());
        existingRecord.setNumberOfOpenings(updateJobRequest.getNumberOfOpenings());
        existingRecord.setCountry(updateJobRequest.getCountry());
        existingRecord.setLanguage(updateJobRequest.getLanguage());
        existingRecord.setJobType(updateJobRequest.getJobType());
        existingRecord.setSchedule(updateJobRequest.getSchedule());
        existingRecord.setMinPay(updateJobRequest.getMinPay());
        existingRecord.setMaxPay(updateJobRequest.getMaxPay());
        existingRecord.setSupplementalPay(updateJobRequest.getSupplementalPay());
        existingRecord.setBenefits(updateJobRequest.getBenefits());
        existingRecord.setJobDescription(updateJobRequest.getJobDescription());
        existingRecord.setRemote(updateJobRequest.isRemote());
        existingRecord.setSkills(jobSkills);
        existingRecord.setJobCategory(updateJobRequest.getJobCategory());
        existingRecord.setJobMode(updateJobRequest.getJobMode());
        existingRecord.setLocation(updateJobRequest.getLocation());
        existingRecord.setUpdatedDate(new Date(System.currentTimeMillis()));
        return existingRecord;
    }


}
