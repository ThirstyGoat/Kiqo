package com.thirstygoat.kiqo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.*;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.model.Team;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class for saving, loading, deleting etc Created by samschofield on 17/03/15.
 */
public class PersistenceManager {
    private static Gson gson;
    private static boolean isOldJSON = false;
    /**
     * Saves the given Organisation to the given filepath as organisation_shortname.json FILE PATH MUST BE VALID
     *
     * @param filePath Path to the location where the organisation is to be saved
     * @param organisation Organisation to be saved
     * @throws IOException Cannot write to file
     */
    public static void saveOrganisation(final File filePath, final Organisation organisation) throws IOException {
        if (gson == null) {
            createGson(false);
        }

        try (final Writer writer = new FileWriter(filePath)) {
            JsonElement jsonElement = gson.toJsonTree(organisation);
            jsonElement.getAsJsonObject().addProperty("VERSION", 1.0);
            gson.toJson(jsonElement, writer);
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

        if (gson == null) {
            createGson(false);
        }

        if (filePath != null) {
            final BufferedReader br = new BufferedReader(new FileReader(filePath));

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(br);
            JsonElement version = jsonObject.get("VERSION");
            organisation = gson.fromJson(jsonObject, Organisation.class);

            // loading old json file
            if(version == null) {
                isOldJSON = true;
                final BufferedReader br1 = new BufferedReader(new FileReader(filePath));
                createGson(true);
                Organisation organisation2 = gson.fromJson(br1, Organisation.class);
                organisation.getProjects().setAll(organisation2.getProjects());
            }
        }

        return organisation;
    }

    /**
     * Creates the GSON object
     * @param isOldFile if the file is from a previous version or not (alters the typeAdapter for organisation
     */
    private static void createGson(boolean isOldFile) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        new GraphAdapterBuilder()
                .addType(Organisation.class)
                .addType(Project.class)
                .addType(Team.class)
                .addType(Person.class)
                .addType(Skill.class)
                .addType(Release.class)
                .addType(Allocation.class)
                .registerOn(gsonBuilder);

        gsonBuilder.registerTypeAdapter(ObservableList.class, new ObservableListDeserializer());
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyDeserializer());
        gsonBuilder.registerTypeAdapter(ObjectProperty.class, new ObjectPropertyDeserializer());

        if(isOldFile) {
            gsonBuilder.registerTypeAdapter(Organisation.class, new OrganisationDeserializer());
        }

        gson = gsonBuilder.create();
    }

    public static boolean getIsOldJSON() {
        return isOldJSON;
    }

    /**
     * Custom Deserializer for Organisation. Is used when loading a project from a json file for deliverable 1
     */
    private static class OrganisationDeserializer implements JsonDeserializer<Organisation> {

        /**
         * Used to deserialize the json from deliverable 1, is under Organisation because the old "project" is interpreted
         * as an "Organisation" by gson
         */
        @Override
        public Organisation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            // this organisation is just a container for project to comply with the return type
            Organisation org = new Organisation();

            Project p = jsonDeserializationContext.deserialize(jsonElement, Project.class);
            org.getProjects().add(p);
            return org;
        }
    }

    /**
     * Custom Deserializer for ObservableLists
     */
    private static class ObservableListDeserializer implements JsonDeserializer<ObservableList> {

        @Override
        public ObservableList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Type type = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];
            if (gson == null) {
                createGson(false);
            }

            ObservableList<Object> observableList =  FXCollections.observableArrayList();
            for (JsonElement element : json.getAsJsonArray()) {
                observableList.add(gson.fromJson(element, type));
            }
            return observableList;
        }
    }

    private static class StringPropertyDeserializer implements JsonDeserializer<StringProperty>, JsonSerializer<StringProperty> {
        @Override
        public StringProperty deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            return new SimpleStringProperty(jsonElement.getAsString());
        }

        @Override
        public JsonElement serialize(StringProperty s, Type type, JsonSerializationContext jsonSerializationContext) {
            if (s != null && s.get() != null) {
                return new JsonPrimitive(s.get());
            } else {
                return null;
            }
        }
    }

    private static class ObjectPropertyDeserializer implements JsonDeserializer<ObjectProperty>, JsonSerializer<ObjectProperty> {
        @Override
        public ObjectProperty deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            Type objectType = ((ParameterizedType)type).getActualTypeArguments()[0];
            if (gson == null) {
                createGson(false);
            }
            return new SimpleObjectProperty<>(gson.fromJson(jsonElement, objectType));
        }

        @Override
        public JsonElement serialize(ObjectProperty o, Type type, JsonSerializationContext jsonSerializationContext) {
            Type objectType = ((ParameterizedType)type).getActualTypeArguments()[0];
            if (o != null && o.get() != null) {
                if (gson == null) {
                    createGson(false);
                }
                return gson.toJsonTree(o.get(), objectType);
            } else {
                return null;
            }
        }
    }
}