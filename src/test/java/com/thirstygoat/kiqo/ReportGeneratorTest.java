package com.thirstygoat.kiqo;


import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.thirstygoat.kiqo.reportGenerator.ReportGenerator;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by Carina on 3/05/2015
 */

public class ReportGeneratorTest{
    private Organisation organisation = new Organisation();
    private Project project = new Project("proj1", "Project 1");
    private Skill skill = new Skill("shortname", "description");
    private Person person = new Person();
    private Team team = new Team("team1", "", new ArrayList<>());
    private Release release = new Release("release short name", project, LocalDate.now().minusDays(1),
            "Description text");
    private Allocation allocation = new Allocation(team, LocalDate.of(2013, 5, 5), LocalDate.of(2015, 5, 5), project);





    @Before
    public void setUp() {
        person.setShortName("shortname");
        person.setLongName("longname");
        person.setDescription("des");
        person.setUserID("123456asdf");
        person.setEmailAddress("asd@asd.com");
        person.setDepartment("awesome department");
        person.getSkills().add(skill);

        organisation.getProjects().add(project);
        organisation.getPeople().add(person);
        organisation.getSkills().addAll(skill);
        project.getReleases().add(release);
        organisation.getTeams().add(team);
        project.getAllocations().add(allocation);
        team.getAllocations().add(allocation);
        team.getTeamMembers().add(person);
        team.setProductOwner(person);
    }

    /**
     * Tests to make sure the report generator output is not null.
     * The output of the report may change with future versions, therefore more specific testing is not beneficial
     * at this time.
     */
    @Test
    public void testGenerateReport() {
        ReportGenerator report = new ReportGenerator(organisation);
        report.generateReport();
        Assert.assertNotNull(report.toString());
    }
}