package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneView;
import com.thirstygoat.kiqo.gui.backlog.BacklogDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneView;
import com.thirstygoat.kiqo.gui.person.PersonDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneView;
import com.thirstygoat.kiqo.gui.project.ProjectDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneView;
import com.thirstygoat.kiqo.gui.release.ReleaseDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneView;
import com.thirstygoat.kiqo.gui.skill.SkillDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneView;
import com.thirstygoat.kiqo.gui.sprint.SprintDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneView;
import com.thirstygoat.kiqo.gui.team.TeamDetailsPaneViewModel;
import com.thirstygoat.kiqo.gui.view.AdvancedSearchView;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;

/**
 * Created by bradley on 16/09/15.
 */
public class OptimizedDetailsPane {
    private ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> skillViewTuple;
    private ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> projectViewTuple;
    private ViewTuple<PersonDetailsPaneView, PersonDetailsPaneViewModel> personViewTuple;
    private ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> teamViewTuple;
    private ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> releaseViewTuple;
    private ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> backlogViewTuple;
    private ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> sprintViewTuple;
    private ViewTuple<AdvancedSearchView, AdvancedSearchViewModel> searchViewTuple;

    public OptimizedDetailsPane() {
        // Initially load all the view tuples so we're always ready!
        loadSkillViewTuple();
        loadProjectViewTuple();
        loadPersonViewTuple();
        loadTeamViewTuple();
        loadReleaseViewTuple();
        loadBacklogViewTuple();
        loadSprintViewTuple();
        loadSearchViewTuple();
    }

    public ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> getSkillViewTuple() {
        ViewTuple<SkillDetailsPaneView, SkillDetailsPaneViewModel> tmp = skillViewTuple;
        loadSkillViewTuple();
        return tmp;
    }

    public ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> getProjectViewTuple() {
        ViewTuple<ProjectDetailsPaneView, ProjectDetailsPaneViewModel> tmp = projectViewTuple;
        loadProjectViewTuple();
        return tmp;
    }

    public ViewTuple<PersonDetailsPaneView, PersonDetailsPaneViewModel> getPersonViewTuple() {
        ViewTuple<PersonDetailsPaneView, PersonDetailsPaneViewModel> tmp = personViewTuple;
        loadPersonViewTuple();
        return tmp;
    }

    public ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> getTeamViewTuple() {
        ViewTuple<TeamDetailsPaneView, TeamDetailsPaneViewModel> tmp = teamViewTuple;
        loadTeamViewTuple();
        return tmp;
    }

    public ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> getReleaseViewTuple() {
        ViewTuple<ReleaseDetailsPaneView, ReleaseDetailsPaneViewModel> tmp = releaseViewTuple;
        loadReleaseViewTuple();
        return tmp;
    }

    public ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> getBacklogViewTuple() {
        ViewTuple<BacklogDetailsPaneView, BacklogDetailsPaneViewModel> tmp = backlogViewTuple;
        loadBacklogViewTuple();
        return tmp;
    }

    public ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> getSprintViewTuple() {
        ViewTuple<SprintDetailsPaneView, SprintDetailsPaneViewModel> tmp = sprintViewTuple;
        loadSprintViewTuple();
        return tmp;
    }

    public ViewTuple<AdvancedSearchView, AdvancedSearchViewModel> getSearchViewTuple() {
        ViewTuple<AdvancedSearchView, AdvancedSearchViewModel> tmp = searchViewTuple;
        loadSearchViewTuple();
        return tmp;

    }

    private void loadSkillViewTuple() {
        Platform.runLater(() -> skillViewTuple = FluentViewLoader.fxmlView(SkillDetailsPaneView.class).load());
    }

    private void loadProjectViewTuple() {
        Platform.runLater(() -> projectViewTuple = FluentViewLoader.fxmlView(ProjectDetailsPaneView.class).load());
    }

    private void loadPersonViewTuple() {
        Platform.runLater(() -> personViewTuple = FluentViewLoader.fxmlView(PersonDetailsPaneView.class).load());
    }

    private void loadTeamViewTuple() {
        Platform.runLater(() -> teamViewTuple = FluentViewLoader.fxmlView(TeamDetailsPaneView.class).load());
    }

    private void loadReleaseViewTuple() {
        Platform.runLater(() -> releaseViewTuple = FluentViewLoader.fxmlView(ReleaseDetailsPaneView.class).load());
    }

    private void loadBacklogViewTuple() {
        Platform.runLater(() -> backlogViewTuple = FluentViewLoader.fxmlView(BacklogDetailsPaneView.class).load());
    }

    private void loadSprintViewTuple() {
        Platform.runLater(() -> sprintViewTuple = FluentViewLoader.fxmlView(SprintDetailsPaneView.class).load());
    }

    private void loadSearchViewTuple() {
        Platform.runLater(() -> searchViewTuple = FluentViewLoader.fxmlView(AdvancedSearchView.class).load());
    }

}