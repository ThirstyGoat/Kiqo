package seng302.group4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import seng302.group4.exceptions.InvalidJSONException;
import seng302.group4.exceptions.InvalidPersonException;
import seng302.group4.exceptions.InvalidProjectException;

import java.io.*;
import java.util.ArrayList;

/**
 * Class for saving, loading, deleting etc Created by samschofield on 17/03/15.
 */
public class PersistenceManager {

    private static Gson gson = new GsonBuilder().create();

    /**
     * Saves the given Project to the given filepath as project_shortname.json
     * FILE PATH MUST BE VALID
     *
     * @param filePath
     *            - the path to the location where the project is to be saved
     * @param project
     *            - the project to be saved
     */
    public static void saveProject(final File filePath, final Project project) throws IOException {
        final Writer writer = new FileWriter(filePath);
        PersistenceManager.gson.toJson(project, writer);
        writer.close();
        System.out.println("Saved project.");
    }

    /**
     * Loads a project from a json file and checks it for validity
     * @param filePath - The filepath to load the project from
     * @return The project that was loaded
     * @throws FileNotFoundException if file path can not be read by the buffered reader
     * @throws InvalidPersonException if one of the people in the project is invalid
     * @throws InvalidJSONException if th json file is corrupt
     */
    public static Project loadProject(final File filePath) throws FileNotFoundException, InvalidProjectException, InvalidPersonException, JsonSyntaxException {
        Project project = null;
        if (filePath != null) {
            final BufferedReader br = new BufferedReader(new FileReader(filePath));

            project = PersistenceManager.gson.fromJson(br, Project.class);
            Validity.checkPeople(project.getPeople());
            Validity.checkProject(project);

        }
        return project;
    }

    /**
     * Basic testing for now to prove that it works Not sure how we want to do
     * it for unit testing
     *
     * @param args
     */
    public static void main(final String args[]) {
        final File f = new File("/Users/samschofield/Documents/Uni/2ndPro/seng302/SaveLocation/");
        final Project p = new Project("p", "project", f);
        try {
            PersistenceManager.saveProject(p.getSaveLocation(), p);
            System.out.println("saved project at: " + p.getSaveLocation());
            ;
        } catch (final IOException o) {
            System.out.println("Couldnt save project. try again");
        }

        final File projectLocation = new File(f.toString() + "/" + p.getShortName() + ".json");
        System.out.println(projectLocation.toString());

        Project p1 = null;
        try {
            p1 = PersistenceManager.loadProject(projectLocation);
            System.out.println(p1.equals(p));
        } catch (final FileNotFoundException o) {
            System.out.println("Couldnt find project. Try again");
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Json file is corrupt");
        }
    }
}
