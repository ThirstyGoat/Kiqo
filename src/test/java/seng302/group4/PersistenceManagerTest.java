package seng302.group4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import static org.junit.Assert.*;


// Methods are run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceManagerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    /**
     * Tests that a project can be saved then loaded.
     * All in one test so that it works with the temp folder
     * @throws Exception
     */
    @Test
    public void aBestCase() throws Exception {
        Project project = new Project("p", "Project", testFolder.newFile("test.json"));
        PersistenceManager.saveProject(project.getSaveLocation(), project);
        assertTrue(new File(project.getSaveLocation() + "").exists());

        Project loadedProject = PersistenceManager.loadProject(new File(testFolder.getRoot() + "/test.json"));
        assertTrue(loadedProject.equals(project));
    }

    /**
     * Tests that an exception is thrown when a invalid file path is given to load a project from
     */
    @Rule public ExpectedException thrown=ExpectedException.none();
    @Test
    public void bFileNotFoundLoad() throws Exception {
        thrown.expect( Exception.class );

        Project p = PersistenceManager.loadProject(new File("a/non/existent/file/path"));

    }

    /**
     * Tests that people are saved with the project and can be loaded correctly
     */
    @Test
    public void testSavePerson() throws Exception {
        Person person = new Person("a", "a", "a", "a", "a", "a" ,"a");
        Project project = new Project("p", "Project", testFolder.newFile("test.json"));

        project.addPerson(person);

        PersistenceManager.saveProject(project.getSaveLocation(), project);
        Project loadedProject = PersistenceManager.loadProject(project.getSaveLocation());

        assertTrue(loadedProject.getPeople().contains(person));
    }
}