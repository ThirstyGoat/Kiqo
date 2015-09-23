package com.thirstygoat.kiqo.gui.scrumboard;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.scrumBoard.TaskCardViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintViewModel;
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
 * Created by leroy on 23/09/15.
 */
public class ScrumBoardTest {
    private SprintViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Story unreadyStory;
    private Story readyStory;
    private Sprint sprint;
    private Person po;
    private Team team;
    private Task task1;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
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
        backlog.observableStories().add(readyStory);
        backlog.observableStories().add(unreadyStory);
        sprint = new Sprint("sprintGoal", "sprintLongName", "sprintDescription", backlog, release, team,
                        LocalDate.now().minusDays(11), LocalDate.now().minusDays(5), new ArrayList<>());
        task1 = new Task("task1", "the first task", 30f, readyStory);
    }

    @Test
    public void scrumBoard_taskBlockedPropertyTest() {
        // Test that a TaskCards blocked property changes when edited in the ExpandedTaskCard.
        TaskCardViewModel taskCardViewModel = new TaskCardViewModel();
        taskCardViewModel.load(task1, organisation);

        Assert.assertEquals(false, taskCardViewModel.blockedProperty().get());
        Assert.assertEquals(false, task1.isBlocked());

        taskCardViewModel.blockedProperty().set(true);
        UndoManager.getUndoManager().doCommand(taskCardViewModel.getCommand());
        Assert.assertEquals(true, taskCardViewModel.blockedProperty().get());
        Assert.assertEquals(true, task1.isBlocked());

        UndoManager.getUndoManager().undoCommand();
        Assert.assertEquals(false, taskCardViewModel.blockedProperty().get());
        Assert.assertEquals(false, task1.isBlocked());

        UndoManager.getUndoManager().redoCommand();
        Assert.assertEquals(true, taskCardViewModel.blockedProperty().get());
        Assert.assertEquals(true, task1.isBlocked());

        taskCardViewModel.blockedProperty().set(false);
        UndoManager.getUndoManager().doCommand(taskCardViewModel.getCommand());
        Assert.assertEquals(false, taskCardViewModel.blockedProperty().get());
        Assert.assertEquals(false, task1.isBlocked());
    }
}
