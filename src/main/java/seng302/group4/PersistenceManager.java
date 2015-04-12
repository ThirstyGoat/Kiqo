package seng302.group4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;

/**
 * Class for saving, loading, deleting etc Created by samschofield on 17/03/15.
 */
public class PersistenceManager {

    /**
     * Saves the given Project to the given filepath as project_shortname.json
     * FILE PATH MUST BE VALID
     *
     * @param filePath Path to the location where the project is to be saved
     * @param project Project to be saved
     */
    public static void saveProject(final File filePath, final Project project) throws IOException {
//        System.out.println(project);
//        System.out.println(project.people);
//        System.out.println(project.getPeople());
        GsonBuilder gsonBuilder = new GsonBuilder();
        // Turn me on baby -
        gsonBuilder.setPrettyPrinting();
        new GraphAdapterBuilder()
                .addType(Project.class)
                .addType(Team.class)
                .addType(Person.class)
                .addType(Skill.class)
                .registerOn(gsonBuilder);

        Gson gson = gsonBuilder.create();
        final Writer writer = new FileWriter(filePath);
        gson.toJson(project, writer);
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
     * @throws FileNotFoundException
     * @throws JsonIOException
     * @throws JsonSyntaxException
     */
    public static Project loadProject(final File filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        Project project = null;
        GsonBuilder gsonBuilder = new GsonBuilder();
        new GraphAdapterBuilder()
                .addType(Project.class)
                .addType(Team.class)
                .addType(Person.class)
                .addType(Skill.class)
                .registerOn(gsonBuilder);
        Gson gson = gsonBuilder.create();
        if (filePath != null) {
            final BufferedReader br = new BufferedReader(new FileReader(filePath));
            project = gson.fromJson(br, Project.class);
            project.setObservableLists();
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
        final File f = new File("/home/amy/Desktop/asdfg.json");
        final Project p = new Project("p", "project", f, "descr");
        try {
            PersistenceManager.saveProject(p.getSaveLocation(), p);
            System.out.println("saved project at: " + p.getSaveLocation());
            ;
        } catch (final IOException o) {
            o.printStackTrace();
            System.out.println("Couldnt save project. try again");
        }

        // final File projectLocation = new File(f.toString());
        final File projectLocation = new File("/home/amy/Desktop/uh-oh.json");
        System.out.println(projectLocation.toString());

        Project p1 = null;
        try {
            p1 = PersistenceManager.loadProject(projectLocation);
            System.out.println(p1.equals(p));
        } catch (final FileNotFoundException o) {
            o.printStackTrace();
            System.out.println("Couldnt find project. Try again");
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Json file is corrupt");
        }
    }
}