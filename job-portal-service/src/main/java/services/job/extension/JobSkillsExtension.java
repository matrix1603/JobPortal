package services.job.extension;

import entity.job.Skills;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JobSkillsExtension {
    public static List<Skills> getSkillEntityList(List<String> requestSkillNames, List<Skills> jobSkills) {

        // GETTING SAVED JOB SKILLS NAMES IN LOWERCASE
        Set<String> savedJobSkillNames = jobSkills.stream()
                .map(Skills::getSkill)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Create new skill records for skills not already present in the DB
        List<Skills> newSkillRecords = requestSkillNames.stream()

                // BELOW CONDITION CHECKING IF REQUESTED JOB SKILL IS NOT PRESENT IN SKILLS THAT WE GOT FROM DATABASE
                .filter(skill -> !savedJobSkillNames.contains(skill.toLowerCase()))
                .map(skill -> Skills.builder().skill(skill.toLowerCase()).build())
                .collect(Collectors.toList());

        // Add new skill records to existing skill records
        if (!newSkillRecords.isEmpty())
            jobSkills.addAll(newSkillRecords);

        return jobSkills;
    }
}
