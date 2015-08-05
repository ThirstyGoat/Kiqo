package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.delete.DeleteAllocationCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.customCells.AllocationDatePickerCell;
import com.thirstygoat.kiqo.gui.customCells.AllocationListCell;
import com.thirstygoat.kiqo.gui.nodes.GoatDialog;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import org.controlsfx.control.PopOver;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


/**
 * TableView for allocations of teams to projects
 */
public class AllocationsTableViewController implements Initializable {
    private MainController mainController;
    private FirstColumnType type;
    @FXML
    private TableView<Allocation> allocationsTableView;
    @FXML
    private TableColumn<Allocation, String> teamTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> startDateTableColumn;
    @FXML
    private TableColumn<Allocation, LocalDate> endDateTableColumn;
    @FXML
    private Button allocateTeamButton;
    @FXML
    private Button deleteAllocationButton;
    @FXML
    private Button editAllocationButton;
    @FXML
    private CheckBox highlightCheckBox;
    @FXML
    private Hyperlink highlightHyperLink;

    public void init(FirstColumnType type) {
        this.type = type;
        allocateTeamButton.setOnAction(event -> mainController.allocateTeams());
        deleteAllocationButton.setOnAction(event -> deleteAllocation());
        editAllocationButton.setOnAction(event -> mainController.editAllocation(allocationsTableView.getSelectionModel().getSelectedItem()));
        //also needs to be bound to make sure that the table view is still focused too
        // at the moment it the delete button remains on if the tableview loses focus
        deleteAllocationButton.disableProperty().bind(Bindings.isNull(allocationsTableView.getSelectionModel().selectedItemProperty()));
        editAllocationButton.disableProperty().bind(Bindings.isNull(allocationsTableView.getSelectionModel().selectedItemProperty()));
        initializeTable();
        setHyperlink();
    }

    private void initializeTable() {
        // center the dates
        startDateTableColumn.setStyle("-fx-alignment: CENTER;");
        endDateTableColumn.setStyle("-fx-alignment: CENTER;");

        // set the Title of the first column and the cell VALUE factory for the first column
        if (type.equals(FirstColumnType.PROJECT)) {
            teamTableColumn.setText("Project");
            teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getProject().shortNameProperty());
            teamTableColumn.setCellFactory(tableColumn -> new AllocationListCell(mainController.selectedOrganisationProperty, this));
            allocationsTableView.setPlaceholder(new Label("Team not allocated to any projects"));
        } else if (type.equals(FirstColumnType.TEAM)) {
//            teamTableColumn.setText("Team");
            teamTableColumn.setCellValueFactory(cellData -> cellData.getValue().getTeam().shortNameProperty());
            allocationsTableView.setPlaceholder(new Label("No teams allocated to project"));
        }

        startDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getStartDateProperty());
        endDateTableColumn.setCellValueFactory(cellData -> cellData.getValue().getEndDateProperty());

        // cellFactory is NOT the same as cellValueFactory
        teamTableColumn.setCellFactory(param -> new AllocationListCell(mainController.selectedOrganisationProperty, this));

        startDateTableColumn.setCellFactory(param -> {
            final AllocationDatePickerCell startDateCellFactory = new AllocationDatePickerCell();
            startDateCellFactory.setValidationType(AllocationDatePickerCell.ValidationType.START_DATE);
            return startDateCellFactory;
        });

        endDateTableColumn.setCellFactory(param -> {
            final AllocationDatePickerCell endDateCellFactory = new AllocationDatePickerCell();
            endDateCellFactory.setValidationType(AllocationDatePickerCell.ValidationType.END_DATE);
            return endDateCellFactory;
        });


        startDateTableColumn.setOnEditCommit(event -> {
            final EditCommand<Allocation, LocalDate> command = new EditCommand<>(event.getRowValue(), "startDate", event.getNewValue());
            mainController.doCommand(command);
        });

        endDateTableColumn.setOnEditCommit(event -> {
            final EditCommand<Allocation, LocalDate> command = new EditCommand<>(event.getRowValue(), "endDate", event.getNewValue());
            mainController.doCommand(command);
        });

        // fixes ghost column issue and resizing
        allocationsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setAllocationButtonListeners() {

        setButtonDisabled();

        mainController.selectedOrganisationProperty().getValue().getProjects().addListener((ListChangeListener<Project>) c -> {
            setButtonDisabled();
        });

        mainController.selectedOrganisationProperty().getValue().getTeams().addListener((ListChangeListener<Team>) c -> {
            setButtonDisabled();
        });
    }

    private void deleteAllocation() {
            final Allocation selectedAllocation = allocationsTableView.getSelectionModel().getSelectedItem();

            final DeleteAllocationCommand command = new DeleteAllocationCommand(selectedAllocation);

            final String[] buttons = {"Delete Allocation", "Cancel"};
            final String result = GoatDialog.createBasicButtonDialog(mainController.getPrimaryStage(),
                    "Delete Project", "Are you sure?",
                    "Are you sure you want to delete the allocation on this project for team " +
                            selectedAllocation.getTeam().getShortName() + "?", buttons);

            if (result.equals("Delete Allocation")) {
                mainController.doCommand(command);
            }
    }

    /**
     * Enables the add allocations button if there are projects and teams in the org
     * otherwise disable it
     */
    private void setButtonDisabled() {
        final boolean areProjects = mainController.selectedOrganisationProperty().getValue().getProjects().size() > 0;
        final boolean areTeams = mainController.selectedOrganisationProperty().getValue().getTeams().size() > 0;
        allocateTeamButton.setDisable(!(areProjects && areTeams));
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        // need to reset the listeners if the organisation changed i.e. load from json file
        mainController.selectedOrganisationProperty().addListener((observable, oldValue, newValue) -> {
            setAllocationButtonListeners();
        });
        setAllocationButtonListeners();
    }

    public void setItems(ObservableList<Allocation> items) {
        allocationsTableView.setItems(items);
        final ChangeListener<LocalDate> listener = (observable, oldValue, newValue) -> {
            // Refresh table view since dates have changed and background colours/tooltips need to update accordingly

            initializeTable();
        };

        for (final Allocation allocation : items) {
            allocation.getStartDateProperty().addListener(listener);
            allocation.getEndDateProperty().addListener(listener);
        }

        mainController.selectedOrganisationProperty().get().getTeams().addListener((ListChangeListener<Team>) c -> {
            c.next();
            if (!c.getAddedSubList().isEmpty() || !c.getRemoved().isEmpty()) {
                initializeTable();
            }
        });

        mainController.selectedOrganisationProperty().get().getProjects().addListener((ListChangeListener<Project>) c -> {
            c.next();
            if (!c.getAddedSubList().isEmpty() || !c.getRemoved().isEmpty()) {
                initializeTable();
            }
        });

        items.addListener((ListChangeListener<Allocation>) c -> {
            c.next();
            for (final Allocation a : c.getAddedSubList()) {
                a.getStartDateProperty().addListener(listener);
                a.getEndDateProperty().addListener(listener);
            }
            for (final Allocation a : c.getRemoved()) {
                a.getStartDateProperty().removeListener(listener);
                a.getEndDateProperty().removeListener(listener);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void setHyperlink() {
        PopOver popOver = new PopOver();
        popOver.setDetachable(false);

        HBox greenHbox = new HBox();
        HBox orangeHbox = new HBox();
        HBox redHbox = new HBox();
        HBox tHbox = new HBox();

        VBox vb = new VBox();

        vb.getChildren().addAll(greenHbox, orangeHbox, redHbox, tHbox);
        vb.setSpacing(2);
        greenHbox.setAlignment(Pos.CENTER_LEFT);
        orangeHbox.setAlignment(Pos.CENTER_LEFT);
        redHbox.setAlignment(Pos.CENTER_LEFT);
        tHbox.setAlignment(Pos.CENTER_LEFT);
        greenHbox.setSpacing(5);
        orangeHbox.setSpacing(5);
        redHbox.setSpacing(5);
        tHbox.setSpacing(5);

        Rectangle g = new Rectangle(10, 10);
        Rectangle o = new Rectangle(10, 10);
        Rectangle r = new Rectangle(10, 10);
        Rectangle t = new Rectangle(10, 10);

        g.setFill(Color.rgb(0x45, 0xc3, 0x4b));
        o.setFill(Color.rgb(0x62, 0xc1, 0xff));
        r.setFill(Color.rgb(0xff, 0xc1, 0x45));
        t.setFill(Color.rgb(0xff, 0x33, 0x33));

        greenHbox.getChildren().addAll(g, new Label("Team is currently allocated to this project"));
        orangeHbox.getChildren().addAll(o, new Label("Team was allocated to this project in the past"));
        redHbox.getChildren().addAll(r, new Label("Team is allocated to this project in the future"));
        tHbox.getChildren().addAll(t, new Label("This allocation is for a Project or Team that no longer exists"));

        vb.setPadding(new Insets(10));

        popOver.setContentNode(vb);

        highlightHyperLink.setOnAction(e -> popOver.show(highlightHyperLink));
        highlightHyperLink.focusedProperty().addListener((observable, oldValue, newValue) -> popOver.hide(Duration.millis(0)));

        highlightHyperLink.visibleProperty().bind(highlightCheckBox.selectedProperty());
    }

    public BooleanProperty checkBoxSelectedProperty() {
        return highlightCheckBox.selectedProperty();
    }

    public enum  FirstColumnType {
        PROJECT, TEAM
    }
}