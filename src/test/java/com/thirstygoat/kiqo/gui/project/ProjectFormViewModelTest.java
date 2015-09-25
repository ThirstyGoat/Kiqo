package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.create.CreateProjectCommand;
import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
* Created by Carina Blair on 21/07/2015.
*/
public class ProjectFormViewModelTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ProjectFormViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Person po;
    private Team team;
    private Story unreadyStory;
    private Story readyStory;

    @Before
    public void setup() {
        viewModel = new ProjectFormViewModel();
        organisation = new Organisation();
        project = new Project("projectShortName", "projectLongName");
        po = new Person("PO", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        backlog = new Backlog("backlog", "blLongName", "blDescription", po, project, new ArrayList<>(), Scale.FIBONACCI);
        project.observableBacklogs().add(backlog);
        release = new Release("releaseShortName", project, LocalDate.now(), "releaseDescription");
        team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(po);
        project.observableReleases().add(release);
        unreadyStory = new Story("unreadyStory", "", "", po, project, backlog, 666, Scale.FIBONACCI, 333, false, false, null);
        readyStory = new Story("readyStory", "", "", po, project, backlog, 420, Scale.FIBONACCI, 42, true, false, null);
        project.getUnallocatedStories().add(unreadyStory);
        project.getUnallocatedStories().add(readyStory);

        viewModel.setExitStrategy(() -> {}); // not running in a JavaFX thread so no special exit action needed
    }

    /**
     * Populate a ProjectFormViewModel's fields with valid data.
     * @param viewModel
     */
    public void populateFields(ProjectFormViewModel viewModel) {
        viewModel.shortNameProperty().set("blahBlahBlah");
        viewModel.longNameProperty().set("projectLongName");
    }

    @Test
    public void newProject_DoNothing_CommandTest() {
        viewModel.load(null, organisation);
        Assert.assertTrue("Command should be null if nothing was done",
                viewModel.getCommand() == null);
    }

    @Test
    public void newProject_ValidFieldsTest() {
        viewModel.load(null, organisation);
        populateFields(viewModel);
        String errorMessages = viewModel.allValidation().getErrorMessages().toString();
        Assert.assertTrue("Fields should all be valid:\n" + errorMessages,
                viewModel.allValidation().isValid());
        Command command = viewModel.getCommand();
        Assert.assertTrue("Command should not be null if all fields are valid",
                command != null);
        Assert.assertTrue("Command should be of type CreateProjectCommand",
                command.getClass().equals(CreateProjectCommand.class));
    }

    @Test
    public void existingProject_EditingShortNameToBeSameAsSelfTest() {
        Project project = new Project("aProject", "aProjectLongName");
        CreateProjectCommand command = new CreateProjectCommand(project, organisation);
        command.execute();

        viewModel.load(null, organisation);
        viewModel.shortNameProperty().set("aProject");

        Assert.assertFalse("The project shortName should NOT be valid when a project which has the same name exists",
                viewModel.shortNameValidation().isValid());

        ProjectFormViewModel editingViewModel = new ProjectFormViewModel();
        editingViewModel.load(project, organisation);
        viewModel.shortNameProperty().set("aProject");
        Assert.assertTrue("Editing the project should allow its existing shortName to be set as shortName",
                editingViewModel.shortNameValidation().isValid());
    }
}
