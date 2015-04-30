package seng302.group4;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by bradley on 27/03/15.
 */
public class Team extends Item {
    private ObservableList<Allocation> allocations = FXCollections.observableArrayList();
    private StringProperty shortName;
    private StringProperty description;
    private ObjectProperty<Person> productOwner = new SimpleObjectProperty<>();
    private ObjectProperty<Person> scrumMaster = new SimpleObjectProperty<>();
    private ObservableList<Person> teamMembers = FXCollections.observableArrayList();
    private ObservableList<Person> devTeam = FXCollections.observableArrayList();

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Team() {
        this.shortName = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
    }

    public Team(String shortName, String description, List<Person> teamMembers) {
        this.shortName = new SimpleStringProperty(shortName);
        this.description = new SimpleStringProperty(description);
        this.teamMembers.addAll(teamMembers);
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObjectProperty<Person> productOwnerProperty() {
        return productOwner;
    }

    public ObjectProperty<Person> scrumMasterProperty() {
        return scrumMaster;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Team{" + shortName);
        sb.append(", description=" + description);
        sb.append(", productOwner=");
        if (getProductOwner() != null) {
            sb.append(getProductOwner().getShortName());
        }
        sb.append(", scrumMaster=");
        if (getScrumMaster() != null) {
            sb.append(getScrumMaster().getShortName());
        }
        sb.append(", teamMembers=" + teamMembers);
        sb.append(", devTeam=" + devTeam);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public List<Person> getTeamMembers() {
        ArrayList<Person> arrayList = new ArrayList<>();
        arrayList.addAll(teamMembers);
        return arrayList;
    }

    public ObservableList<Person> observableTeamMembers() {
        return teamMembers;
    }

    public ObservableList<Person> observableDevTeam() {
        return devTeam;
    }

    public void setTeamMembers(List<Person> teamMembers) {
        this.teamMembers.clear();
        this.teamMembers.addAll(teamMembers);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Person getProductOwner() {
        return productOwner.get();
    }

    public void setProductOwner(Person productOwner) {
        this.productOwner.set(productOwner);
    }

    public Person getScrumMaster() {
        return scrumMaster.get();
    }

    public void setScrumMaster(Person scrumMaster) {
        this.scrumMaster.set(scrumMaster);
    }

    public List<Person> getDevTeam() {
        ArrayList<Person> arrayList = new ArrayList<>();
        arrayList.addAll(devTeam);
        return arrayList;
    }

    public void setDevTeam(List<Person> devTeam) {
        this.devTeam.clear();
        this.devTeam.addAll(devTeam);
    }

    public ObservableList<Allocation> getAllocations() {
        return allocations;
    }

}