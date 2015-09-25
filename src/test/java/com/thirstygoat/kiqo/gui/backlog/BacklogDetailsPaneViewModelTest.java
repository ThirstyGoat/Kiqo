package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.persistence.PersistenceManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by leroy on 9/08/15.
 */

public class BacklogDetailsPaneViewModelTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private BacklogDetailsPaneViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Story unreadyStory;
    private Story readyStory;
    private Backlog Backlog;
    private Person po;
    private Team team;

    @Before
    public void setup() {
        viewModel = new BacklogDetailsPaneViewModel();
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
    }

    @Test
    public void detailsPaneUpdatesWhenModelChangesTest() {
        viewModel.load(backlog, organisation);
        Assert.assertTrue("Short name should be the same as the model object",
                viewModel.shortNameProperty().get().equals("backlog"));
        backlog.setShortName("testShortNameChanged");
        Assert.assertTrue("The models short name changed, so the view models shortNameProperty should have been updated",
                viewModel.shortNameProperty().get().equals("testShortNameChanged"));
    }

    @Test
    public void detailsPaneUpdatesWhenStoriesChangeTest() {
        viewModel.load(backlog, organisation);
        Assert.assertTrue("Stories should be empty initially",
                viewModel.stories().isEmpty());

        backlog.getStories().add(unreadyStory);
        Assert.assertTrue("Story was added to model, so details pane should have updated",
                viewModel.stories().contains(unreadyStory));

        backlog.getStories().remove(unreadyStory);
        Assert.assertTrue("Story was removed from model, so details pane should have updated",
                viewModel.stories().isEmpty());
    }


    /**
     * Checks that updating the scale of a backlog updates the scale of its stories
     */
    @Test
    public void testUpdateBacklogScaleUpdatesStoriesScale() throws FileNotFoundException {

        Organisation o = PersistenceManager.loadOrganisation(new File(getClass().getResource("/save_files/backlog1.json").getFile()));
        backlog = o.getProjects().get(0).getBacklogs().get(0);
        BacklogDetailsPaneViewModel backlogViewModel = new BacklogDetailsPaneViewModel();
        backlogViewModel.load(backlog, o);

        for (Story story : backlog.getStories()) {
            Assert.assertEquals("Story scale was initialised to Fib", story.getScale(), Scale.FIBONACCI);
        }
        backlogViewModel.scaleProperty().setValue(Scale.DOG_BREEDS);
        backlogViewModel.commitEdit();
        for (Story story : backlog.getStories()) {
            Assert.assertEquals("Story scale should be same as backlog scale", backlog.getScale(), story.getScale());
        }
    }
}
