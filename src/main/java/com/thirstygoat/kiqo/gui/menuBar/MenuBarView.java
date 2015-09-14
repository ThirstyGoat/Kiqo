package com.thirstygoat.kiqo.gui.menuBar;

import com.thirstygoat.kiqo.gui.SideBarController;
import com.thirstygoat.kiqo.gui.ToolBarController;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class MenuBarView implements FxmlView<MenuBarViewModel> {
    @FXML
    private MenuItem newOrganisationMenuItem;
    @FXML
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem newTeamMenuItem;
    @FXML
    private MenuItem newPersonMenuItem;
    @FXML
    private MenuItem newSkillMenuItem;
    @FXML
    private MenuItem newReleaseMenuItem;
    @FXML
    private MenuItem newSprintMenuItem;
    @FXML
    private MenuItem newBacklogMenuItem;
    @FXML
    private MenuItem newStoryMenuItem;
    @FXML
    private MenuItem revertMenuItem;
    @FXML
    private MenuItem searchMenuItem;
    @FXML
    private MenuItem generateStatusReportMenuItem;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem editMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private MenuItem advancedSearchMenuItem;
    @FXML
    private CheckMenuItem listToggleCheckMenuItem;
    @FXML
    private CheckMenuItem toolBarToggleCheckMenuItem;
    @FXML
    private RadioMenuItem listShowProjectsMenuItem;
    @FXML
    private RadioMenuItem listShowTeamsMenuItem;
    @FXML
    private RadioMenuItem listShowPeopleMenuItem;
    @FXML
    private RadioMenuItem listShowSkillsMenuItem;
    @FXML
    private ToggleGroup selectedTab;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private ToolBarController toolBarController;

    @InjectViewModel
    private MenuBarViewModel viewModel;

    public void initialize() {
        revertMenuItem.disableProperty().bind(viewModel.changesSaved());

        editMenuItem.disableProperty().bind(viewModel.itemSelected().not());
        deleteMenuItem.disableProperty().bind(viewModel.itemSelected().not());

        undoMenuItem.disableProperty().bind(viewModel.undoDisabled());
        undoMenuItem.textProperty().bind(viewModel.undoString());
        redoMenuItem.disableProperty().bind(viewModel.redoDisabled());
        redoMenuItem.textProperty().bind(viewModel.redoString());

        listToggleCheckMenuItem.selectedProperty().bindBidirectional(viewModel.listVisible());
        viewModel.mainControllerIsSet().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                toolBarController.setMainController(viewModel.getMainController());
            }
        });
        toolBarToggleCheckMenuItem.selectedProperty().bindBidirectional(viewModel.toolBarIsVisible());
        toolBarToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            toolBarController.setVisible(newValue);
        });
        viewModel.selectedTab().addListener((observable, oldValue, newValue) -> {
            if (newValue == SideBarController.TabOption.PROJECTS) {
                selectedTab.selectToggle(listShowProjectsMenuItem);
            } else if (newValue == SideBarController.TabOption.TEAMS) {
                selectedTab.selectToggle(listShowTeamsMenuItem);
            } else if (newValue == SideBarController.TabOption.PEOPLE) {
                selectedTab.selectToggle(listShowPeopleMenuItem);
            } else if (newValue == SideBarController.TabOption.SKILLS) {
                selectedTab.selectToggle(listShowSkillsMenuItem);
            }
        });

        newProjectMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newTeamMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN));
        newPersonMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        newSkillMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.K, KeyCombination.SHORTCUT_DOWN));
        newReleaseMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        newBacklogMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN));
        newStoryMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN));
        newSprintMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        listToggleCheckMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));
        toolBarToggleCheckMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.SLASH, KeyCombination.SHORTCUT_DOWN));
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        editMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.SHORTCUT_DOWN));
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));
        final long[] timestamp = {0};
        searchMenuItem.setAccelerator(new KeyCombination() {
            @Override
            public boolean match(KeyEvent event) {
                if (event.getCode() == KeyCode.SHIFT) {
                    long diff = System.currentTimeMillis() / 1000L - timestamp[0];
                    if (diff < 1) {
                        timestamp[0] = 0;
                        return true;
                    }
                    timestamp[0] = System.currentTimeMillis() / 1000L;
                }
                return false;
            }

            @Override
            public String getDisplayText() {
                return "Double Shift";
            }
        });
        advancedSearchMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
    }

    @FXML
    public void newOrganisation() {
        viewModel.newOrganisationAction();
    }

    @FXML
    public void newProject() {
        viewModel.newProjectAction();
    }

    @FXML
    public void newRelease() {
        viewModel.newReleaseAction();
    }

    @FXML
    public void newSprint() {
        viewModel.newSprintAction();
    }

    @FXML
    public void newTeam() {
        viewModel.newTeamAction();
    }

    @FXML
    public void newPerson() {
        viewModel.newPersonAction();
    }

    @FXML
    public void newSkill() {
        viewModel.newSkillAction();
    }

    @FXML
    public void newBacklog() {
        viewModel.newBacklogAction();
    }

    @FXML
    public void newStory() {
        viewModel.newStoryAction();
    }

    @FXML
    public void open() {
        viewModel.openAction();
    }

    @FXML
    public void save() {
        viewModel.saveAction();
    }

    @FXML
    public void saveAs() {
        viewModel.saveAsAction();
    }

    @FXML
    public void revert() {
        viewModel.revertAction();
    }

    @FXML
    public void search() {
        viewModel.searchAction();
    }

    @FXML
    public void generateReport() {
        viewModel.generateReportAction();
    }

    @FXML
    public void quit() {
        viewModel.quitAction();
    }

    @FXML
    public void undo() {
        viewModel.undoAction();
    }

    @FXML
    public void redo() {
        viewModel.redoAction();
    }

    @FXML
    public void edit() {
        viewModel.editAction();
    }

    @FXML
    public void delete() {
        viewModel.deleteAction();
    }

    @FXML
    public void advancedSearch() {
        viewModel.advancedSearchAction();
    }

    @FXML
    public void projectsTab() {
        viewModel.projectsTabAction();
    }

    @FXML
    public void teamsTab() {
        viewModel.teamsTabAction();
    }

    @FXML
    public void peopleTab() {
        viewModel.peopleTabAction();
    }

    @FXML
    public void skillsTab() {
        viewModel.skillsTabAction();
    }
    
    @FXML
    public void about() {
        viewModel.aboutAction();
    }
}