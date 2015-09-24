package com.thirstygoat.kiqo.gui.team;

import java.util.*;

import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.assertj.core.api.Assertions.*;

import com.thirstygoat.kiqo.model.*;

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
        
    	viewModel.load(team, organisation);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void testDevSelection() {
    	viewModel.teamMembersProperty().addAll(po, sm, dev1, dev2, person3);
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(po, sm, dev1, dev2, person3);
    	viewModel.productOwnerProperty().set(po);
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(sm, dev1, dev2, person3);
    	viewModel.productOwnerProperty().set(null); // ex-PO should be allowed back into dev
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(po, sm, dev1, dev2, person3);
    	viewModel.productOwnerProperty().set(po); // setup for later tests
    	
    	viewModel.scrumMasterProperty().set(sm);
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(dev1, dev2, person3);
    	
    	viewModel.devTeamProperty().addAll(dev1, dev2);
    	assertThat(viewModel.eligibleDevs().get()).containsExactly(dev1, dev2, person3);
    }
}
