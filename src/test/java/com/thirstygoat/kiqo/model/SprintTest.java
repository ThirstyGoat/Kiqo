package com.thirstygoat.kiqo.model;


import org.junit.Test;

/**
 * Created by james on 21/09/15.
 */
public class SprintTest {
    private Sprint sprint;
    private Story story;
    private Task task;


    @Test
    public void testEstimateProperty() {
        sprint = new Sprint();
        story = new Story();
        task = new Task();
        story.getTasks().add(task);
        System.out.println(sprint.getStories());
        sprint.getStories().add(story);

        System.out.println("Before adding: " + sprint.createTotalEstimateBinding().get());

        task.setEstimate(3f);

        System.out.println("After adding: " + sprint.createTotalEstimateBinding().get());

    }


}
