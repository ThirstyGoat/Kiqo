package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.command.delete.DeleteImpedimentCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Impediment;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;


/**
 * Created by james on 10/09/15.
 */
public class DeleteImpedimentCommandTest {
    private DeleteImpedimentCommand deleteImpedimentCommand;
    private MainController mainController = new MainController();
    private Organisation organisation;
    private Task task;
    private Impediment impediment;

    @Before
    public void setup() {
        mainController.openOrganisation(new File("demo.json"));
        organisation = mainController.selectedOrganisationProperty.get();
        task = organisation.getProjects().get(0).getBacklogs().get(0).getStories().get(0).getTasks().get(0);
        impediment = new Impediment("some impediment", true);

        task.getImpediments().add(impediment);

        deleteImpedimentCommand = new DeleteImpedimentCommand(impediment, task);
    }

    @Test
    public void testRemoveFromModel() throws Exception {
        Assert.assertTrue("should have been added to the model", task.getImpediments().contains(impediment));

        deleteImpedimentCommand.execute();

        Assert.assertFalse("Should have been removed to the model", task.getImpediments().contains(impediment));
    }

    @Test
    public void testAddToModel() throws Exception {

        Assert.assertTrue("should have been added to the model", task.getImpediments().contains(impediment));

        deleteImpedimentCommand.execute();

        Assert.assertFalse("Should have been removed from the model", task.getImpediments().contains(impediment));

        deleteImpedimentCommand.undo();

        Assert.assertTrue("Should be in the model", task.getImpediments().contains(impediment));

    }
}
