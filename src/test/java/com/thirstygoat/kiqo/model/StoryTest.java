package com.thirstygoat.kiqo.model;


import org.junit.Test;

/**
 * Created by samschofield on 21/04/15.
 */
public class StoryTest {
    private Story story;
    private Task task;


    @Test
    public void testEstimateProperty() {
        story = new Story();
        task = new Task();
        story.getTasks().add(task);

        System.out.println("Before adding: " + story.totalEstimateProperty().get());

        task.setEstimate(3f);

        System.out.println("After adding: " + story.totalEstimateProperty().get());

    }

}
