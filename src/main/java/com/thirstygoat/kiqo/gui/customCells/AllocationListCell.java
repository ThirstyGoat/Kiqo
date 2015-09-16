package com.thirstygoat.kiqo.gui.customCells;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import com.thirstygoat.kiqo.gui.nodes.AllocationsTableViewController;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Organisation;

/**
 * Created by amy on 25/07/15.
 */
public class AllocationListCell extends TableCell<Allocation, String> {
    private AllocationsTableViewController vm;
    
    private ObjectProperty<Organisation> organisationProperty;

    public AllocationListCell(ObjectProperty<Organisation> organisationProperty, AllocationsTableViewController allocationsTableViewController) {
        super();
        this.organisationProperty = organisationProperty;
        vm = allocationsTableViewController;
    }

    public ObservableValue<Paint> getBadgeColorProperty(Allocation allocation) {
        ObjectProperty<Paint> colorProperty = new SimpleObjectProperty<>(getColor(allocation));

        ChangeListener<? super Object> listener = (observable, oldValue, newValue) -> {
            colorProperty.set(getColor(allocation));
        };

        allocation.getStartDateProperty().addListener(listener);
        allocation.getEndDateProperty().addListener(listener);

        return colorProperty;
    }
    
    /**
     * Converts model state directly into color.
     * This replaces CSS because Java code can't read CSS files and we need this information dynamically.
     * @param allocation model object for display
     * @return color representing color state
     */
    private Color getColor(Allocation allocation) {
        final Color color;
        if (!organisationProperty.get().getTeams().contains(allocation.getTeam())
                || !organisationProperty.get().getProjects().contains(allocation.getProject())) {
            color = Color.rgb(0xff, 0x33, 0x33);
        } else if (allocation.isCurrent()) {
            color = Color.rgb(0x45, 0xc3, 0x4b);
        } else if (allocation.isFuture()) {
            color = Color.rgb(0xff, 0xc1, 0x45);
        } else { // past
            color = Color.rgb(0x62, 0xc1, 0xff);
        }
        return color;
    }


    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        Allocation allocation = null;
        if (getTableRow() != null) {
            allocation = (Allocation) getTableRow().getItem();
        }

        if (!empty && allocation != null) {

            final HBox hbox = new HBox();
            final Rectangle badge = new Rectangle(10, 10);
            Text shortname = new Text(item);

            hbox.getChildren().addAll(badge, shortname);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setSpacing(10);
            hbox.setPadding(new Insets(0, 0, 0, 5));

            badge.fillProperty().bind(getBadgeColorProperty(allocation));

            badge.visibleProperty().set(true);
            badge.managedProperty().set(true);

            badge.visibleProperty().bind(vm.checkBoxSelectedProperty());
            badge.managedProperty().bind(vm.checkBoxSelectedProperty());

            setGraphic(hbox);
        } else {
            // clear
            setGraphic(null);
        }
    }
}
