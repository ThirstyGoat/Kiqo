package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.Command;

/**
 * Created by Carina Blair on 14/08/2015.
 */
public interface GoatViewModel {

    public void load();

    public void reset();

    public void reload();

    public Command getCommand();


}
