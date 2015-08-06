package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

public class GoatTreeItemTest {
    private static final SelectionModel<TreeItem<Item>> selectionModel = new SelectionModel<TreeItem<Item>>() {

        @Override
        public void clearAndSelect(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void clearSelection() {
            // TODO Auto-generated method stub

        }

        @Override
        public void clearSelection(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isSelected(int arg0) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void select(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void select(TreeItem<Item> arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void selectFirst() {
            // TODO Auto-generated method stub

        }

        @Override
        public void selectLast() {
            // TODO Auto-generated method stub

        }

        @Override
        public void selectNext() {
            // TODO Auto-generated method stub

        }

        @Override
        public void selectPrevious() {
            // TODO Auto-generated method stub

        }
    };
    private static final Comparator<Item> comparator = (o1, o2) -> {
        return ((MockObject) o1).getShortName().compareToIgnoreCase(((MockObject) o2).getShortName());
    };
    // some tests assume this array is in sorted order
    private MockObject[] reservoir;
    private ObservableList<MockObject> observableList;
    private GoatTreeItem<MockObject> goatTreeItem;

    @Before
    public void setUp() throws Exception {
        reservoir = new MockObject[] {
                new MockObject("a"),
                new MockObject("b"),
                new MockObject("c"),
                new MockObject("d")
        };
        observableList = FXCollections.observableArrayList(Item.getWatchStrategy());
        goatTreeItem = new GoatTreeItem<MockObject>("Tests", GoatTreeItemTest.selectionModel, GoatTreeItemTest.comparator);
        goatTreeItem.setItems(observableList);
    }

    @After
    public void tearDown() throws Exception {
        goatTreeItem = null;
    }

    @Test
    public void test_init() {
        Assert.assertTrue(observableList.isEmpty());
        Assert.assertTrue(goatTreeItem.getChildren().isEmpty());
        Assert.assertEquals("Tests", goatTreeItem.getValue().getShortName());
    }

    @Test
    public void test_add() {
        // order of insertion: add to empty list, prepend, append, insert
        for (final int i : new int[] {1, 0, 3, 2} ) {
            observableList.add(reservoir[i]);
            assertListsAreEquivalent();
        }
        Assert.assertArrayEquals(reservoir, getItemsInGoatTreeItem());
    }

    @Test
    public void test_update() {
        observableList.addAll(reservoir);

        // move something to the start of the list
        reservoir[2].shortNameProperty().set("1");

        // assert new item is at start
        Assert.assertEquals(reservoir[2], goatTreeItem.getChildren().get(0).getValue());
        // assert ex-first item is displaced (not overwritten)
        Assert.assertEquals(reservoir[0], goatTreeItem.getChildren().get(1).getValue());

        assertListsAreEquivalent();
    }

    @Test
    public void test_replaceList() {
        // fill up the existing list
        observableList.addAll(reservoir);
        // replace the list with a reference to an empty one (mimics "File > Load")
        observableList = FXCollections.observableArrayList(Item.getWatchStrategy());
        goatTreeItem.setItems(observableList);

        Assert.assertTrue(observableList.isEmpty());
        Assert.assertTrue(goatTreeItem.getChildren().isEmpty());
    }

    /**
     * Asserts that the backing list and the treeview's children have the same content and sort order. Note that the backing list is not assumed to be sorted.
     */
    private void assertListsAreEquivalent() {
        Assert.assertArrayEquals(getItemsInObservableListSorted(), getItemsInGoatTreeItem());
    }

    /**
     * @return
     */
    private Object[] getItemsInObservableListSorted() {
        return observableList.sorted((MockObject o1, MockObject o2) -> {
            return GoatTreeItemTest.comparator.compare(o1, o2);
        }).toArray();
    }

    /**
     * @return
     */
    private Item[] getItemsInGoatTreeItem() {
        final ObservableList<TreeItem<Item>> children = goatTreeItem.getChildren();
        final int size = children.size();
        final Item[] items = new MockObject[size];
        for (int i = 0; i < size; i++){
            items[i] = children.get(i).getValue();
        }
        return items;
    }

    private static class MockObject extends Item {
        private final StringProperty shortNameProperty;
        public MockObject(String label) {
            shortNameProperty = new SimpleStringProperty(label);
        }

        @Override
        public String getShortName() {
            return shortNameProperty.get();
        }

        @Override
        public StringProperty shortNameProperty() {
            return shortNameProperty;
        }

        @Override
        public String toString() {
            return getShortName();
        }
    }
}
