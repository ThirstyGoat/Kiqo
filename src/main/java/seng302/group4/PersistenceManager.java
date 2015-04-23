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
     * Saves the given Organisation to the given filepath as organisation_shortname.json FILE PATH MUST BE VALID
     *
     * @param filePath Path to the location where the organisation is to be saved
     * @param organisation Organisation to be saved
     * @throws IOException Cannot write to file
     */
    public static void saveOrganisation(final File filePath, final Organisation organisation) throws IOException {
//        System.out.println(organisation);
//        System.out.println(organisation.people);
//        System.out.println(organisation.getPeople());
        final GsonBuilder gsonBuilder = new GsonBuilder();
        // Turn me on baby -
         gsonBuilder.setPrettyPrinting();
        new GraphAdapterBuilder()
                .addType(Organisation.class)
.addType(Project.class)
                .addType(Team.class)
                .addType(Person.class)
                .addType(Skill.class)
.addType(Allocation.class)
                .registerOn(gsonBuilder);

        final Gson gson = gsonBuilder.create();
        try (final Writer writer = new FileWriter(filePath)) {
            gson.toJson(organisation, writer);
        }
        System.out.println("Saved organisation.");
    }

    /**
     * Loads the organisation from the given JSON file
     *
     * @param filePath - Path to the organisation.json
     * @return Organisation loaded from the organisation.json file in the organisation directory
     * @throws FileNotFoundException File does not exist, or insufficient permissions
     * @throws JsonIOException Internal JSON IO problem
     * @throws JsonSyntaxException Malformed JSON structure
     */
    public static Organisation loadOrganisation(final File filePath) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        Organisation organisation = null;
        final GsonBuilder gsonBuilder = new GsonBuilder();
        new GraphAdapterBuilder()
                .addType(Organisation.class)
.addType(Project.class)
                .addType(Team.class)
                .addType(Person.class)
                .addType(Skill.class)
.addType(Allocation.class)
                .registerOn(gsonBuilder);
        final Gson gson = gsonBuilder.create();
        if (filePath != null) {
            final BufferedReader br = new BufferedReader(new FileReader(filePath));
            organisation = gson.fromJson(br, Organisation.class);
            organisation.setObservableLists();
        }
        return organisation;
    }
}