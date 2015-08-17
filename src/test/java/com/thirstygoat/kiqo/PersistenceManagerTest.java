package com.thirstygoat.kiqo;

import com.google.gson.JsonSyntaxException;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.persistence.PersistenceManager;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;


// Methods are run in alphabetical order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersistenceManagerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    Organisation organisation = null;

    /**
     * Tests that attempting to load a non-existent project file throws a {@link FileNotFoundException}.
     *
     * @throws Exception Exception
     */
    @Test
    public void testLoad_fileNotFound() throws Exception {
        thrown.expect(FileNotFoundException.class);

        PersistenceManager.loadOrganisation(new File("a/non/existent/file/path"));
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

        PersistenceManager.loadOrganisation(f);
    }

    @Test
    public void testLoad_sprint1File() {
        final File f = new File(getClass().getResource("/save_files/sprint1.json").getFile());
        Exception ex = null;
        try {
            PersistenceManager.loadOrganisation(f);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertTrue("Opening old save file should not raise an exception",
                ex == null);
    }

    @Test
    public void testLoad_sprint2File() {
        final File f = new File(getClass().getResource("/save_files/sprint2.json").getFile());
        Exception ex = null;
        try {
            PersistenceManager.loadOrganisation(f);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertTrue("Opening old save file should not raise an exception",
                ex == null);
    }

    @Test
    public void testLoad_sprint3File() {
        final File f = new File(getClass().getResource("/save_files/sprint3.json").getFile());
        Exception ex = null;
        try {
            PersistenceManager.loadOrganisation(f);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertTrue("Opening old save file should not raise an exception",
                ex == null);     }

    @Test
    public void testLoad_sprint4File() {
        final File f = new File(getClass().getResource("/save_files/sprint4.json").getFile());
        Exception ex = null;
        try {
            PersistenceManager.loadOrganisation(f);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertTrue("Opening old save file should not raise an exception",
                ex == null); }

    @Test
    public void testLoad_sprint5File() {
        final File f = new File(getClass().getResource("/save_files/sprint5.json").getFile());
        Exception ex = null;
        try {
            PersistenceManager.loadOrganisation(f);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertTrue("Opening old save file should not raise an exception",
                ex == null); }
}
