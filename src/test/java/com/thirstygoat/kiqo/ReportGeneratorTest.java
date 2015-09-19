package com.thirstygoat.kiqo;


import com.thirstygoat.kiqo.command.create.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.reportGenerator.ReportGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by Carina on 3/05/2015, Edited by James on 6//08/15
 */

public class ReportGeneratorTest{
    private Organisation organisation = new Organisation(true);
    private Project project = new Project("proj1", "Project 1", "Project description");
    private Release release = new Release("release short name", project, LocalDate.now().minusDays(1),
            "Description text");
    private Skill skill = new Skill("shortname", "description");
    private Person person = new Person("shortname", "longname", "description", "userid", "123@qwe.com", "0123456789", "department", Arrays.asList(skill, organisation.getPoSkill(), organisation.getSmSkill()));
    private Person person2 = new Person("shortname2", "longname2", "description2", "userid2", "123@qwe.com2", "01234567892", "department2", Arrays.asList(skill, organisation.getPoSkill(), organisation.getSmSkill()));
    private Person person3 = new Person("shortname3", "longname3", "description3", "userid3", "123@qwe.com3", "01234567893", "department3", Arrays.asList(skill));
    private Person person4 = new Person("shortname4", "longname4", "description4", "userid4", "123@qwe.com4", "01234567894", "department4", Arrays.asList(skill));
    private Story story;
    private Story story2;
    private Story story3;
    private Backlog backlog;
    private Team team = new Team("team1", "description", Arrays.asList(person, person2, person3, person4));
    private Sprint sprint = new Sprint("Goal", "longname", "Sprint description", null, release, team, LocalDate.now().minusDays(10), LocalDate.now().minusDays(1), new ArrayList<>() );
    private Allocation allocation;
    private AcceptanceCriteria acceptanceCriteria = new AcceptanceCriteria("ac text", story);
    private Task task = new Task("Shortname task", "description task", (float) 5, story);
    private Impediment impediment = new Impediment("Some impediment", true);

    @Before
    public void setUp() {

        /*
         * To all those that look at the code below, forgive me
         */

        CreateProjectCommand createProjectCommand = new CreateProjectCommand(project, organisation);
        createProjectCommand.execute();
        CreateSkillCommand createSkillCommand = new CreateSkillCommand(skill, organisation);
        createSkillCommand.execute();
        CreatePersonCommand createPersonCommand = new CreatePersonCommand(person, organisation);
        createPersonCommand.execute();
        CreatePersonCommand createPersonCommand2 = new CreatePersonCommand(person2, organisation);
        createPersonCommand2.execute();

        CreateTeamCommand createTeamCommand = new CreateTeamCommand(team, organisation);
        createTeamCommand.execute();

        allocation = new Allocation(organisation.getTeams().get(0), LocalDate.of(2013, 5, 5), LocalDate.now().plusDays(1), organisation.getProjects().get(0));

        CreateAllocationCommand createAllocationCommand = new CreateAllocationCommand(allocation);
        createAllocationCommand.execute();

        story = new Story("shortname", "longname", "description", person, organisation.getProjects().get(0), null, 1, Scale.FIBONACCI, 1, true, false);
        CreateStoryCommand createStoryCommand = new CreateStoryCommand(story);
        createStoryCommand.execute();

        story2 = new Story("shortname2", "longname2", "description2", person, organisation.getProjects().get(0), null, 2, Scale.FIBONACCI, 2, true, false);
        CreateStoryCommand createStoryCommand2 = new CreateStoryCommand(story2);
        createStoryCommand2.execute();

        story3 = new Story("shortname3", "longname3", "description3", person, organisation.getProjects().get(0), null, 3, Scale.FIBONACCI, 3, true, false);
        CreateStoryCommand createStoryCommand3 = new CreateStoryCommand(story3);
        createStoryCommand3.execute();

        CreateAcceptanceCriteriaCommand acceptanceCriteriaCommand = new CreateAcceptanceCriteriaCommand(acceptanceCriteria, story);
        acceptanceCriteriaCommand.execute();

        CreateTaskCommand createTaskCommand = new CreateTaskCommand(task, story);
        createTaskCommand.execute();

        backlog = new Backlog("shortname", "longname", "description", person, organisation.getProjects().get(0), Arrays.asList(story, story2), Scale.FIBONACCI);
        CreateBacklogCommand createBacklogCommand = new CreateBacklogCommand(backlog);
        createBacklogCommand.execute();

        CreateReleaseCommand createReleaseCommand = new CreateReleaseCommand(release);
        createReleaseCommand.execute();

        story.setBacklog(backlog);

        sprint.getRelease().getSprints().add(sprint);

        sprint.getStories().addAll(story, story2);
        sprint.setBacklog(backlog);

        CreateImpedimentCommand createImpedimentCommand = new CreateImpedimentCommand(impediment, task);
        createImpedimentCommand.execute();


    }

    /**
     * Sends a query to yaml-online-parser.appspot.com and check to see if the output is valid.
     * @param reportStr
     * @return the reponse from the website
     * @throws IOException if the website is unreachable (no internet connection), if you have an internet connection
     * try running the yaml-online-parser.appspot.com through http://downforeveryoneorjustme.com/
     */
    public Boolean reportTestHelper(String reportStr) throws IOException {
        String url = "http://yaml-online-parser.appspot.com/ajax?";
        StringBuilder postData = new StringBuilder();
        StringBuilder response = new StringBuilder();

        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            Map<String,Object> params = new LinkedHashMap<>();
            params.put("yaml", reportStr);
            params.put("type", "json");

            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);

            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            for ( int c = in.read(); c != -1; c = in.read() ) {
                response.append(((char)c));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        return response.toString().matches("\"ERROR.*");
    }


    /**
     *  Test to see if the person report is valid yaml
     */
    @Test
    public void testGeneratePersonReport() {
        ReportGenerator report = new ReportGenerator(organisation);
        String reportStr = report.generateReport(organisation.getPeople());
        Boolean response = false;
        try {
            response = reportTestHelper(reportStr);
        } catch (IOException e) {
            Assert.fail("Check your internet connection");
        }
        Assert.assertFalse("return string should not contain \"ERROR\"", response);
    }


    /**
     *  Test to see if the person report is valid yaml
     */
    @Test
    public void testGenerateTeamReport() {
        ReportGenerator report = new ReportGenerator(organisation);
        String reportStr = report.generateReport(organisation.getTeams());
        Boolean response = false;
        try {
            response = reportTestHelper(reportStr);
        } catch (IOException e) {
            Assert.fail("Check your internet connection");
        }
        Assert.assertFalse("return string should not contain \"ERROR\"", response);
    }


    /**
     *  Test to see if the backlog report is valid yaml
     */
    @Test
    public void testGenerateBacklogReport() {
        ReportGenerator report = new ReportGenerator(organisation);
        String reportStr = report.generateReport(organisation.getProjects().get(0).getBacklogs());
        Boolean response = false;
        try {
            response = reportTestHelper(reportStr);
        } catch (IOException e) {
            Assert.fail("Check your internet connection");
        }
        Assert.assertFalse("return string should not contain \"ERROR\"", response);
    }


    /**
     *  Test to see if the project report is valid yaml
     */
    @Test
    public void testGenerateProjectReport() {
        ReportGenerator report = new ReportGenerator(organisation);
        String reportStr = report.generateReport(organisation.getProjects());
        Boolean response = false;
        try {
            response = reportTestHelper(reportStr);
        } catch (IOException e) {
            Assert.fail("Check your internet connection");
        }
        Assert.assertFalse("return string should not contain \"ERROR\"", response);
    }

    /**
     *  Test to see if the organisation report is valid yaml
     */
    @Test
    public void testGenerateOrganisationReport() {
        ReportGenerator report = new ReportGenerator(organisation);
        String reportStr = report.generateReport();
        Boolean response = false;
        try {
            response = reportTestHelper(reportStr);
        } catch (IOException e) {
            Assert.fail("Check your internet connection");
        }
        Assert.assertFalse("return string should not contain \"ERROR\"", response);
    }
}