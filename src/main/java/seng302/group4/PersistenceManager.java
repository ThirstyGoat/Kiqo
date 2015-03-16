package seng302.group4;


import java.io.File;

/**
 * Created by samschofield on 17/03/15.
 */
public class PersistenceManager {

    /**
     * Saves the given Project to the given filepath as a directory with name Project.shortname
     * @param filePath - the path to the location where the project is to be saved
     * @param project - the project to be saved
     */
    public static void saveProject(String filePath, Project project) {
        filePath += "/" + project.getShortName();
        File f = new File(filePath);


        if(f.exists()) {
            System.out.println("Project already exists.. Saving chagnes");
        } else {
            System.out.println("Creating new project‰");
            f.mkdir();
        }
    }


    public static void main(String args[]) {
        Project p = new Project("p1", "Project1", new File("/aaaa"));
        PersistenceManager.saveProject("/Users/samschofield/Documents/Uni/2ndPro/seng302/SaveLocation", p);

    }

}
