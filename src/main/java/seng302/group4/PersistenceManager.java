package seng302.group4;


import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by samschofield on 17/03/15.
 */
public class PersistenceManager {

    /**
     * Saves the given Project to the given filepath as a directory with name Project.shortname
     * @param filePath - the path to the location where the project is to be saved
     * @param project - the project to be saved
     */
    public static void saveProject(String filePath, Project project) throws IOException{
        filePath += "/" + project.getShortName();
        File file = new File(filePath);

        Gson gson = new GsonBuilder().create();
        Writer writer = new FileWriter(filePath + "/" + project.getShortName() + ".json");

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
    public static Project loadProject(File filePath) throws IOException{
        Gson gson = new GsonBuilder().create();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        return gson.fromJson(br, Project.class);
    }


    public static void main(String args[]) {
//        Project p = new Project("p1", "Project1", new File("/aaaa"));
        try {
//            PersistenceManager.saveProject("/Users/samschofield/Documents/Uni/2ndPro/seng302/SaveLocation", p);
            Project p = PersistenceManager.loadProject(new File("/Users/samschofield/Documents/Uni/2ndPro/seng302/SaveLocation/p1/p1.json"));
            System.out.println(p.getShortName());
        } catch (IOException o) {

        }
    }

}
