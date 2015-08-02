package com.thirstygoat.kiqo.search;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by leroy on 25/07/15.
 */
public class SearchableItems {
    private static SearchableItems instance;
    private ObservableList<Searchable> searchableItems = FXCollections.observableArrayList();

    private SearchableItems() {
    }

    public static SearchableItems getInstance() {
        if (instance == null) {
            instance = new SearchableItems();
        }
        return instance;
    }

    public static void clear() {
        getInstance().searchableItems.clear();
    }

    public void addSearchable(Searchable item) {
        searchableItems.add(item);
    }

    public ObservableList<Searchable> getSearchables() {
        return FXCollections.unmodifiableObservableList(searchableItems);
    }

    /**
     * Generates and returns a list of Searchables limited to the scope that is defined
     * @param scope Scope of search (eg Projects/Teams/People)
     * @param organisation Organisation to search within
     * @return ObservableList containing Searchables items within the defined scope
     */
    public ObservableList<? extends Searchable> getSearchables(SCOPE scope, Organisation organisation) {
        switch (scope) {
            case ORGANISATION: return getSearchables();
            case PROJECTS: return organisation.getProjects();
            case TEAMS: return organisation.getTeams();
            case PEOPLE: return organisation.getPeople();
            case SKILLS: return organisation.getSkills();
            case BACKLOGS:
                ObservableList<Searchable> backlogs = FXCollections.observableArrayList();
                for (Project project : organisation.getProjects()) {
                    project.observableBacklogs().forEach(backlogs::addAll);
                }
                return backlogs;
            case STORIES:
                ObservableList<Searchable> stories = FXCollections.observableArrayList();
                for (Project project : organisation.getProjects()) {
                    stories.addAll(project.observableUnallocatedStories());
                    project.observableBacklogs().forEach(stories::addAll);
                }
                return stories;
        }
        return getSearchables();
    }

    public void removeSearchable(Searchable searchable) {
        searchableItems.remove(searchable);
    }

    public enum SCOPE {
        ORGANISATION,
        PROJECTS,
        BACKLOGS,
        STORIES,
        TEAMS,
        PEOPLE,
        SKILLS
    }
}
