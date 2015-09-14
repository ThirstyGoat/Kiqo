package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.Utilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
}
