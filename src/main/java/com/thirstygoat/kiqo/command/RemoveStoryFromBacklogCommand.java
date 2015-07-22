package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps the commands needed when a story is removed from a backlog into a single
 * command
 * @author Bradley Kirwan
 */
public class RemoveStoryFromBacklogCommand extends Command {
    private CompoundCommand compoundCommand;
    public RemoveStoryFromBacklogCommand(Story story, Backlog backlog) {
        List<Command<?>> commands = new ArrayList<>();
        commands.add(new MoveItemCommand<>(story, backlog.observableStories(),
                backlog.projectProperty().get().observableUnallocatedStories()));
        commands.add(new EditCommand<>(story, "backlog", null));
        if (story.getIsReady()) {
            commands.add(new EditCommand<>(story, "isReady", false));
        }
        compoundCommand = new CompoundCommand("Remove Story from Backlog", commands);
    }

    @Override
    public Object execute() {
        return compoundCommand.execute();
    }

    @Override
    public void undo() {
        compoundCommand.undo();
    }

    @Override
    public String getType() {
        return compoundCommand.getType();
    }
}