package com.thirstygoat.kiqo.gui.team;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

import com.thirstygoat.kiqo.gui.person.PersonViewModel;
import com.thirstygoat.kiqo.model.Person;

public class PersonListItemViewModel extends PersonViewModel {
    public static enum Role {
        PRODUCT_OWNER("Product Owner", Color.BLUE),
        SCRUM_MASTER("Scrum Master", Color.RED),
        DEVELOPMENT("Development", Color.GREEN),
        OTHER("Other", Color.GREY);
        
        private String name;
        private Color color;
        
        private Role(String name, Color color) {
            this.name = name;
            this.color = color;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        private Color getColor() {
            return color;
        }
    }
    
    private ObjectProperty<Role> role;
    private ObjectBinding<Color> roleColor;
    
    public PersonListItemViewModel(Person person) {
    	this.load(person, null);
        role = new SimpleObjectProperty<>(Role.OTHER);
        roleColor = Bindings.createObjectBinding(
                () -> { return roleProperty().get().getColor(); }, 
                roleProperty());
    }

    protected ObjectProperty<Role> roleProperty() {
        return role;
    }
    
    protected ObjectBinding<Color> roleColorBinding() {
        return roleColor;
    }

    public Person getPerson() {
        return modelWrapper.get();
    }

    public static String getColorString(Role role) {
        return role.getColor().toString();
    }
}
