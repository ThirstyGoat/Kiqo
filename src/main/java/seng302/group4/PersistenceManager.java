package seng302.group4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Class for saving, loading, deleting etc Created by samschofield on 17/03/15.
 */
public class PersistenceManager {

    /**
     * Saves the given Project to the given filepath as project_shortname.json FILE PATH MUST BE VALID
     *
     * @param filePath Path to the location where the project is to be saved
     * @param project Project to be saved
     * @throws IOException Cannot write to file
     */
    public static void saveProject(final File filePath, final Project project) throws IOException {
//        System.out.println(project);
//        System.out.println(project.people);
//        System.out.println(project.getPeople());
        final GsonBuilder gsonBuilder = new GsonBuilder();
        // Turn me on baby - gsonBuilder.setPrettyPrinting();
        new GraphAdapterBuilder()
                .addType(Project.class)
                .addType(Team.class)
                .addType(Person.class)
                .addType(Skill.class)
                .registerOn(gsonBuilder);

        final Gson gson = gsonBuilder.create();
        try (final Writer writer = new FileWriter(filePath)) {
            gson.toJson(project, writer);
        }
        System.out.println("Saved project.");
    }

    /**
     * Loads the project from the given JSON file
     *
     * @param filePath - Path to the project.json
     * @return Project loaded from the project.json file in the project directory
     * @throws FileNotFoundException File does not exist, or insufficient permissions
     * @throws JsonIOException Internal JSON IO problem
     * @throws JsonSyntaxException Malformed JSON structure
     */
    public static Project loadProject(final File filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        Project project = null;
        final GsonBuilder gsonBuilder = new GsonBuilder();
        new GraphAdapterBuilder()
                .addType(Project.class)
                .addType(Team.class)
                .addType(Person.class)
                .addType(Skill.class)
                .registerOn(gsonBuilder);
        final Gson gson = gsonBuilder.create();
        if (filePath != null) {
            final BufferedReader br = new BufferedReader(new FileReader(filePath));
            project = gson.fromJson(br, Project.class);
            project.setObservableLists();
        }
        return project;
    }
}