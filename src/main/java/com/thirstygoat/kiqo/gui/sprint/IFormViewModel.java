package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;

public interface IFormViewModel<T> extends Loadable<T> {
    public Command createCommand();
    public void setExitStrategy(Runnable exitStrategy);
}
