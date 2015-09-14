package com.thirstygoat.kiqo.gui.release;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.create.CreateReleaseCommand;
import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ReleaseViewModel implements Loadable<Release>, ViewModel {
    private ModelWrapper<Release> modelWrapper;
    private ObjectProperty<Release> release;
    private ObjectProperty<Organisation> organisation;

    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private CompositeValidator allValidator;
    private ObservableRuleBasedValidator projectValidator;
    private ObservableRuleBasedValidator dateValidator;

    public ReleaseViewModel() {
        release = new SimpleObjectProperty<>(null);
        organisation = new SimpleObjectProperty<>(null);
        modelWrapper = new GoatModelWrapper<>();
        createValidators();
    }

    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() ->
                {
                    Project project = projectProperty().get();
                    if (project != null) {
                        return Utilities.shortnameIsUnique(shortNameProperty().get(), release.get(), project.getReleases());
                    } else {
                        return true; // no project means this isn't for real yet.
                    }
                },
                shortNameProperty(), projectProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true

        projectValidator = new ObservableRuleBasedValidator();
        projectValidator.addRule(projectProperty().isNotNull(), ValidationMessage.error("Project must not be empty"));

        dateValidator = new ObservableRuleBasedValidator();
        BooleanBinding isAfterAllSprintsAreFinished = Bindings.createBooleanBinding(() -> {
            if (release.get() != null) { // new releases don't have sprints
                LocalDate releaseDate = dateProperty().get();
                if (releaseDate != null) {
                    return release.get().getSprints().stream().allMatch(sprint -> releaseDate.isAfter(sprint.getEndDate()));
                }
            }
            return true;
        }, dateProperty());
        dateValidator.addRule(dateProperty().isNotNull(), ValidationMessage.error("Release date must not be empty"));
        dateValidator.addRule(isAfterAllSprintsAreFinished, ValidationMessage.error("Release date must fall after any sprint within."));

        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator, projectValidator, dateValidator);
    }

    @Override
    public void load(Release release, Organisation organisation) {
        this.release.set(release);
        this.organisation.set(organisation);
        modelWrapper.set(release != null ? release : new Release());
        modelWrapper.reload();
    }

    protected Command createCommand() {
        final Command command;
        if (release.get() != null) { // edit
            final ArrayList<Command> changes = new ArrayList<>();

            if (shortNameProperty().get() != null && !shortNameProperty().get().equals(release.get().getShortName())) {
                changes.add(new EditCommand<>(release.get(), "shortName", shortNameProperty().get()));
            }
            if (descriptionProperty().get() != null && !descriptionProperty().get().equals(release.get().getDescription())) {
                changes.add(new EditCommand<>(release.get(), "description", descriptionProperty().get()));
            }
            if (projectProperty().get() != null && !projectProperty().get().equals(release.get().getProject())) {
                changes.add(new MoveItemCommand<>(release.get(), release.get().getProject().observableReleases(), projectProperty().get().observableReleases()));
                changes.add(new EditCommand<>(release.get(), "project", projectProperty().get()));
            }
            if (dateProperty().get() != null && !dateProperty().get().equals(release.get().getDate())) {
                changes.add(new EditCommand<>(release.get(), "date", dateProperty().get()));
            }

            if (changes.size() > 0) {
                command = new CompoundCommand("Edit Release", changes);
            } else {
                command = null;
            }
        } else { // new
            final Release release = new Release(shortNameProperty().get(), projectProperty().get(), dateProperty().get(), descriptionProperty().get());
            command = new CreateReleaseCommand(release);
        }
        return command;
    }

    protected void reload() {
        modelWrapper.reload();
    }

    protected Supplier<List<Project>> projectsSupplier() {
        return () -> {
            List<Project> list = new ArrayList<>();
            if (organisation.get() != null) {
                list.addAll(organisation.get().getProjects());
            }
            return list;
        };
    }

    protected StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Release::getShortName, Release::setShortName, "");
    }

    protected StringProperty descriptionProperty() {
        return modelWrapper.field("description", Release::getDescription, Release::setDescription, "");
    }

    protected ObjectProperty<Project> projectProperty() {
        return modelWrapper.field("project", Release::getProject, Release::setProject, null);
    }

    protected ObjectProperty<LocalDate> dateProperty() {
        return modelWrapper.field("date", Release::getDate, Release::setDate, null);
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

    protected ValidationStatus projectValidation() {
        return projectValidator.getValidationStatus();
    }

    protected ValidationStatus dateValidation() {
        return dateValidator.getValidationStatus();
    }

    protected ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}
