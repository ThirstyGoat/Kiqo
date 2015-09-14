package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Story;


/**
 * Command to add a Skill to a Project
 */
public class CreateAcceptanceCriteriaCommand extends CreateCommand {
    private final AcceptanceCriteria acceptanceCriteria;
    private final Story story;

    /**
     * @param acceptanceCriteria Skill created
     * @param story              story that the acceptanceCriteria is to be associated with
     */
    public CreateAcceptanceCriteriaCommand(final AcceptanceCriteria acceptanceCriteria, final Story story) {
        super(acceptanceCriteria);
        this.acceptanceCriteria = acceptanceCriteria;
        this.story = story;
    }

    @Override
    public void addToModel() {
        story.getAcceptanceCriteria().add(acceptanceCriteria);
    }

    @Override
    public void removeFromModel() {
        // Goodbye acceptanceCriteria
        story.getAcceptanceCriteria().remove(acceptanceCriteria);
    }

    @Override
    public String toString() {
        return "<Create AC: \"" + acceptanceCriteria.criteria + "\">";
    }

    @Override
    public String getType() {
        return "Create AC";
    }

}
