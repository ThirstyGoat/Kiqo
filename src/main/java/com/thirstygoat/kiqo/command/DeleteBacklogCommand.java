package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.SearchableItems;
import com.thirstygoat.kiqo.model.Story;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Carina on 20/05/2015.
 */
public class DeleteBacklogCommand extends Command<Backlog>{
    private final Project project;
    private final Backlog backlog;
    private final Map<Integer, Story> stories = new LinkedHashMap<>();
    private boolean deleteStories = false;
    private int index;

    /**
     * Delete a backlog and either delete its stories or move them to being unallocated
     * @param backlog
     */
    public DeleteBacklogCommand(final Backlog backlog) {
        this.project = backlog.getProject();
        this.backlog = backlog;

        for (final Story story  : backlog.getStories()) {
            stories.put(backlog.getStories().indexOf(story), story);
        }
    }

    /**
     * Sets the stories of the backlog to be deleted
     */
    public void setDeleteMembers() {
        deleteStories = true;
    }

    @Override
    public Backlog execute() {
        // Set stories backlog' field to null
        for (final Story story : backlog.getStories()) {
            story.setBacklog(null);
        }

        // if setDelteMember was called, delete each story member
        for (final Story story : stories.values()) {
            backlog.observableStories().remove(story);
        }

        // delete the backlog
        index = project.getBacklogs().indexOf(backlog);
        project.observableBacklogs().remove(backlog);

        // Remove from SearchableItems
        SearchableItems.getInstance().removeSearchable(backlog);

        return backlog;
    }

    @Override
    public void undo() {

        // Set team members team field to this team
        for (final Story story : backlog.getStories()) {
            story.setBacklog(backlog);
        }

        // if we deleted the stories put them back, otherwise they will be moved back with moveItemCommands in MC
        if(deleteStories) {
            for (final Map.Entry<Integer, Story> entry : stories.entrySet()) {
                backlog.observableStories().add(entry.getKey(), entry.getValue());
            }
        }
        project.observableBacklogs().add(index, backlog);

        // Add back to SearchableItems
        SearchableItems.getInstance().addSearchable(backlog);
    }

    @Override
    public String toString() {
        return "<Delete Backlog: \"" + backlog.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Backlog";
    }
}
