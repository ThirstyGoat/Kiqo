package com.thirstygoat.kiqo.reportGenerator;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.ApplicationInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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
    private static final String PROJECT_COMMENT =       " -  ### Project ###";
    private static final String TEAM_COMMENT =          " -  ### Team ###";
    private static final String PERSON_COMMENT =        " -  ### Person ###";
    private static final String RELEASE_COMMENT =       " -  ### Release ###";
    private static final String SKILL_COMMENT =         " -  ### Skill ###";
    private static final String BACKLOG_COMMENT =       " -  ### Backlog ###";
    private static final String STORY_COMMENT =         " -  ### Story ###";
    private static final String ALLOCATION_COMMENT =    " -  ### Allocation ###";
    private static final String AC_COMMENT =            " - ";
    private static final String TASK_COMMENT =          " - ";
    private static final int WIDTH = 80;
    private static final int INDENT_SIZE = 4;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TITLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final List<Team> teams;
    private final List<Person> people;
    private final Organisation organisation;

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
    }

    /**
     * @param type resource from which to retrieve the name
     * @return string representing pluralised class name e.g. "People", "Skills"
     */
    private static String pluraliseClassName(final Class<?> type) {
        final String collectionLabel;
        if (type == Person.class) {
            collectionLabel = "People";
        } else if (type == Story.class) {
            collectionLabel = "Stories";
        } else {
            collectionLabel = type.getSimpleName() + "s";
        }
        return collectionLabel;
    }

    /**
     * Generates a string which contains all of the content of the report including the header.
     * @return a string consisting of the report data.
     */
    public String generateReport() {
        final StringBuilder report = new StringBuilder();
        report.append(ReportUtils.dashes());  // needed to represent the start of the yaml document
        report.append(generateHeader());
        report.append(String.join("\n", generateOrganisationReport(organisation)));  // report content

        return report.toString();
    }

    public String generateReport(Collection<? extends Item> items) {
        final StringBuilder report = new StringBuilder();
        report.append(ReportUtils.dashes());  // needed to represent the start of the yaml document
        report.append(generateHeader());

        final Class<?> type = items.iterator().next().getClass();
        report.append("\n### " + type.getSimpleName().toUpperCase() + " REPORT ###\n" + ReportGenerator.pluraliseClassName(type) + ":");
        for (final Item item : items) {
            report.append("\n\n");
            report.append(String.join("\n", generateItemReport(item)));
        }
        return report.toString();
    }

    /**
     * @return
     */
    private String generateHeader() {
        final String[] title = new String[]{
            "Organisation: " + organisation.organisationNameProperty().get(),
            "",
            "Generated " + LocalDateTime.now().format(ReportGenerator.TITLE_DATE_FORMATTER),
            "by " + ApplicationInfo.getProperty("name") + " " + ApplicationInfo.getProperty("version")
        };
        return HeadingBuilder.makeHeading(title, ReportGenerator.WIDTH, HeadingBuilder.Style.JUMBO);
    }

    private List<String> generateItemReport(Item item) {
        final List<String> lines = new LinkedList<>();
        if (item.getClass() == Project.class) {
            lines.add(ReportGenerator.PROJECT_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateProjectReport((Project) item)));
        } else if (item.getClass() == Team.class) {
            lines.add(ReportGenerator.TEAM_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateTeamReport((Team) item)));
        } else if (item.getClass() == Person.class) {
            lines.add(ReportGenerator.PERSON_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport((Person) item)));
        } else if (item.getClass() == Backlog.class) {
            lines.add(ReportGenerator.BACKLOG_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateBacklogReport((Backlog) item)));
        }
        return lines;
    }

    /**
     * Generates the report data for the organisation.
     * @param organisation the organisation the report will be generated for.
     * @return string consisting of the organisation report data
     */
    private List<String> generateOrganisationReport(Organisation organisation) {
        final List<String> lines = new LinkedList<>();

        // append all projects
        lines.add(ReportUtils.collectionLine("Projects", organisation.getProjects().isEmpty()));
        for (final Project project : organisation.getProjects()) {
            lines.add(ReportGenerator.PROJECT_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateProjectReport(project)));
        }
        // append all skills
        if (organisation.getSkills().size() > 0) {
            lines.add(ReportUtils.collectionLine("Skills", organisation.getSkills().isEmpty()));
            for (final Skill skill : organisation.getSkills()) {
                lines.add(ReportGenerator.SKILL_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateSkillReport(skill)));
            }
        }

        // append unallocated teams
        lines.add(ReportUtils.collectionLine("Unallocated Teams", teams.isEmpty()));
        if (teams.size() > 0) {
            for (final Team team : new ArrayList<Team>(teams)) {
                lines.add(ReportGenerator.TEAM_COMMENT);
                lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateTeamReport(team)));
            }
        }
        // append unallocated people
        if (people.size() > 0) {
            lines.add(ReportUtils.collectionLine("Unallocated People", organisation.getPeople().isEmpty()));
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

        lines.add(ReportUtils.valueLine("Short Name", project.getShortName()));
        lines.add(ReportUtils.valueLine("Name", project.getLongName()));
        lines.add(ReportUtils.valueLine("Description", null));

        // Add backlogs to the report
        lines.add(ReportUtils.collectionLine("Backlogs", project.getBacklogs().isEmpty()));
        for (final Backlog backlog : project.getBacklogs()) {
            lines.add(ReportGenerator.BACKLOG_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateBacklogReport(backlog)));
        }

        // Add unallocated stories that belong to this project to the report
        lines.add(ReportUtils.collectionLine("Unallocated Stories", project.getUnallocatedStories().isEmpty()));
        for(final Story story : project.getUnallocatedStories()) {
            lines.add(ReportGenerator.STORY_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateStoryReport(story)));
        }

        // Add releases associated to this project to the report
        lines.add(ReportUtils.collectionLine("Releases", project.getReleases().isEmpty()));
        for (final Release release : project.getReleases()) {
            lines.add(ReportGenerator.RELEASE_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateReleaseReport(release)));
        }

        // only print teams that are currently allocated
        boolean hasAllocationHeader = false;

        for (final Allocation allocation : project.getCurrentAllocations()) {
            if (!hasAllocationHeader) {
                // print list header
                lines.add(ReportUtils.collectionLine("Currently Allocated Teams", false));
                hasAllocationHeader = true;
            }
            final Team team = allocation.getTeam();
            lines.add(ReportGenerator.TEAM_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateTeamReport(team)));
        }
        if (!hasAllocationHeader) {
            lines.add(ReportUtils.collectionLine("Currently Allocated Teams", true));
        }

        return lines;
    }

    /**
     *  Generate backlog data.
     */
    private List<String> generateBacklogReport(Backlog backlog) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Short Name", backlog.getShortName()));
        lines.add(ReportUtils.valueLine("Name", backlog.getLongName()));
        lines.add(ReportUtils.valueLine("Product Owner", backlog.getProductOwner().getShortName()));
        lines.add(ReportUtils.valueLine("Description", backlog.getDescription()));
        lines.add(ReportUtils.valueLine("Scale", backlog.getScale().toString()));
        lines.add(ReportUtils.collectionLine("Stories", backlog.getStories().isEmpty()));
        for(final Story story : backlog.getStories()) {
            lines.add(ReportGenerator.STORY_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateStoryReport(story)));
        }

        return lines;
    }

    /**
     * Generate story data.
     */
    private List<String> generateStoryReport(Story story) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Short Name", story.getShortName()));
        lines.add(ReportUtils.valueLine("Name", story.getLongName()));
        lines.add(ReportUtils.valueLine("Description", story.getDescription()));
        lines.add(ReportUtils.valueLine("Creator", story.getCreator().getShortName()));
        
        lines.add(ReportUtils.collectionLine("Dependencies", story.getDependencies().isEmpty()));
        for (final Story dependency : story.getDependencies()) {
            lines.add(" - " + dependency.getShortName());
        }
        
        lines.add(ReportUtils.valueLine("Priority", story.getPriority()));
        lines.add(ReportUtils.valueLine("Scale", story.getScale()));
        lines.add(ReportUtils.valueLine("Estimate", story.getEstimate()));
        lines.add(ReportUtils.valueLine("Ready", story.getIsReady()));
        

        // Add unallocated stories that belong to this project to the report
        lines.add(ReportUtils.collectionLine("Acceptance Criteria", story.getAcceptanceCriteria().isEmpty()));
        for (final AcceptanceCriteria acceptanceCriteria : story.getAcceptanceCriteria()) {
            lines.add(ReportGenerator.AC_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateACReport(acceptanceCriteria)));
        }
        lines.add(ReportUtils.collectionLine("Task", story.observableTasks().isEmpty()));
        for (final Task task : story.observableTasks()) {
            lines.add(ReportGenerator.TASK_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateTaskReport(task)));
        }

        return lines;
    }

    /**
     *  Generate acceptance criteria data.
     */
    private List<String> generateACReport(AcceptanceCriteria acceptanceCriteria) {
        final List<String> lines = new ArrayList<String>();
        lines.addAll(ReportUtils.valueLiteral("Criteria", acceptanceCriteria.getCriteria()));
        lines.add(ReportUtils.valueLine("State", acceptanceCriteria.getState()));

        return lines;
    }

    /**
     *  Generate task data.
     */
    private List<String> generateTaskReport(Task tasks) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Short Name", tasks.getShortName()));
        lines.add(ReportUtils.valueLine("Description", tasks.getDescription()));
        lines.add(ReportUtils.valueLine("Estimate", tasks.getEstimate()));
        lines.add(ReportUtils.valueLine("Status", tasks.getStatus()));

        return lines;
    }

    /**
     *  Generate release data.
     */
    private List<String> generateReleaseReport(Release release) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Short Name", release.getShortName()));
        lines.add(ReportUtils.valueLine("Description", release.getDescription()));
        lines.add(ReportUtils.valueLine("Date", release.getDate().format(ReportGenerator.DATE_FORMATTER)));

        return lines;
    }

    /**
     *  Generate team data including product owner information and current allocation.
     */
    private List<String> generateTeamReport(Team team) {
        teams.remove(team);
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Short Name", team.getShortName()));

        final Person productOwner = team.getProductOwner();
        lines.add(ReportUtils.collectionLine("Product Owner", productOwner == null));
        if (productOwner != null) {
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(productOwner)));
        }

        final Person scrumMaster = team.getScrumMaster();
        lines.add(ReportUtils.collectionLine("Scrum Master", scrumMaster == null));
        if (scrumMaster != null) {
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(scrumMaster)));
        }

        lines.add(ReportUtils.collectionLine("Development Team", team.getDevTeam().isEmpty()));
        for (final Person person : team.getDevTeam()) {
            lines.add(ReportGenerator.PERSON_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(person)));
        }

        // find all team members who are not po, sm or in the dev team
        final List<Person> otherTeamMembers = team.getTeamMembers();
        otherTeamMembers.removeIf(p -> p.equals(productOwner));
        otherTeamMembers.removeIf(p -> p.equals(scrumMaster));
        otherTeamMembers.removeAll(team.getDevTeam());
        lines.add(ReportUtils.collectionLine("Other Team Members", otherTeamMembers.isEmpty()));
        for (final Person person : otherTeamMembers) {
            lines.add(ReportGenerator.PERSON_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generatePersonReport(person)));
        }

        final LocalDate today = LocalDate.now();
        final List<Allocation> allocations = team.getAllocations();
        allocations.removeIf(a -> a.getStartDate().isBefore(today) && a.getEndDate().isAfter(today));
        lines.add(ReportUtils.collectionLine("Current Allocation", allocations.isEmpty()));
        for (final Allocation allocation : allocations) {
            lines.add(ReportGenerator.ALLOCATION_COMMENT);
            lines.addAll(ReportUtils.indentArray(ReportGenerator.INDENT_SIZE, generateAllocationlReport(allocation)));
        }

        return lines;
    }

    /**
     *  Generate person data including skills.
     */
    private List<String> generatePersonReport(Person person) {
        people.remove(person);
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Short Name", person.getShortName()));
        lines.add(ReportUtils.valueLine("Name", person.getLongName()));
        lines.add(ReportUtils.valueLine("Description", person.getDescription()));
        lines.add(ReportUtils.valueLine("User ID", person.getUserId()));
        lines.add(ReportUtils.valueLine("Email Address", person.getEmailAddress()));
        lines.add(ReportUtils.valueLine("Phone Number", person.getPhoneNumber()));
        lines.add(ReportUtils.valueLine("Department", person.getDepartment()));
        lines.add(ReportUtils.collectionLine("Skills", person.getSkills().isEmpty()));
        for (final Skill skill : person.getSkills()) {
            lines.add(" -" + ReportUtils.indent(ReportGenerator.INDENT_SIZE) + skill.getShortName());
        }
        return lines;
    }

    /**
     *  Generate skill data.
     */
    private List<String> generateSkillReport(Skill skill) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Short Name", skill.getShortName()));
        lines.add(ReportUtils.valueLine("Description", skill.getDescription()));
        return lines;
    }

    /**
     *  Generate skill data.
     */
    private List<String> generateAllocationlReport(Allocation allocation) {
        final List<String> lines = new ArrayList<String>();
        lines.add(ReportUtils.valueLine("Project", allocation.getProject().getShortName()));
        lines.add(ReportUtils.valueLine("Start Date", allocation.getStartDate().format(ReportGenerator.DATE_FORMATTER)));
        lines.add(ReportUtils.valueLine("End Date", allocation.getEndDate().format(ReportGenerator.DATE_FORMATTER)));
        return lines;
    }
}
