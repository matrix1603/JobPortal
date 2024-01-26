package com.jobportal.repository;

import com.jobportal.ApplicationTestConfiguration;
import entity.job.Skills;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import repository.job.SkillRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
@Import({ApplicationTestConfiguration.class})
public class SkillRepositoryTests {

    @Autowired
    private SkillRepository skillRepository;

    private Skills skill;
    private List<Skills> skillsList = new ArrayList();

    @Before
    public void setupData() {
        skill = Skills.builder().skill("TEST SKILL").build();
        skillsList.add(Skills.builder().skill("TEST SKILL 1").build());
        skillsList.add(Skills.builder().skill("TEST SKILL 2").build());
    }

    @Test
    @DisplayName("TEST: Should save a skill record in DB")
    public void should_SaveSkillRecordInDbTest() {
        Skills savedSkill = skillRepository.save(skill);
        assertNotNull(savedSkill);
        assertNotEquals(0, savedSkill.getId());
    }

    @Test
    @DisplayName("TEST: Find skills from DB in given skills list")
    public void should_GetListOfSkillsForMatchingSkillNames() {
        skillRepository.save(skillsList.get(0));
        skillRepository.save(skillsList.get(1));
        List<String> skillsNameList = skillsList.stream().map(skill -> skill.getSkill()).collect(Collectors.toList());
        List<Skills> savedSkills = skillRepository.findBySkillIn(skillsNameList);
        assertNotNull(savedSkills);
        assertNotEquals(true, savedSkills.isEmpty());
        assertNotEquals(0, savedSkills.size());
    }

    @Test
    @DisplayName("TEST: Should return empty list if no matching skill found for skill names")
    public void should_GetEmptyListOfSkillsIfNoMatchingSkillIsFound() {
        skillRepository.save(skillsList.get(0));
        skillRepository.save(skillsList.get(1));
        List<String> skillsNameList = List.of("NEW SKILL 1", "NEW SKILL 2");
        List<Skills> savedSkills = skillRepository.findBySkillIn(skillsNameList);
        assertEquals(true, savedSkills.isEmpty());
        assertEquals(0, savedSkills.size());
    }


}
