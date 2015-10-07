package com.thirstygoat.kiqo.demo;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.create.CreateProjectCommand;
import com.thirstygoat.kiqo.command.create.CreateTeamCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by leroy on 6/10/15.
 */
public class Demo {
    private HashMap<String, Person> personIdMap = new LinkedHashMap<>();
    private Organisation organisation;

    public void init() {
        if (UndoManager.getUndoManager().getMainController() != null) {
            organisation = UndoManager.getUndoManager().getMainController().selectedOrganisationProperty.get();
        } else {
            organisation = new Organisation();
        }
        organisation.getPeople().stream()
                        .forEach(person -> personIdMap.put(person.getUserId(), person));
        parseDemoJson();
    }

    private void parseDemoJson() {
        DemoFileParser parser = new DemoFileParser("./seng302-2015.json");

//        parser.getProjects().stream().forEach(project -> UndoManager.getUndoManager()
//                        .doCommand(new CreateProjectCommand(project, organisation)));
//
//        parser.getTeams(personIdMap).stream().forEach(
//                        team -> UndoManager.getUndoManager().doCommand(new CreateTeamCommand(team, organisation)));

//        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(5), new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                UndoManager.getUndoManager().redoCommand();
//            }
//        }));
//        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
//        fiveSecondsWonder.play();
    }

}
