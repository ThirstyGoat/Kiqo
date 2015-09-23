package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.command.delete.DeleteEffortCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by james on 17/09/15.
 */
public class DeleteEffortCommandTest {
    private DeleteEffortCommand command;
    private MainController mainController = new MainController();
    private Organisation organisation;
    private Task task;
    private Effort effort;

    @Before
    public void setup() {
        mainController.openOrganisation(new File("demo.json"));
        organisation = mainController.selectedOrganisationProperty.get();
        task = organisation.getProjects().get(0).getBacklogs().get(0).getStories().get(0).getTasks().get(0);
        effort = new Effort();

        task.getObservableLoggedEffort().add(effort);

        command = new DeleteEffortCommand(effort, task);
    }

    @Test
    public void testRemoveFromModel() throws Exception {
        Assert.assertTrue("should have been added to the model", task.getObservableLoggedEffort().contains(effort));

        command.execute();

        Assert.assertFalse("Should have been removed to the model", task.getObservableLoggedEffort().contains(effort));
    }

    @Test
    public void testAddToModel() throws Exception {

        Assert.assertTrue("should have been added to the model", task.getObservableLoggedEffort().contains(effort));

        command.execute();

        Assert.assertFalse("Should have been removed from the model", task.getObservableLoggedEffort().contains(effort));

        command.undo();

        Assert.assertTrue("Should be in the model", task.getObservableLoggedEffort().contains(effort));

    }
}
