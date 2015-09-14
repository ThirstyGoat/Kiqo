package com.thirstygoat.kiqo.search;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Created by leroy on 25/07/15.
 */
public class SearchableItems {
    private static SearchableItems instance;
    private ObservableList<Searchable> searchableItems = FXCollections.observableArrayList();
    private ObjectProperty<Organisation> organisation = new SimpleObjectProperty<>();

    private SearchableItems() {
    }

    public static SearchableItems getInstance() {
        if (instance == null) {
            instance = new SearchableItems();
        }
        return instance;
    }

    public void clear() {
        searchableItems.clear();
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }

    public Organisation getOrganisation() {
        return organisationProperty().get();
    }

    public void addSearchable(Searchable item) {
        searchableItems.add(item);
    }

    public ObservableList<Searchable> getSearchables() {
        return FXCollections.unmodifiableObservableList(searchableItems);
    }

    /**
     * Generates and returns a list of Searchables limited to the scope that is defined
     *
     * @param scope Scope of search (eg Projects/Teams/People)
     * @return ObservableList containing Searchables items within the defined scope
     */
    public ObservableList<? extends Searchable> getSearchables(SCOPE scope) {
        switch (scope) {
            case ORGANISATION:
                return getSearchables();
            case PROJECTS:
                return getOrganisation().getProjects();
            case TEAMS:
                return getOrganisation().getTeams();
            case PEOPLE:
                return getOrganisation().getPeople();
            case SKILLS:
                return getOrganisation().getSkills();
            case BACKLOGS:
                ObservableList<Searchable> backlogs = FXCollections.observableArrayList();
                for (Project project : getOrganisation().getProjects()) {
                    project.observableBacklogs().forEach(backlogs::addAll);
                }
                return backlogs;
            case STORIES:
                ObservableList<Searchable> stories = FXCollections.observableArrayList();
                for (Project project : getOrganisation().getProjects()) {
                    stories.addAll(project.observableUnallocatedStories());
                    for (Backlog backlog : project.observableBacklogs())
                        backlog.getStories().forEach(stories::addAll);
                }
                return stories;
        }
        return getSearchables();
    }

    public void removeSearchable(Searchable searchable) {
        searchableItems.remove(searchable);
    }

    /**
     * Add all Searchables in Organisation to SearchableItems. Useful for deserialisation.
     *
     * @param organisation model to traverse for searchables
     */
    public void addAll(Organisation organisation) {
        organisation.getProjects().forEach((project) -> {
            addSearchable(project);
            // (allocations are not included)
            project.getReleases().forEach(this::addSearchable);
            project.getUnallocatedStories().forEach(this::addSearchable);
            project.getSprints().forEach(this::addSearchable);
            project.getBacklogs().forEach((backlog) -> {
                addSearchable(backlog);
                backlog.getStories().forEach((story) -> {
                    addSearchable(story);
                    story.getTasks().forEach(this::addSearchable);
                    story.getAcceptanceCriteria().forEach(this::addSearchable);
                });
            });
        });
        organisation.getPeople().forEach(this::addSearchable);
        organisation.getSkills().forEach(this::addSearchable);
        organisation.getTeams().forEach(this::addSearchable);
    }

    public enum SCOPE {
        ORGANISATION("Entire Organisation"),
        PROJECTS("Projects"),
        BACKLOGS("Backlogs"),
        STORIES("Stories"),
        TEAMS("Teams"),
        PEOPLE("People"),
        SKILLS("Skills");

        private String name;

        SCOPE(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
