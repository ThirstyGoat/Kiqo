package com.thirstygoat.kiqo.model;

import java.util.*;

import com.thirstygoat.kiqo.search.SearchableField;
import com.thirstygoat.kiqo.util.Utilities;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.*;

/**
 * Created by bradley on 27/03/15.
 */
public class Team extends Item {
    private final ObservableList<Allocation> allocations = FXCollections.observableArrayList();
    private final StringProperty shortName;
    private final StringProperty description;
    private final ObjectProperty<Person> productOwner = new SimpleObjectProperty<>();
    private final ObjectProperty<Person> scrumMaster = new SimpleObjectProperty<>();
    private final ObservableList<Person> teamMembers = FXCollections.observableArrayList(person -> new Observable[] {person.shortNameProperty(), person.teamProperty()});
    private final ObservableList<Person> devTeam = FXCollections.observableArrayList(person -> new Observable[] {person.shortNameProperty(), person.teamProperty()});

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Team() {
        shortName = new SimpleStringProperty("");
        description = new SimpleStringProperty("");
    }

    public Team(String shortName, String description, List<Person> teamMembers) {
        this.shortName = new SimpleStringProperty(shortName);
        this.description = new SimpleStringProperty(description);
        this.teamMembers.addAll(teamMembers);
    }

    /**
     * @return a string array of the searchable fields for a model object
     */
    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchString = new ArrayList<>();
        searchString.addAll(Arrays.asList(new SearchableField("Short Name", getShortName()), new SearchableField("Description", getDescription())));
        searchString.add(new SearchableField("Members", Utilities.commaSeparatedValues(teamMembers)));
        if (productOwnerProperty().get() != null) searchString.add(new SearchableField("PO", getProductOwner().getShortName()));
        if (scrumMasterProperty().get() != null) searchString.add(new SearchableField("SM", getScrumMaster().getShortName()));
        return searchString;
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

    public ObservableList<Person> observableTeamMembers() {
    	return teamMembers;
    }
    
    public List<Person> getTeamMembers() {
    	List<Person> list = new ArrayList<>();
    	list.addAll(teamMembers);
    	return list;
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

    public ObservableList<Person> observableDevTeam() {
    	return devTeam;
    }
    
    public List<Person> getDevTeam() {
    	List<Person> list = new ArrayList<>();
    	list.addAll(devTeam);
    	return list;
    }

    public void setDevTeam(List<Person> devTeam) {
    	this.devTeam.clear();
        this.devTeam.addAll(devTeam);
    }

    public List<Allocation> getAllocations() {
        final ArrayList<Allocation> allocations1 = new ArrayList<>();
        allocations1.addAll(allocations);
        return allocations1;
//        return Collections.unmodifiableList(allocations);
    }

    public void setAllocations(List<Allocation> allocations) {
        this.allocations.setAll(allocations);
    }

    public ObservableList<Allocation> observableAllocations() {
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
        sb.append(", teamMembers=" + Utilities.commaSeparatedValues(observableTeamMembers()));
        sb.append(", devTeam=" + Utilities.commaSeparatedValues(observableDevTeam()));
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(shortName);
        bps.addPropertyChangeSupportFor(description);
        bps.addPropertyChangeSupportFor(productOwner);
        bps.addPropertyChangeSupportFor(scrumMaster);
        bps.addPropertyChangeSupportFor(teamMembers);
        bps.addPropertyChangeSupportFor(devTeam);
        bps.addPropertyChangeSupportFor(allocations);
    }
}
