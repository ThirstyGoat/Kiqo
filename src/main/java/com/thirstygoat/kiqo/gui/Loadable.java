package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.model.Organisation;

public interface Loadable<T> {
    public void load(T t, Organisation organisation);
}
