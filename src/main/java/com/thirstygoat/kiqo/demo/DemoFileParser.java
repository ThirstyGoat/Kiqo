package com.thirstygoat.kiqo.demo;

import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by leroy on 7/10/15.
 */
public class DemoFileParser {
    JSONObject data;

    public DemoFileParser(String filePath) {

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(filePath));
            data = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            System.out.println("Could not open or parse JSON file!");
            System.exit(1);
        }
    };

    public List<Project> getProjects() {
        JSONArray projects = (JSONArray) data.get("projects");
        return castToJsonObjects.apply(projects).stream()
                        .map(parseProject)
                        .collect(Collectors.toList());
    }

    Function<ArrayList<?>, ArrayList<JSONObject>> castToJsonObjects = jsonArrays -> {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        jsonObjects.addAll(jsonArrays.stream().map(jsonArray -> (JSONObject) jsonArray).collect(Collectors.toList()));
        return jsonObjects;
    };

    Function<JSONObject, Project> parseProject = project -> {
        String description = (String) project.get("description");
        String shortName = (String) project.get("name");
        return new Project(shortName, "", description);
    };

    public List<Team> getTeams(HashMap<String, Person> personIdMap) {
        List<Team> teams = new ArrayList<>();

        JSONArray projects = (JSONArray) data.get("projects");
        castToJsonObjects.apply(projects).forEach(project -> {
            Team team = new Team();
            JSONObject root = (JSONObject) project.get("root");
            team.setShortName(((String) root.get("name")).substring(2));

            JSONArray assignees = (JSONArray) project.get("assignees");
            List<Person> teamMembers = castToJsonObjects.apply(assignees).stream()
                            .map(assignee -> personIdMap.get((String) assignee.get("initials")))
                            .collect(Collectors.toList());
            team.observableTeamMembers().addAll(teamMembers);
            team.observableDevTeam().addAll(teamMembers);
            teams.add(team);
        });
        return teams;
    };
}
