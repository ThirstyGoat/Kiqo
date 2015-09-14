package com.thirstygoat.kiqo.gui.backlog;

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
        unreadyStory = new Story("unreadyStory", "", "", po, project, backlog, 666, Scale.FIBONACCI, 333, false, false);
        readyStory = new Story("readyStory", "", "", po, project, backlog, 420, Scale.FIBONACCI, 42, true, false);
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

        backlog.observableStories().add(unreadyStory);
        Assert.assertTrue("Story was added to model, so details pane should have updated",
                viewModel.stories().contains(unreadyStory));

        backlog.observableStories().remove(unreadyStory);
        Assert.assertTrue("Story was removed from model, so details pane should have updated",
                viewModel.stories().isEmpty());
    }
}
