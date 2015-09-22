package com.thirstygoat.kiqo;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneView;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneViewModel;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.persistence.PersistenceManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by leroy on 22/09/15.
 */
public class DemoFileTest {
    private Organisation organisation;

    @Before
    public void setUp() throws FileNotFoundException {
        final File f = new File(getClass().getResource("/save_files/demo.json").getFile());
        organisation = PersistenceManager.loadOrganisation(f);
    }

    @Test
    public void testLoad() {
        // Done in setUp so this test will fail if file does not load.
    }

    @Test
    public void personDetailsPaneTest() {
        // Covers issue #106
        PersonDetailsPaneViewModel viewModel = new PersonDetailsPaneViewModel();
        final Person person = organisation.getPeople().get(0);
        viewModel.load(person, organisation);

        final String PHONE_NUMBER = "0272960114";
        viewModel.phoneNumberProperty().set(PHONE_NUMBER);
        UndoManager.getUndoManager().doCommand(viewModel.getCommand());
        Assert.assertEquals(PHONE_NUMBER, person.getPhoneNumber());
        Assert.assertEquals(PHONE_NUMBER, viewModel.phoneNumberProperty().get());

        final String SHORT_NAME = "Jose";
        viewModel.shortNameProperty().set(SHORT_NAME);
        UndoManager.getUndoManager().doCommand(viewModel.getCommand());
        Assert.assertEquals(SHORT_NAME, person.getShortName());
        Assert.assertEquals(SHORT_NAME, viewModel.shortNameProperty().get());
    }
}
