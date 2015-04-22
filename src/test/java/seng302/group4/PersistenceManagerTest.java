package seng302.group4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonSyntaxException;

// Methods are run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceManagerTest {

    Organisation organisation = null;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Tests that a project can be saved then loaded. All in one test so that it works with the temp folder
     *
     * @throws Exception Exception
     */
    @Test
    public void testLoad_happyCase() throws Exception {
        organisation = new Organisation("p", "Project", testFolder.newFile("test.json"));
        PersistenceManager.saveProject(organisation.getSaveLocation(), organisation);
        Assert.assertTrue(new File(organisation.getSaveLocation() + "").exists());

        final Organisation loadedOrganisation = PersistenceManager.loadProject(new File(testFolder.getRoot() + "/test.json"));
        Assert.assertTrue(loadedOrganisation.equals(organisation));
    }

    /**
     * Tests that attempting to load a non-existent project file throws a {@link FileNotFoundException}.
     *
     * @throws Exception Exception
     */
    @Test
    public void testLoad_fileNotFound() throws Exception {
        thrown.expect(FileNotFoundException.class);

        final Organisation p = PersistenceManager.loadProject(new File("a/non/existent/file/path"));
    }

    /**
     * Tests that attempting to load a badly-formed project file throws a {@link JsonSyntaxException}.
     *
     * @throws Exception Exception
     */

    @Test
    public void testLoad_invalidProjectFormat() throws Exception {
        thrown.expect(JsonSyntaxException.class);
        final File f = testFolder.newFile("test.json");

        try (final FileWriter fw = new FileWriter(f)) {
            fw.write("{"); // lone opening brace == bad json
        }

        final Organisation p = PersistenceManager.loadProject(f);
        System.out.println(p);
    }
}
