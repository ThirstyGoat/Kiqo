package com.thirstygoat.kiqo.gui.team;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;
import java.util.function.Supplier;

import org.junit.*;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

public class TeamViewModelTest {
    TeamViewModel viewModel;
    Organisation organisation;
    
    @Before
    public void setUp() throws Exception {
        viewModel = new TeamViewModel();
        organisation = new Organisation(true);
        viewModel.load(null, organisation);
    }

    @Test
    public void testShortNameValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                viewModel.shortNameValidation().validProperty().get());
        
        viewModel.shortNameProperty().set(null);
        Assert.assertFalse("Must not be null.",
                viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("This name is likely to be longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.");
        Assert.assertFalse("Must not be longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.",
                viewModel.shortNameValidation().validProperty().get());

        // validating uniqueness within organisation requires model data
        final String SHARED_NAME = "not unique";
        Team secondTeam = new Team(SHARED_NAME, "arbitrary description", new ArrayList<>());
        organisation.getTeams().add(secondTeam);
        
        viewModel.shortNameProperty().set("unique");
        Assert.assertTrue("Unique name must be valid.", viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set(SHARED_NAME);
        Assert.assertEquals(viewModel.shortNameProperty().get(), secondTeam.shortNameProperty().get());
        Assert.assertFalse("Non-unique name must be invalid.", viewModel.shortNameValidation().validProperty().get());
    }

    @Test
    public void testDescriptionValidation() {
        Assert.assertTrue("Must be valid initially.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("");
        Assert.assertTrue("Empty string should be recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());
    }

    @Test
    public void testAllValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.allValidation().validProperty().get());

        viewModel.descriptionProperty().set("Billy Goat");
        Assert.assertFalse("Must not be valid with partial validity.",
                viewModel.allValidation().validProperty().get());

        viewModel.shortNameProperty().set("Name");
        // TODO set others
        Assert.assertTrue("Must be valid when all contributors are valid.",
                viewModel.allValidation().validProperty().get());
    }
    
    @Test
    public void testProductOwnerSupplier() {
        Supplier<List<Person>> supplier = viewModel.productOwnerSupplier();
        Person person = new Person();
        organisation.getPeople().add(person);
        viewModel.teamMembersProperty().add(person);
        
        Assert.assertFalse("Person without PO skill is ineligible.", supplier.get().contains(person));

        person.observableSkills().add(organisation.getPoSkill());
        Assert.assertTrue("Person with PO skill is eligible.", supplier.get().contains(person));
        
        viewModel.scrumMasterProperty().set(person);
        Assert.assertFalse("Person with SM role is ineligible.", supplier.get().contains(person));
        viewModel.scrumMasterProperty().set(null);
        
        viewModel.devTeamProperty().get().add(person);
        Assert.assertFalse("Person with dev role is ineligible.", supplier.get().contains(person));
        viewModel.devTeamProperty().get().remove(person);
        
        viewModel.productOwnerProperty().set(person);
        Assert.assertTrue("Person already in PO role is eligible.", supplier.get().contains(person));
    }
    
    @Test
    public void testDevSelection() {
        Project project = new Project("projectShortName", "projectLongName");
    	Person po = new Person("PO", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person sm = new Person("SM", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person dev1 = new Person("DEV1", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person dev2 = new Person("DEV2", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person person3 = new Person("PERSON3", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Team team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(po);
        organisation.getPeople().add(sm);
        organisation.getPeople().add(dev1);
        organisation.getPeople().add(dev2);
        organisation.getPeople().add(person3);
        
    	viewModel.load(team, organisation);
    	assertThat(viewModel.eligibleDevs().get()).isEmpty();
    	viewModel.teamMembersProperty().addAll(po, sm, dev1, dev2, person3);
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(po, sm, dev1, dev2, person3);
    	viewModel.productOwnerProperty().set(po);
    	assertThat(viewModel.eligibleDevs().get()).doesNotContain(po);
    	viewModel.productOwnerProperty().set(null); // ex-PO should be allowed back into dev
    	assertThat(viewModel.eligibleDevs().get()).contains(po);
    	viewModel.productOwnerProperty().set(po); // setup for later tests
    	
    	viewModel.scrumMasterProperty().set(sm); // now po and sm are both set
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(dev1, dev2, person3);
    	
    	viewModel.devTeamProperty().addAll(dev1, dev2);
    	 // still eligible when selected
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(dev1, dev2, person3);
    }
    
    @Test
    public void testTeamMembersSelection() {
        Project project = new Project("projectShortName", "projectLongName");
    	Person po = new Person("PO", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person sm = new Person("SM", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person dev1 = new Person("DEV1", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person dev2 = new Person("DEV2", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Person person3 = new Person("PERSON3", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        Team team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(po);
        organisation.getPeople().add(sm);
        organisation.getPeople().add(dev1);
        organisation.getPeople().add(dev2);
        organisation.getPeople().add(person3);
        
        TeamDetailsPaneViewModel detailsPaneViewModel = new TeamDetailsPaneViewModel(); // because commit to model
        detailsPaneViewModel.load(team, organisation);
        // all people in organisation are eligible
    	assertThat(detailsPaneViewModel.eligibleTeamMembers().get()).containsExactly(po, sm, dev1, dev2, person3);
    	
    	// regardless of membership in current team
    	detailsPaneViewModel.teamMembersProperty().addAll(po, sm, dev1, dev2, person3);
    	assertThat(detailsPaneViewModel.eligibleTeamMembers().get()).containsExactly(po, sm, dev1, dev2, person3);
    	detailsPaneViewModel.teamMembersProperty().removeAll(po, sm, dev1);
    	assertThat(detailsPaneViewModel.eligibleTeamMembers().get()).containsExactly(po, sm, dev1, dev2, person3);
    	
    	// and adding/removing teamMembers in model has no effect
    	UndoManager.getUndoManager().doCommand(viewModel.getCommand());
    	assertThat(detailsPaneViewModel.eligibleTeamMembers().get()).containsExactly(po, sm, dev1, dev2, person3);
    	
    	// but... people in a different team in the model are excluded
    	Team team2 = new Team("another team", "pancakes", new ArrayList<>());
        organisation.getTeams().add(team2);
    	team2.observableTeamMembers().add(po);
    	po.setTeam(team2);
    	assertThat(detailsPaneViewModel.eligibleTeamMembers().get()).doesNotContain(po);
    }
}
