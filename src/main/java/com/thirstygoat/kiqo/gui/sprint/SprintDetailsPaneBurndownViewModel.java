package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;


public class SprintDetailsPaneBurndownViewModel extends SprintViewModel implements Editable {
    private ObservableList<XYChart.Data<LocalDate, Number>> targetLineData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<LocalDate, Number>> loggedHoursData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Data<LocalDate, Number>> burndownData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> targetLineDataProperty = new SimpleObjectProperty<>(targetLineData);
    private ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> loggedHoursDataProperty = new SimpleObjectProperty<>(loggedHoursData);
    private ObjectProperty<ObservableList<XYChart.Data<LocalDate, Number>>> burndownDataProperty = new SimpleObjectProperty<>(burndownData);

    public SprintDetailsPaneBurndownViewModel() {
        super();
        sprintWrapper.dirtyProperty().addListener((observable, oldValue, newValue) -> draw());
        totalEstimatedHoursProperty().addListener((observable, oldValue, newValue) -> draw());

    }

    public void draw() {
        drawTargetLine();
//        drawBurndownLine();
        drawLoggedHoursLine();
    }

    private void drawLoggedHoursLine() {
        loggedHoursData.clear();
        burndownData.clear();

        Queue<Effort> efforts = getTotalEffort();
        Float totalEstimate = getTotalEstimate();

        if (efforts.isEmpty())
            return;

        Effort currentEffort;
        LocalDate currentDay = sprintProperty().get().getStartDate();
        float accrued = 0;
        while (efforts.peek() != null) {
            Effort effort = efforts.poll();

            if (!effort.getEndTime().toLocalDate().isAfter(currentDay)) {
                accrued += effort.getDuration();
            } else {
                XYChart.Data<LocalDate, Number> point = new XYChart.Data<>(currentDay, accrued);
                XYChart.Data<LocalDate, Number> point2 = new XYChart.Data<>(currentDay, Math.max(totalEstimate - accrued, 0));
                loggedHoursData.add(point);
                burndownData.add(point2);

                currentEffort = effort;
                currentDay = effort.getEndTime().toLocalDate();
                accrued += currentEffort.getDuration();
            }
        }

        loggedHoursData.add(new XYChart.Data<>(currentDay, accrued));
        burndownData.add(new XYChart.Data<>(currentDay, Math.max(totalEstimate - accrued, 0)));
    }

    private void drawBurndownLine() {
        burndownData.clear();
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
        Comparator<Effort> comparator = (o1, o2) -> o1.getEndTime().compareTo(o2.getEndTime());
        Queue<Effort> efforts = new PriorityQueue<>(comparator);

        for (Story story : sprintProperty().get().getStories()) {
            for (Task task : story.getTasks()) {
                efforts.addAll(task.getLoggedEffort().stream().collect(Collectors.toList()));
            }
        }

        return efforts;
    }

    private Float getTotalEstimate() {
        Float totalEstimate = 0f;
        for (Story story : sprintProperty().get().getStories()) {
            for (Task task : story.getTasks()) {
                totalEstimate += task.getEstimate();
            }
        }
        return totalEstimate;
    }

}


