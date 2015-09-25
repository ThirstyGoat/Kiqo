package com.thirstygoat.kiqo.command;

import java.util.ArrayList;
import java.util.List;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Story;

/**
 * Wraps the commands needed when a story is removed from a backlog into a single
 * command
 * @author Bradley Kirwan
 */
public class RemoveStoryFromBacklogCommand extends Command {
    private CompoundCommand compoundCommand;
    public RemoveStoryFromBacklogCommand(Story story, Backlog backlog) {
        List<Command> commands = new ArrayList<>();
        commands.add(new MoveItemCommand<>(story, backlog.getStories(),
                backlog.projectProperty().get().getUnallocatedStories()));
        commands.add(new EditCommand<>(story, "backlog", null));
        if (story.getIsReady()) {
            commands.add(new EditCommand<>(story, "isReady", false));
        }
        if (!story.getDependencies().isEmpty()) {
            commands.add(new EditCommand<>(story, "dependencies", new ArrayList<>()));
        }
        compoundCommand = new CompoundCommand("Remove Story from Backlog", commands);
    }

    @Override
    public void execute() {
        compoundCommand.execute();
    }

    @Override
    public void undo() {
        compoundCommand.undo();
    }

    @Override
    public String getType() {
        return compoundCommand.getType();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RemoveStoryFromBacklogCommand\\{compoundCommand='");
        builder.append(compoundCommand);
        builder.append("\\}");
        return builder.toString();
    }
}