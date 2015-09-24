package com.thirstygoat.kiqo.persistence;

import com.google.gson.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.search.SearchableItems;
import com.thirstygoat.kiqo.util.ApplicationInfo;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for saving, loading, deleting etc Created by samschofield on 17/03/15.
 */
public class PersistenceManager {
    private static final Logger LOGGER = Logger.getLogger(PersistenceManager.class.getName());
    private static Gson gson;
    private static boolean isOldJSON = false;
    private static String revertVersion;
    /**
     * Saves the given Organisation to the given filepath as organisation_shortname.json FILE PATH MUST BE VALID
     *
     * @param filePath Path to the location where the organisation is to be saved
     * @param organisation Organisation to be saved
     * @throws IOException Cannot write to file
     */
    public static void saveOrganisation(final File filePath, final Organisation organisation) throws IOException {
        if (PersistenceManager.gson == null) {
            PersistenceManager.createGson(false);
        }

        try (final Writer writer = new FileWriter(filePath)) {
            final JsonElement jsonElement = PersistenceManager.gson.toJsonTree(organisation);
            jsonElement.getAsJsonObject().addProperty("VERSION", ApplicationInfo.getProperty("version"));
            PersistenceManager.gson.toJson(jsonElement, writer);
            revertVersion = gson.toJson(jsonElement);
        }
        PersistenceManager.LOGGER.log(Level.INFO, "Saved organisation to %s", filePath);
    }

    public static Organisation revert() {
        final JsonParser parser = new JsonParser();
        final JsonObject jsonObject = (JsonObject) parser.parse(revertVersion);
        final JsonElement version = jsonObject.get("VERSION");
        return PersistenceManager.gson.fromJson(jsonObject, Organisation.class);
    }

    /**
     * Loads the organisation from the given JSON file
     *
     * @param file - Path to the organisation.json
     * @return Organisation loaded from the organisation.json file in the organisation directory
     * @throws FileNotFoundException File does not exist, or insufficient permissions
     * @throws JsonIOException Internal JSON IO problem
     * @throws JsonSyntaxException Malformed JSON structure
     * @throws ClassCastException Problem casting the JsonParsers return value to a JsonObject. For example
     * if the JsonParser is parsing an empty file it will return a JsonNull object which cannot be cast to a JsonObject.
     */
    public static Organisation loadOrganisation(final File file) throws JsonIOException, JsonSyntaxException,
            ClassCastException, FileNotFoundException {
        Organisation organisation = null;

        if (PersistenceManager.gson == null) {
            PersistenceManager.createGson(false);
        }

        if (file != null) {
            final BufferedReader br = new BufferedReader(new FileReader(file));

            final JsonParser parser = new JsonParser();
            final JsonObject jsonObject = (JsonObject) parser.parse(br);
            final JsonElement version = jsonObject.get("VERSION");
            organisation = PersistenceManager.gson.fromJson(jsonObject, Organisation.class);
            revertVersion = gson.toJson(jsonObject);

            // loading old json file
            if (version == null) {
                // 1.0
                PersistenceManager.isOldJSON = true;
                final BufferedReader br1 = new BufferedReader(new FileReader(file));
                PersistenceManager.createGson(true);
                final Organisation organisation2 = PersistenceManager.gson.fromJson(br1, Organisation.class);
                organisation.getProjects().setAll(organisation2.getProjects());
                PersistenceManager.createGson(false);
            } else {
                String versionStr = version.getAsString();
                if (versionStr.equals("4.0")) {
                    List<Story> stories = new ArrayList<>();
                    organisation.getProjects().forEach(project1 -> {
                        stories.addAll(project1.getUnallocatedStories());
                        project1.getBacklogs().forEach(backlog -> stories.addAll(backlog.getStories()));
                    });
                    stories.forEach(story -> {
                        story.getTasks().forEach(task -> task.setStory(story));
                        story.getAcceptanceCriteria().forEach(acceptanceCriteria -> acceptanceCriteria.setStory(story));
                    });
                } else if (versionStr.equals("5.0")){
                    organisation.getProjects().forEach(project -> project.getReleases()
                                    .forEach(release -> release.getSprints()
                                    .forEach(sprint -> sprint.getStories().forEach(story -> story.setSprint(sprint)))));
                }
            }
        }
        if (organisation != null) {
            organisation.setSaveLocation(file);
            SearchableItems.getInstance().addAll(organisation);
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
                .addType(Backlog.class)
                .addType(Story.class)
                .addType(AcceptanceCriteria.class)
                .addType(Task.class)
                .addType(Sprint.class)
                .addType(Impediment.class)
                .addType(Effort.class)
                .registerOn(gsonBuilder);

        gsonBuilder.registerTypeAdapter(ObservableList.class, new ObservableListDeserializer());
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyDeserializer());
        gsonBuilder.registerTypeAdapter(IntegerProperty.class, new IntegerPropertyDeserializer());
        gsonBuilder.registerTypeAdapter(FloatProperty.class, new FloatPropertyDeserializer());
        gsonBuilder.registerTypeAdapter(ObjectProperty.class, new ObjectPropertyDeserializer());
        gsonBuilder.registerTypeAdapter(BooleanProperty.class, new BooleanPropertyDeserializer());

        if(isOldFile) {
            gsonBuilder.registerTypeAdapter(Organisation.class, new OrganisationDeserializer());
        }

        PersistenceManager.gson = gsonBuilder.create();
    }

    public static boolean getIsOldJSON() {
        return PersistenceManager.isOldJSON;
    }

    public static void resetIsOldJSON() {
        PersistenceManager.isOldJSON = false;
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
            final Organisation org = new Organisation();

            final Project p = jsonDeserializationContext.deserialize(jsonElement, Project.class);
            org.getProjects().add(p);

            return org;
        }
    }

    /**
     * Custom Deserializer for ObservableLists
     */
    private static class ObservableListDeserializer implements JsonDeserializer<ObservableList<?>> {

        @Override
        public ObservableList<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            final Type type = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];
            if (PersistenceManager.gson == null) {
                PersistenceManager.createGson(false);
            }

            ObservableList observableList;
            if (AcceptanceCriteria.class.isAssignableFrom((Class<?>) type)) {
                observableList = FXCollections.observableArrayList(AcceptanceCriteria.getWatchStrategy());
            } else if (Story.class.isAssignableFrom((Class<?>) type)) {
                observableList = FXCollections.observableArrayList(Story.getWatchStrategy());
            } else if (Task.class.isAssignableFrom((Class<?>) type)) {
                observableList = FXCollections.observableArrayList(Task.getWatchStrategy());
            } else if (Item.class.isAssignableFrom((Class<?>) type)) {
                observableList = FXCollections.observableArrayList(Item.getWatchStrategy());
            } else if (Effort.class.isAssignableFrom((Class<?>) type)) {
                observableList = FXCollections.observableArrayList(Effort.getWatchStrategy());
            } else {
                observableList = FXCollections.observableArrayList();
            }
            for (final JsonElement element : json.getAsJsonArray()) {
                observableList.add(PersistenceManager.gson.fromJson(element, type));
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

    private static class IntegerPropertyDeserializer implements JsonDeserializer<IntegerProperty>, JsonSerializer<IntegerProperty> {
        @Override
        public IntegerProperty deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            return new SimpleIntegerProperty(jsonElement.getAsInt());
        }

        @Override
        public JsonElement serialize(IntegerProperty s, Type type, JsonSerializationContext jsonSerializationContext) {
            if (s != null) {
                return new JsonPrimitive(s.get());
            } else {
                return null;
            }
        }
    }

    private static class FloatPropertyDeserializer implements JsonDeserializer<FloatProperty>, JsonSerializer<FloatProperty> {
        @Override
        public FloatProperty deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            return new SimpleFloatProperty(jsonElement.getAsFloat());
        }

        @Override
        public JsonElement serialize(FloatProperty floatProperty, Type type, JsonSerializationContext jsonSerializationContext) {
            if (floatProperty != null) {
                return new JsonPrimitive(floatProperty.get());
            } else {
                return null;
            }
        }
    }

    private static class BooleanPropertyDeserializer implements JsonDeserializer<BooleanProperty>, JsonSerializer<BooleanProperty> {

        @Override
        public BooleanProperty deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new SimpleBooleanProperty(jsonElement.getAsBoolean());
        }

        @Override
        public JsonElement serialize(BooleanProperty booleanProperty, Type type, JsonSerializationContext jsonSerializationContext) {
            if (booleanProperty != null) {
                return new JsonPrimitive(booleanProperty.get());
            } else {
                return null;
            }
        }
    }

    private static class ObjectPropertyDeserializer implements JsonDeserializer<ObjectProperty<?>>, JsonSerializer<ObjectProperty<?>> {
        @Override
        public ObjectProperty<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            final Type objectType = ((ParameterizedType)type).getActualTypeArguments()[0];
            if (PersistenceManager.gson == null) {
                PersistenceManager.createGson(false);
            }
            return new SimpleObjectProperty<>(PersistenceManager.gson.fromJson(jsonElement, objectType));
        }

        @Override
        public JsonElement serialize(ObjectProperty<?> o, Type type, JsonSerializationContext jsonSerializationContext) {
            final Type objectType = ((ParameterizedType)type).getActualTypeArguments()[0];
            if (o != null && o.get() != null) {
                if (PersistenceManager.gson == null) {
                    PersistenceManager.createGson(false);
                }
                return PersistenceManager.gson.toJsonTree(o.get(), objectType);
            } else {
                return null;
            }
        }
    }
}
