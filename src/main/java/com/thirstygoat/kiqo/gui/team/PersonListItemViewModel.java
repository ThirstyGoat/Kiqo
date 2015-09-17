package com.thirstygoat.kiqo.gui.team;

import javafx.beans.property.*;

import com.thirstygoat.kiqo.gui.person.PersonViewModel;
import com.thirstygoat.kiqo.model.Person;

public class PersonListItemViewModel extends PersonViewModel {
    public static enum Role {
        PRODUCT_OWNER("Product Owner", "blue"),
        SCRUM_MASTER("Scrum Master", "red"),
        DEVELOPMENT("Development", "green"),
        OTHER("Other", "grey");
        
        private String name, color;
        
        private Role(String name, String color) {
            this.name = name;
            this.color = color;
        }
        
        public String getName() {
            return name;
        }
        
        public String getStyle() {
            return "-fx-text-fill: " + color + ";";
        }
    }
    
    private ObjectProperty<Role> role;
    
    public PersonListItemViewModel() {
        role = new SimpleObjectProperty<>(Role.OTHER);
    }

    protected ObjectProperty<Role> roleProperty() {
        return role;
    }

    public Person getPerson() {
        return modelWrapper.get();
    }
}
