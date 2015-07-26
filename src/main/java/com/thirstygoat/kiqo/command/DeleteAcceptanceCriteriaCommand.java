package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.SearchableItems;
import com.thirstygoat.kiqo.model.Story;

/**
 * Command to delete a acceptanceCriteria from a project.
 *
 */
public class DeleteAcceptanceCriteriaCommand extends Command<AcceptanceCriteria> {

    private final Story story;
    private final AcceptanceCriteria acceptanceCriteria;
    // Hash map of people with skills, and the index at which the acceptanceCriteria appears in their skills list

    private int storyIndex;

    /**
     *
     */
    public DeleteAcceptanceCriteriaCommand(final AcceptanceCriteria acceptanceCriteria, final Story story) {
        this.acceptanceCriteria = acceptanceCriteria;
        this.story = story;
    }

    @Override
    public AcceptanceCriteria execute() {
        storyIndex = story.getAcceptanceCriteria().indexOf(acceptanceCriteria);
        story.getAcceptanceCriteria().remove(acceptanceCriteria);

        // Remove from SearchableItems
        SearchableItems.getInstance().removeSearchable(acceptanceCriteria);
        return acceptanceCriteria;
    }

    @Override
    public void undo() {
        // Add the acceptanceCriteria back to wherever it was
        story.getAcceptanceCriteria().add(storyIndex, acceptanceCriteria);

        // Add back to SearchableItems
        SearchableItems.getInstance().addSearchable(acceptanceCriteria);
    }

    @Override
    public String toString() {
        return "<Delete AC: \"" + acceptanceCriteria.criteria + "\">";
    }

    @Override
    public String getType() {
        return "Delete AC";
    }
}