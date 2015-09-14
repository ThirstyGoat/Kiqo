package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Story;

/**
 * Command to delete a acceptanceCriteria from a project.
 */
public class DeleteAcceptanceCriteriaCommand extends DeleteCommand {

    private final Story story;
    private final AcceptanceCriteria acceptanceCriteria;
    // Hash map of people with skills, and the index at which the acceptanceCriteria appears in their skills list

    private int storyIndex;

    /**
     *
     */
    public DeleteAcceptanceCriteriaCommand(final AcceptanceCriteria acceptanceCriteria, final Story story) {
        super(acceptanceCriteria);
        this.acceptanceCriteria = acceptanceCriteria;
        this.story = story;
    }

    @Override
    public void removeFromModel() {
        storyIndex = story.getAcceptanceCriteria().indexOf(acceptanceCriteria);
        story.getAcceptanceCriteria().remove(acceptanceCriteria);
    }

    @Override
    public void addToModel() {
        // Add the acceptanceCriteria back to wherever it was
        story.getAcceptanceCriteria().add(storyIndex, acceptanceCriteria);
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
