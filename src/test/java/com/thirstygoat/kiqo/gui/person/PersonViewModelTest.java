package com.thirstygoat.kiqo.gui.person;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.create.CreatePersonCommand;
import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.*;
/**
 * Created by leroy on 21/09/15.
 */
public class PersonViewModelTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private PersonViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Person person;
    private Team team;
    private Story unreadyStory;
    private Story readyStory;
    private Skill skill1;
    private Skill skill2;

    @Before
    public void setup() {
        viewModel = new PersonViewModel();
        organisation = new Organisation();
        project = new Project("projectShortName", "projectLongName");
        person = new Person("existingPerson", "", "", "", "", "", "", Arrays.asList());
        backlog = new Backlog("backlog", "blLongName", "blDescription", person, project, new ArrayList<>(), Scale.FIBONACCI);
        project.observableBacklogs().add(backlog);
        release = new Release("releaseShortName", project, LocalDate.now(), "releaseDescription");
        team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(person);
        project.observableReleases().add(release);
        unreadyStory = new Story("unreadyStory", "", "", person, project, backlog, 666, Scale.FIBONACCI, 333, false, false, null);
        readyStory = new Story("readyStory", "", "", person, project, backlog, 420, Scale.FIBONACCI, 42, true, false, null);
        project.getUnallocatedStories().add(unreadyStory);
        project.getUnallocatedStories().add(readyStory);
        skill1 = new Skill("skill1", "");
        skill2 = new Skill("skill2", "");
        organisation.getSkills().add(skill1);
        organisation.getSkills().add(skill2);
    }

    /**
     * Populate a PersonViewModel's fields with valid data.
     * @param viewModel
     */
    private void populateFields(PersonViewModel viewModel) {
        viewModel.shortNameProperty().set("personShortName");
        viewModel.longNameProperty().set("personLongName");
    }

    @Test
    public void newPerson_doNothingCommandTest() {
        viewModel.load(null, organisation);
        Assert.assertEquals("Command should be null if nothing was done", null, viewModel.getCommand());
    }

    @Test
    public void newPerson_validFieldsCommandTest() {
        viewModel.load(null, organisation);
        populateFields(viewModel);
        String errorMessages = viewModel.allValidation().getErrorMessages().toString();
        Assert.assertTrue("Fields should all be valid:\n" + errorMessages,
                        viewModel.allValidation().isValid());
        Command command = viewModel.getCommand();
        Assert.assertTrue("Command should not be null if all fields are valid", command != null);
        Assert.assertEquals("Command should be of type CreatePersonCommand",
                        command.getClass(), (CreatePersonCommand.class));
        command.execute();
    }

    @Test
    public void existingPerson_editingShortNameToBeSameAsSelfTest() {

        viewModel.load(null, organisation);
        viewModel.shortNameProperty().set("existingPerson");

        Assert.assertEquals("The person short name should NOT be valid when it is the same as another persons shortName",
                        false, viewModel.shortNameValidation().isValid());

        PersonViewModel editingViewModel = new PersonViewModel();
        editingViewModel.load(person, organisation);
        viewModel.shortNameProperty().set("existingPerson");
        Assert.assertTrue("Editing the person should allow the same short name to be set as itself",
                        editingViewModel.shortNameValidation().isValid());
    }

    @Test
    public void existingPerson_settingSkillsTest() {
        viewModel.load(person, organisation);
        viewModel.skills().add(skill1);
        UndoManager.getUndoManager().doCommand(viewModel.getCommand());

        Assert.assertTrue("story should contain a skill", person.observableSkills().contains(skill1));
    }

    @Test
    public void existingPerson_existingSkillsLoadTest() {
        person.observableSkills().add(skill1);
        person.observableSkills().add(skill2);
        viewModel.load(person, organisation);
        Assert.assertEquals("ViewModel skills property should contain two items", viewModel.skills().size(), 2);
    }

    @Test
    public void existingPerson_skillChangeCommandTest() {
        viewModel.load(person, organisation);
        Assert.assertEquals(false, viewModel.skills().contains(skill1));

        // Add skill to Model via ViewModel
        viewModel.skills().add(skill1);
        UndoManager.getUndoManager().doCommand(viewModel.getCommand());
        Assert.assertEquals(true, person.observableSkills().contains(skill1));

        // Remove skill using undo command. Should update both the Model and ViewModel
        UndoManager.getUndoManager().undoCommand();
        Assert.assertEquals(false, person.observableSkills().contains(skill1));
        Assert.assertEquals(false, viewModel.skills().contains(skill1));

        // Reinstate skill using redo command. Should update both the Model and ViewModel
        UndoManager.getUndoManager().redoCommand();
        Assert.assertEquals(true, person.observableSkills().contains(skill1));
        Assert.assertEquals(true, viewModel.skills().contains(skill1));
    }
    
    @Test
    public void eligibleSkillsTest() {
    	assertThat(viewModel.eligibleSkills()).isEmpty();
    	
    	viewModel.load(person, organisation);
    	assertThat(viewModel.organisationProperty().get()).isNotNull(); // just in case
    	assertThat(viewModel.eligibleSkills()).hasSameElementsAs(organisation.getSkills());
    	
    	// add new skill to organisation
    	final Skill skill3 = new Skill("skill3", "desc");
		organisation.getSkills().add(skill3);
    	assertThat(viewModel.eligibleSkills()).contains(skill3);
    }
}
