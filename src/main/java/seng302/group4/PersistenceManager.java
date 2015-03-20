package seng302.group4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
     * Loads the project from the given json file
     *
     * @param filePath
     *            - Path to the project.json
     * @return Project loaded from the project.json file in the project
     *         directory
     * @throws IOException
     */
    public static Project loadProject(final File filePath) throws Exception {
        Project project = null;
        if (filePath != null) {
            final BufferedReader br = new BufferedReader(new FileReader(filePath));
            try {
                project = PersistenceManager.gson.fromJson(br, Project.class);
                ArrayList people = new ArrayList(project.getPeople());
                if (people.size() > 0) {
                    for (int i=0; i < people.size(); i+=1){
                        if(!(Validity.checkPersonValidity((Person)people.get(i), people.subList(i,people.size()-1)))) {
                            throw new Exception();
                        }


                    }
                }
            } catch (final Exception e) {
                throw e;
            }
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
