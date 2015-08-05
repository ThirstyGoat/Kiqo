package com.thirstygoat.kiqo.search;

<<<<<<< Upstream, based on twig/sharedBasicSearch
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
=======
import java.util.Collections;
import java.util.List;

>>>>>>> 1a6614e Updated SearchTest to use CreateCommands instead of Item constructor. Converted SearchableItems.clear() into a static method (following singleton pattern).
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

<<<<<<< Upstream, based on twig/sharedBasicSearch
    public ObservableList<Searchable> getSearchables() {
        return FXCollections.unmodifiableObservableList(searchableItems);
    }

    /**
     * Generates and returns a list of Searchables limited to the scope that is defined
     * @param scope Scope of search (eg Projects/Teams/People)
     * @return ObservableList containing Searchables items within the defined scope
     */
    public ObservableList<? extends Searchable> getSearchables(SCOPE scope) {
        switch (scope) {
            case ORGANISATION: return getSearchables();
            case PROJECTS: return getOrganisation().getProjects();
            case TEAMS: return getOrganisation().getTeams();
            case PEOPLE: return getOrganisation().getPeople();
            case SKILLS: return getOrganisation().getSkills();
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
                    for (Backlog backlog: project.observableBacklogs())
                        backlog.getStories().forEach(stories::addAll);
                }
                return stories;
        }
        return getSearchables();
=======
    public List<Searchable> getSearchables() {
        return Collections.unmodifiableList(searchableItems);
>>>>>>> 1a6614e Updated SearchTest to use CreateCommands instead of Item constructor. Converted SearchableItems.clear() into a static method (following singleton pattern).
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
