package seng302.group4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;

import com.google.gson.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sun.management.MethodInfo;

/**
 * Class for saving, loading, deleting etc Created by samschofield on 17/03/15.
 */
public class PersistenceManager {
    private static Gson gson;
    /**
     * Saves the given Organisation to the given filepath as organisation_shortname.json FILE PATH MUST BE VALID
     *
     * @param filePath Path to the location where the organisation is to be saved
     * @param organisation Organisation to be saved
     * @throws IOException Cannot write to file
     */
    public static void saveOrganisation(final File filePath, final Organisation organisation) throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        // Turn me on baby -
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

        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyDeserializer());
        gsonBuilder.registerTypeAdapter(ObjectProperty.class, new ObjectPropertyDeserializer());

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
                .addType(Release.class)
                .addType(Allocation.class)
                .registerOn(gsonBuilder);

        gsonBuilder.registerTypeAdapter(ObservableList.class, new ObservableListDeserializer());
        gsonBuilder.registerTypeAdapter(StringProperty.class, new StringPropertyDeserializer());
        gsonBuilder.registerTypeAdapter(ObjectProperty.class, new ObjectPropertyDeserializer());
        gson = gsonBuilder.create();
        if (filePath != null) {
            final BufferedReader br = new BufferedReader(new FileReader(filePath));
            organisation = gson.fromJson(br, Organisation.class);
        }
        return organisation;
    }

    /**
     * Custom Deserializer for ObservableLists
     */
    private static class ObservableListDeserializer implements JsonDeserializer<ObservableList> {

        @Override
        public ObservableList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Type type = ((ParameterizedType)typeOfT).getActualTypeArguments()[0];

            ObservableList<Object> observableList =  FXCollections.observableArrayList();
            for (JsonElement element : json.getAsJsonArray()) {
                observableList.add(gson.fromJson(element, type));
            }
            return observableList;
        }
    }

    private static class StringPropertyDeserializer implements JsonDeserializer, JsonSerializer<StringProperty> {
        @Override
        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            return new SimpleStringProperty(jsonElement.getAsString());
        }

        @Override
        public JsonElement serialize(StringProperty s, Type type, JsonSerializationContext jsonSerializationContext) {
            return gson.toJsonTree(s.get(), String.class);
        }
    }

    private static class ObjectPropertyDeserializer implements JsonDeserializer, JsonSerializer<ObjectProperty> {
        @Override
        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            Type objectType = ((ParameterizedType)type).getActualTypeArguments()[0];
            return new SimpleObjectProperty<>(gson.fromJson(jsonElement, objectType));
        }

        @Override
        public JsonElement serialize(ObjectProperty o, Type type, JsonSerializationContext jsonSerializationContext) {
            Type objectType = ((ParameterizedType)type).getActualTypeArguments()[0];
            return gson.toJsonTree(o.get(), objectType);
        }
    }
}