package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;


public class SprintDetailsPaneBurndownViewModel extends SprintViewModel implements Editable {
    private LongProperty days = new SimpleLongProperty(0);
    private ObservableList<XYChart.Data<LocalDate, Number>> targetLineData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<LocalDate, Number>> loggedHoursData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<LocalDate, Number>> burndownData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> targetLineDataProperty = new SimpleObjectProperty<>(targetLineData);
    private ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> loggedHoursDataProperty = new SimpleObjectProperty<>(loggedHoursData);
    private ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> burndownDataProperty = new SimpleObjectProperty<>(burndownData);

    public SprintDetailsPaneBurndownViewModel() {
        super();
        sprintWrapper.dirtyProperty().addListener((observable, oldValue, newValue) -> {
            draw();
        });
        totalEstimatedHoursProperty().addListener((observable, oldValue, newValue) -> {
            draw();
        });
        spentHoursProperty().addListener((observable, oldValue, newValue) -> {
            draw();
        });

    }

    public LongProperty daysProperty() {
        return days;
    }

    private void updateDays() {
        long dayCount = Math.abs(ChronoUnit.DAYS.between(sprintProperty().get().endDateProperty().get(),
                        sprintProperty().get().startDateProperty().get()));
        days.set(dayCount);
    }

    public void draw() {
        updateDays();
        drawTargetLine();
        drawLines();
    }

    private void drawLines() {
        loggedHoursData.clear();
        burndownData.clear();

        Queue<Effort> efforts = getTotalEffort();
        Float totalEstimate = getTotalEstimate();

        if (efforts.isEmpty())
            return;

        // for each day calculate the spent effort
        // if the day has none logged use the previous days effort

        // calculate the range of days to iterate over between the start of the sprint and either the end date or localdate.now() depending on which is earlier
        LocalDate endDay = (sprintProperty().get().getEndDate().isAfter(LocalDate.now())) ? LocalDate.now() : sprintProperty().get().getEndDate();

        LocalDate currentDay = sprintProperty().get().getStartDate();
        Effort currentEffort;
        float totalSpentEffort = 0;

        while(currentDay.isBefore(endDay.plusDays(1))) {
            // if there is effort left get it
            while (efforts.peek() != null) {
                // if the next effort is today we need to get it and add its time to the total and then increment the effort list
                if (efforts.peek().getEndDateTime().toLocalDate().isEqual(currentDay)) {
                    currentEffort = efforts.poll();
                    totalSpentEffort += Utilities.durationToFloat(currentEffort.getDuration());
                } else {
                    // the day is tomorrow and therefore we need to break the while to increment the day
                    break;
                }
            }
            // at this point all the effort for a day has been totalled and we now need to add it to the burndown
            XYChart.Data<LocalDate, Number> point = new XYChart.Data<>(currentDay, totalSpentEffort);
            XYChart.Data<LocalDate, Number> point2 = new XYChart.Data<>(currentDay, Math.max(totalEstimate - totalSpentEffort, 0));
            loggedHoursData.add(point);
            burndownData.add(point2);
            // and then move to the next day
            currentDay = currentDay.plusDays(1);
        }
    }


    private void drawTargetLine() {
        targetLineData.clear();
        targetLineData.add(new XYChart.Data<>(sprintProperty().get().getStartDate(), totalEstimatedHoursProperty().get()));
        targetLineData.add(new XYChart.Data<>(sprintProperty().get().endDateProperty().get(), 0));
    }

    public ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> targetLineDataProperty() {
        return targetLineDataProperty;
    }

    public ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> loggedHoursDataProperty() {
        return loggedHoursDataProperty;
    }

    public ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> burndownDataProperty() {
        return burndownDataProperty;
    }

    @Override
    public void commitEdit() {
        // no editing done on chart
    }

    @Override
    public void cancelEdit() {
        // no editing done on chart
    }

    private Queue<Effort> getTotalEffort() {
        Comparator<Effort> comparator = (o1, o2) -> o1.getEndDateTime().compareTo(o2.getEndDateTime());
        Queue<Effort> efforts = new PriorityQueue<>(comparator);

        for (Story story : sprintProperty().get().getStories()) {
            for (Task task : story.getTasks()) {
                efforts.addAll(task.getLoggedEffort().stream().collect(Collectors.toList()));
            }
        }
        sprintProperty().get().getTasksWithoutStory().getTasks().forEach(t -> t.getLoggedEffort().forEach(efforts::add));
        return efforts;
    }


    private Float getTotalEstimate() {
        final Float[] totalEstimate = {0f};
        for (Story story : sprintProperty().get().getStories()) {
            for (Task task : story.getTasks()) {
                totalEstimate[0] += task.getEstimate();
            }
        }
        sprintProperty().get().getTasksWithoutStory().getTasks().forEach(t -> totalEstimate[0] += t.getEstimate());
        return totalEstimate[0];
    }

}


