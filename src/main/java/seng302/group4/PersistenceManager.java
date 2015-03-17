package seng302.group4;


import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class for saving and loading
 * Created by samschofield on 17/03/15.
 */
public class PersistenceManager {

    private static Gson gson = new GsonBuilder().create();

    /**
     * Saves the given Project to the given filepath as a directory with name Project.shortname
     * FILE PATH MUST BE VALID
     * @param filePath - the path to the location where the project is to be saved
     * @param project - the project to be saved
     */
    public static void saveProject(File filePath, Project project) throws IOException {
        String saveLocation = filePath.toString() + "/" + project.getShortName();
        File file = new File(saveLocation);
        file.mkdir();
        Writer writer = new FileWriter(saveLocation + "/" + project.getShortName() + ".json");

        if(file.exists()) {
            System.out.println("Project already exists.. Saving chagnes");
            gson.toJson(project, writer);
        } else {
            System.out.println("Creating new project‰");
            file.mkdir();
            gson.toJson(project, writer);
        }
        writer.close();
    }

    /**
     * Loads the project from the given json file
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Project loadProject(File filePath) {
        Project project = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            project = gson.fromJson(br, Project.class);
        } catch (IOException o) {
            o.printStackTrace();
        }
        return project;
    }

    public static void main(String args[]) {
        Project p = new Project("a", "a", new File("/Users/samschofield/Documents/Uni/2ndPro/seng302/SaveLocation"));
        try {
            PersistenceManager.saveProject(p.getSaveLocation(), p);
        } catch (IOException e) {
            System.out.println("didnt save");
        }
    }
}
