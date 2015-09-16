package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
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
 * Created by leroy on 15/9/15.
 */
public class TeamDetailsPaneViewModelTest {
    private TeamDetailsPaneViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Person po;
    private Person sm;
    private Person dev1;
    private Person dev2;
    private Person person3;
    private Team team;

    @Before
    public void setup() {
        viewModel = new TeamDetailsPaneViewModel();
        organisation = new Organisation();
        project = new Project("projectShortName", "projectLongName");
        po = new Person("PO", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        sm = new Person("SM", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        dev1 = new Person("DEV1", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        dev2 = new Person("DEV2", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        person3 = new Person("PERSON3", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(po);
        organisation.getPeople().add(sm);
        organisation.getPeople().add(dev1);
        organisation.getPeople().add(dev2);
        organisation.getPeople().add(person3);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void detailsPaneUpdatesWhenModelChangesTest() {
        viewModel.load(team, organisation);
        Assert.assertTrue("Short name should be the same as the model object",
                        viewModel.shortNameProperty().get().equals("teamShortName"));
        team.setShortName("testShortNameChanged");
        Assert.assertTrue(
                        "The models short name changed, so the ViewModel's shortNameProperty should have been updated",
                        viewModel.shortNameProperty().get().equals("testShortNameChanged"));

        Assert.assertEquals("Team has no members so there should be no ListItemViewModels",
                        viewModel.teamMemberViewModels().size(), 0);
        team.observableTeamMembers().add(person3);
        Assert.assertEquals(
                        "A person was added to the team, so the ViewModel's teamMemberViewModelsProperty should have"
                                        + "been updated.", viewModel.teamMemberViewModels().size(), 1);
    }

    @Test
    public void detailsPaneUpdatesWhenTeamMembersChangeTest() {
        team.observableTeamMembers().add(person3);
        viewModel.load(team, organisation);
        Assert.assertEquals(viewModel.teamMemberViewModels().get(0).shortNameProperty().get(), "PERSON3");
        Assert.assertEquals(viewModel.teamMemberViewModels().get(0).descriptionProperty().get(), "");

        // edit person
        person3.shortNameProperty().set("a different name");
        person3.descriptionProperty().set("some description");
        Assert.assertEquals(viewModel.teamMemberViewModels().get(0).shortNameProperty().get(), "a different name");
        Assert.assertEquals(viewModel.teamMemberViewModels().get(0).descriptionProperty().get(), "some description");
    }
}
