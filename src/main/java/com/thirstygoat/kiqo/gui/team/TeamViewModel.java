package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TeamViewModel implements Loadable<Team>, ViewModel {
    private ModelWrapper<Team> modelWrapper;
    private ObjectProperty<Team> team;
    private ObjectProperty<Organisation> organisation;

    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private ObservableRuleBasedValidator productOwnerValidator;
    private ObservableRuleBasedValidator scrumMasterValidator;
    private ObservableRuleBasedValidator teamMembersValidator;
    private ObservableRuleBasedValidator devTeamValidator;
    private CompositeValidator allValidator;

    public TeamViewModel() {
        team = new SimpleObjectProperty<>(null);
        organisation = new SimpleObjectProperty<>(null);
        modelWrapper = new GoatModelWrapper<>();
        createValidators();
    }

    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() ->
                {
                    if (organisation.get() != null) {
                        return Utilities.shortnameIsUnique(shortNameProperty().get(), team.get(), organisation.get().getTeams());
                    } else {
                        return true; // no project means this isn't for real yet.
                    }
                },
                shortNameProperty(), organisationProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true

        // the other validators are input-constrained so need not be validated
        productOwnerValidator = new ObservableRuleBasedValidator(); // always true
        scrumMasterValidator = new ObservableRuleBasedValidator(); // always true
        teamMembersValidator = new ObservableRuleBasedValidator(); // always true
        devTeamValidator = new ObservableRuleBasedValidator(); // always true

        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator, productOwnerValidator, scrumMasterValidator, teamMembersValidator, devTeamValidator);
    }

    protected Command createCommand() {
        // edit command
        final ArrayList<Command> changes = new ArrayList<>();

        if (!shortNameProperty().get().equals(team.get().getShortName())) {
            changes.add(new EditCommand<>(team.get(), "shortName", shortNameProperty().get()));
        }
        if (!descriptionProperty().get().equals(team.get().getDescription())) {
            changes.add(new EditCommand<>(team.get(), "description", descriptionProperty().get()));
        }
        if (!(teamMembersProperty().get().containsAll(team.get().getTeamMembers()) && team.get().getTeamMembers().containsAll(teamMembersProperty().get()))) {
            changes.add(new EditCommand<>(team.get(), "teamMembers", teamMembersProperty().get()));
        }

        if (productOwnerProperty().get() != team.get().getProductOwner()) {
            changes.add(new EditCommand<>(team.get(), "productOwner", productOwnerProperty().get()));
        }

        if (scrumMasterProperty().get() != team.get().getScrumMaster()) {
            changes.add(new EditCommand<>(team.get(), "scrumMaster", scrumMasterProperty().get()));
        }

        if (!(devTeamProperty().get().containsAll(team.get().getDevTeam()) && team.get().getDevTeam().containsAll(devTeamProperty().get()))) {
            changes.add(new EditCommand<>(team.get(), "devTeam", devTeamProperty().get()));
        }

        final ArrayList<Person> newMembers = new ArrayList<>(teamMembersProperty().get());
        newMembers.removeAll(team.get().getTeamMembers());
        final ArrayList<Person> oldMembers = new ArrayList<>(team.get().getTeamMembers());
        oldMembers.removeAll(teamMembersProperty().get());

        // Loop through all the new members and add a command to set their team
        // Set the person's team field to this team
        changes.addAll(newMembers.stream().map(person -> new EditCommand<>(person, "team", team))
                .collect(Collectors.toList()));

        // Loop through all the old members and add a command to remove their team
        // Set the person's team field to null, since they're no longer in the team
        changes.addAll(oldMembers.stream().map(person -> new EditCommand<>(person, "team", null))
                .collect(Collectors.toList()));

        return new CompoundCommand("Edit Team", changes);
    }

    protected void reload() {
        modelWrapper.reload();
    }

    @Override
    public void load(Team team, Organisation organisation) {
        this.team.set(team);
        this.organisation.set(organisation);
        modelWrapper.set(team != null ? team : new Team());
        modelWrapper.reload();
    }

    protected Supplier<List<Person>> productOwnerSupplier() {
        return () -> {
            Skill poSkill = organisationProperty().get().getPoSkill();
            Person currentScrumMaster = scrumMasterProperty().get();
            List<Person> currentDevTeam = devTeamProperty().get();
            // person has po skill and does not currently have any other role in the team
            List<Person> eligiblePeople = organisationProperty().get().getPeople().stream()
                    .filter(person -> {
                        return person.getSkills().contains(poSkill)
                                && (currentScrumMaster == null || !person.equals(currentScrumMaster))
                                && !currentDevTeam.contains(person);
                    }).collect(Collectors.toList());
            return eligiblePeople;
        };
    }

    protected Supplier<List<Person>> scrumMasterSupplier() {
        return () -> {
            Skill smSkill = organisationProperty().get().getSmSkill();
            Person currentProductOwner = productOwnerProperty().get();
            List<Person> currentDevTeam = devTeamProperty().get();
            // person has sm skill and does not currently have any other role in the team
            return organisationProperty().get().getPeople().stream()
                    .filter(person -> person.getSkills().contains(smSkill)
                            && !person.equals(currentProductOwner)
                            && !currentDevTeam.contains(person))
                    .collect(Collectors.toList());
        };
    }

    protected StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Team::getShortName, Team::setShortName, "");
    }

    protected StringProperty descriptionProperty() {
        return modelWrapper.field("description", Team::getDescription, Team::setDescription, "");
    }

    protected ObjectProperty<Person> productOwnerProperty() {
        return modelWrapper.field("productOwner", Team::getProductOwner, Team::setProductOwner, null);
    }

    protected ObjectProperty<Person> scrumMasterProperty() {
        return modelWrapper.field("scrumMaster", Team::getScrumMaster, Team::setScrumMaster, null);
    }

    protected ListProperty<Person> teamMembersProperty() {
        return modelWrapper.field("teamMembers", Team::getTeamMembers, Team::setTeamMembers, new ArrayList<Person>());
    }

    protected ListProperty<Person> devTeamProperty() {
        return modelWrapper.field("devTeam", Team::getDevTeam, Team::setDevTeam, new ArrayList<Person>());
    }

    protected ListProperty<Allocation> allocations() {
        return modelWrapper.field("allocations", Team::getAllocations, Team::setAllocations, new ArrayList<Allocation>());
    }

    protected ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }

    protected ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }

    protected ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    protected ValidationStatus productOwnerValidation() {
        return productOwnerValidator.getValidationStatus();
    }

    protected ValidationStatus scrumMasterValidation() {
        return scrumMasterValidator.getValidationStatus();
    }

    protected ValidationStatus teamMembersValidation() {
        return teamMembersValidator.getValidationStatus();
    }

    protected ValidationStatus devTeamValidation() {
        return devTeamValidator.getValidationStatus();
    }

    protected ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}
