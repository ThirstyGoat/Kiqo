package com.thirstygoat.kiqo.sort;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by leroy on 17/09/15.
 */
public class AnchoredSortStrategyTest {

    @Test
    public void anchoredSortTest() {
        ArrayList<String> toSort = new ArrayList<>(Arrays.asList("Aben", "Carmen", "Benjamin"));
        SortStrategy<String, String, String> sortStrategy = new AnchoredSortStrategy<>();
        sortStrategy.setData("ben");
        sortStrategy.setComparableGetter(String::toString);
        ArrayList<String> sorted = new ArrayList<>(sortStrategy.sorted(toSort));
        Assert.assertEquals(sorted.get(0), "Benjamin");
        Assert.assertEquals(sorted.get(1), "Aben");
        Assert.assertEquals(sorted.get(2), "Carmen");
    }
}
