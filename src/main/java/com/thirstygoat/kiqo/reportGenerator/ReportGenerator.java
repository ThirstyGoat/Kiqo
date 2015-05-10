package com.thirstygoat.kiqo.reportGenerator;

import com.thirstygoat.kiqo.model.*;
import seng302.group4.utils.ApplicationInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by james, amy on 6/5/15.
 *
 * ReportGenerator provides the functionality to generate reports based on the current model of story #134 - Status Reports.
 *
 * This class will need to be constantly updated upon changing the current model, or adding additional classes that are
 * required in a report.
 */
public final class ReportGenerator {
    private static final String PROJECT_COMMENT = "-   ### Project ###";
    private static final String TEAM_COMMENT = "-   ### Team ###";
    private static final String PERSON_COMMENT = "-   ### Person ###";
    private static final String RELEASE_COMMENT = "-   ### Release ###";
    private static final String SKILL_COMMENT = "-   ### Skill ###";
    private static final int WIDTH = 80;
    private static final int INDENT_SIZE = 4;
    private static final String ALLOCATION_COMMENT = "-   ### Allocation ###";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter titleFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
    private final List<Team> teams;
    private final List<Person> people;
    private final Organisation organisation;
    // Create the header string for the report
    private final String[] title;

    /**
     * Constructor for report generator
     * @param organisation the organisation the report will be generated for
     */
    public ReportGenerator(Organisation organisation) {
        this.organisation = organisation;
        teams = new ArrayList<Team>();
        teams.addAll(organisation.getTeams());
        people = new ArrayList<Person>();
        people.addAll(organisation.getPeople());


        title= (new String[]{
                "Organisation: " + organisation.organisationNameProperty().get(),
                "",
                "Generated: " + LocalDateTime.now().format(titleFormatter),
                "by " + ApplicationInfo.getProperty("name"),
                "Version: " + ApplicationInfo.getProperty("version")});
    }

    /**
     * Generates a string which contains all of the content of the report including the header.
     * @return a string consisting of the report data.
     */
    public String generateReport() {
        final StringBuilder report = new StringBuilder();
        final String reportTitle = HeadingBuilder.makeHeading(title,
                ReportGenerator.WIDTH, HeadingBuilder.Style.JUMBO);

        report.append(ReportUtils.dashes());  // needed to represent the start of the yaml document
        report.append(reportTitle);  // header
        report.append(String.join("\n", generateOrganisationReport(organisation)));  // report content

        return report.toString();
    }

    /**
     * Generates the report data for the organisation.
     * @param organisation the organisation the report will be generated for.
     * @return string consisting of the organisation report data
     */
    private List<String> generateOrganisationReport(Organisation organisation) {
        final List<String> lines = new LinkedList<>();

        // append save location

        lines.add("Save Location: " +
                (organisation.getSaveLocation() == null ? "~" : organisation.getSaveLocation().toString()));

        // append all projects
        lines.add("Projects:");
        for (final Project project:  organisation.getProjects()) {
            lines.add(PROJECT_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateProjectReport(project)));
        }
        // append all skills
        if (organisation.getSkills().size() > 0) {
            lines.add("Skills:");
            for (Skill skill : organisation.getSkills()) {
                lines.add(ReportGenerator.SKILL_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateSkillReport(skill)));
            }
        }

        // append unallocated teams
        if (teams.size() > 0) {
            lines.add("Unallocated Teams: ");
            for (final Team team : new ArrayList<Team>(teams)) {
                lines.add(ReportGenerator.TEAM_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateTeamReport(team)));
            }
        }
        // append unallocated people
        if (people.size() > 0) {
            lines.add("Unallocated People: ");
            for (final Person person : new ArrayList<Person>(people)) {
                lines.add(ReportGenerator.PERSON_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(person)));
            }
        }

        return lines;
    }

    /**
     *  Generate project data including releases and allocations
     */
    private List<String> generateProjectReport(Project project) {
        final List<String> lines = new LinkedList<>();

        lines.add(ReportUtils.formattedLine("Short Name", project.getShortName()));
        lines.add(ReportUtils.formattedLine("Long Name", project.getLongName()));
        lines.add(ReportUtils.formattedLine("Description", null));

        for (Release release : project.getReleases()) {
            lines.add(ReportGenerator.RELEASE_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateReleaseReport(release)));
        }

        // only print teams that are currently allocated
        boolean hasAllocationHeader = false;
        final LocalDate now = LocalDate.now();
        for (final Allocation allocation : project.getAllocations()) {
            if (allocation.getStartDate().isBefore(now) && allocation.getEndDate().isAfter(now)) {
                if (!hasAllocationHeader) {
                    // print list header
                    lines.add("Teams: ");
                    hasAllocationHeader = true;
                }
                final Team team = allocation.getTeam();
                lines.add(ReportGenerator.TEAM_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateTeamReport(team)));
            }
        }

        return lines;
    }

    /**
     *  Generate release data.
     */
    private List<String> generateReleaseReport(Release release) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.formattedLine("Short Name", release.getShortName()));
        lines.add(ReportUtils.formattedLine("Description", release.getDescription()));
        lines.add(ReportUtils.formattedLine("Date", release.getDate().format(formatter)));

        return lines;
    }

    /**
     *  Generate team data including product owner information and current allocation.
     */
    private List<String> generateTeamReport(Team team) {
        teams.remove(team);
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.formattedLine("Short Name", team.getShortName()));

        if (team.getProductOwner() != null) {
            lines.add("Product Owner:");
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(team.getProductOwner())));
        }

        if (team.getScrumMaster() != null) {
            lines.add("Scrum Master:");
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(team.getScrumMaster())));
        }

        if (team.getDevTeam() != null) {
            lines.add("Development Team:");
            for (final Person person : team.getDevTeam()) {
                lines.add(ReportGenerator.PERSON_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(person)));
            }
        }

        if (team.getTeamMembers() != null) {
            lines.add(ReportUtils.formattedLine("Other Team Members", null));
            // First we need to get the set of all team members and remove people who are either product owner,
            // scrum master or in the dev team.
            List<Person> otherTeamMembers = team.getTeamMembers();
            otherTeamMembers.removeIf(p -> p.equals(team.getProductOwner()));
            otherTeamMembers.removeIf(p -> p.equals(team.getScrumMaster()));
            otherTeamMembers.removeAll(team.getDevTeam());
            for (final Person person : otherTeamMembers) {
                lines.add(ReportGenerator.PERSON_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(person)));
            }
        }

        final boolean hasAllocation = false;
        final LocalDate now = LocalDate.now();
        for (final Allocation allocation : team.getAllocations()) {
            if (allocation.getStartDate().isBefore(now) && allocation.getEndDate().isAfter(now)) {
                lines.add(ReportGenerator.ALLOCATION_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateAllocationlReport(allocation)));
            }
        }
        return lines;
    }

    /**
     *  Generate person data including skills.
     */
    private List<String> generatePersonReport(Person person) {
        people.remove(person);
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.formattedLine("Short Name", person.getShortName()));
        lines.add(ReportUtils.formattedLine("Long Name", person.getLongName()));
        lines.add(ReportUtils.formattedLine("Description", person.getDescription()));
        lines.add(ReportUtils.formattedLine("User ID", person.getUserID()));
        lines.add(ReportUtils.formattedLine("Email Address", person.getEmailAddress()));
        lines.add(ReportUtils.formattedLine("Phone Number", person.getPhoneNumber()));
        lines.add(ReportUtils.formattedLine("Department", person.getDepartment()));
        lines.add("Skills:");
        for (Skill skill : person.getSkills()) {
            lines.add("-" + ReportUtils.indent(INDENT_SIZE) + ReportUtils.formattedLine("Short Name", skill.getShortName()));
        }
        return lines;
    }

    /**
     *  Generate skill data.
     */
    private List<String> generateSkillReport(Skill skill) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.formattedLine("shortName", skill.getShortName()));
        lines.add(ReportUtils.formattedLine("description", skill.getDescription()));
        return lines;
    }

    /**
     *  Generate skill data.
     */
    private List<String> generateAllocationlReport(Allocation allocation) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.formattedLine("Current Allocation Project", allocation.getProject().getShortName()));
        lines.add(ReportUtils.formattedLine("Current Allocation Start Date", allocation.getStartDate().format(formatter)));
        lines.add(ReportUtils.formattedLine("Current Allocation End Date", allocation.getEndDate().format(formatter)));
        return lines;
    }
}
