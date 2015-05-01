package seng302.group4;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng302.group4.utils.Utilities;

/**
 * Created by bradley on 27/03/15.
 */
public class Team extends Item {
    private final ObservableList<Allocation> allocations = FXCollections.observableArrayList();
    private final StringProperty shortName;
    private final StringProperty description;
    private final ObjectProperty<Person> productOwner = new SimpleObjectProperty<>();
    private final ObjectProperty<Person> scrumMaster = new SimpleObjectProperty<>();
    private final ObservableList<Person> teamMembers = FXCollections.observableArrayList();
    private final ObservableList<Person> devTeam = FXCollections.observableArrayList();

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Team() {
        shortName = new SimpleStringProperty();
        description = new SimpleStringProperty();
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
    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public List<Person> getTeamMembers() {
        final ArrayList<Person> arrayList = new ArrayList<>();
        arrayList.addAll(teamMembers);
        return arrayList;
    }

    public void setTeamMembers(List<Person> teamMembers) {
        this.teamMembers.clear();
        this.teamMembers.addAll(teamMembers);
    }

    public ObservableList<Person> observableTeamMembers() {
        return teamMembers;
    }

    public ObservableList<Person> observableDevTeam() {
        return devTeam;
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
        final ArrayList<Person> arrayList = new ArrayList<>();
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Team{" + getShortName());
        sb.append(", description=" + getDescription());
        sb.append(", productOwner=");
        if (getProductOwner() != null) {
            sb.append(getProductOwner().getShortName());
        }
        sb.append(", scrumMaster=");
        if (getScrumMaster() != null) {
            sb.append(getScrumMaster().getShortName());
        }
        sb.append(", teamMembers=" + Utilities.commaSeparatedValues(getTeamMembers()));
        sb.append(", devTeam=" + Utilities.commaSeparatedValues(getDevTeam()));
        sb.append('}');
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getShortName().hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Team)) {
            return false;
        }
        final Team other = (Team) obj;
        if (getShortName() == null) {
            if (other.getShortName() != null) {
                return false;
            }
        } else if (!getShortName().equals(other.getShortName())) {
            return false;
        }
        return true;
    }

}
