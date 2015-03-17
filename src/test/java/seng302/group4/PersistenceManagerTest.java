package seng302.group4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import static org.junit.Assert.*;


// Methods are run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceManagerTest {

    Project project = null;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    /**
     * Tests that a project can be saved then loaded.
     * All in one test so that it works with the temp folder
     * @throws Exception
     */
    @Test
    public void afirstSetUP() throws Exception {
        project = new Project("p", "Project", testFolder.getRoot());
        PersistenceManager.saveProject(project.getSaveLocation(), project);
        assertTrue(new File(project.getSaveLocation() + "/" + project.getShortName() + ".json").exists());

        Project loadedProject = PersistenceManager.loadProject(new File(testFolder.getRoot() + "/p.json"));
        assertTrue(loadedProject.equals(project));
    }


}