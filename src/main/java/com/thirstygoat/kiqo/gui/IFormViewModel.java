package com.thirstygoat.kiqo.gui;

public interface IFormViewModel<T> extends Loadable<T> {
    public void setExitStrategy(Runnable exitStrategy);
}
