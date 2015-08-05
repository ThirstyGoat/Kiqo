package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;

/**
 * Created by samschofield on 31/07/15.
 */
public class TestUtils {

    public static Organisation initOrganisation() {
        Organisation organisation = new Organisation(true);
        Project project = initProject();
        organisation.getProjects().add(project);
        return organisation;
    }

    public static Project initProject() {
        Project project = new Project("Project1", "Project 1");
        return project;
    }
}
