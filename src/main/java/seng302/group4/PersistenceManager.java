package seng302.group4;


import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class for saving, loading, deleting etc
 * Created by samschofield on 17/03/15.
 */
public class PersistenceManager {

    private static Gson gson = new GsonBuilder().create();

    /**
     * Saves the given Project to the given filepath as project_shortname.json
     * FILE PATH MUST BE VALID
     * @param filePath - the path to the location where the project is to be saved
     * @param project - the project to be saved
     */
    public static void saveProject(File filePath, Project project) throws IOException {
        Writer writer = new FileWriter(filePath);
        gson.toJson(project, writer);
        writer.close();
    }

    /**
     * Loads the project from the given json file
     * @param filePath - Path to the project.json
     * @return Project loaded from the project.json file in the project directory
     * @throws IOException
     */
    public static Project loadProject(File filePath) throws Exception {
        Project project = null;
        if(filePath != null) {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            try {
                project = gson.fromJson(br, Project.class);
            } catch (Exception e) {
                throw e;
            }
        }
        return project;
    }

    /**
     * Basic testing for now to prove that it works
     * Not sure how we want to do it for unit testing
     * @param args
     */
    public static void main(String args[]) {
        File f = new File("/Users/samschofield/Documents/Uni/2ndPro/seng302/SaveLocation/");
        Project p = new Project("p", "project", f);
        try {
            PersistenceManager.saveProject(p.getSaveLocation(), p);
            System.out.println("saved project at: " + p.getSaveLocation());;
        } catch (IOException o) {
            System.out.println("Couldnt save project. try again");
        }

        File projectLocation = new File(f.toString() + "/" + p.getShortName() + ".json");
        System.out.println(projectLocation.toString());

        Project p1 = null;
        try {
            p1 = PersistenceManager.loadProject(projectLocation);
            System.out.println(p1.equals(p));
        } catch (FileNotFoundException o) {
            System.out.println("Couldnt find project. Try again");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Json file is corrupt");
        }
    }
}
