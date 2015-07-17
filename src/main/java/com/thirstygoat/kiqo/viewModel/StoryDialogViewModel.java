package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Created by samschofield on 16/07/15.
 */
public class StoryDialogViewModel {

    public static Predicate<String> getShortNameValidation(Project project, Story story, String shortName) {
        return s -> {
            if (s.length() == 0) {
                return false;
            }
            if (project == null) {
                return true;
            }
            Collection<Collection<? extends Item>> existingBacklogs = new ArrayList<>();
            existingBacklogs.add(project.getUnallocatedStories());
            existingBacklogs.addAll(project.getBacklogs().stream().map(Backlog::observableStories).collect(Collectors.toList()));

            return Utilities.shortnameIsUniqueMultiple(shortName, story, existingBacklogs);
        };
    }

    public static Predicate<String> getExistenceValidation(List<? extends Item> list) {
        return s -> {
            for (final Item item : list) {
                if (item.getShortName().equals(s)) {
                    return true;
                }
            }
            return false;
        };
    }

    public static Predicate<String> getPriorityValidation() {
        return s -> {
            try {
                int i = Integer.parseInt(s);
                if (i < Story.MIN_PRIORITY || i > Story.MAX_PRIORITY) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        };
    }
}
