package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.command.create.CreateEffortCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;


/**
 * Created by james on 10/09/15.
 */
public class CreateEffortCommandTest {
    private CreateEffortCommand command;
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


        command = new CreateEffortCommand(effort, task);
    }

    @Test
    public void testAddToModel() throws Exception {
        Assert.assertFalse(task.getImpediments().contains(effort));

        command.execute();

        Assert.assertTrue("Should have been added to the model", task.getObservableLoggedEffort().contains(effort));
    }

    @Test
    public void testRemoveFromModel() throws Exception {

        Assert.assertFalse("Shouldn't be in the model",task.getObservableLoggedEffort().contains(effort));

        command.execute();

        Assert.assertTrue("Should have been added to the model", task.getObservableLoggedEffort().contains(effort));

        command.undo();

        Assert.assertFalse("Shouldn't be in the model", task.getObservableLoggedEffort().contains(effort));

    }
}
