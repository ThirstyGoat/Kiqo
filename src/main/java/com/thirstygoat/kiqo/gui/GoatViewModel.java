package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.Command;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;

/**
 * Created by leroy on 9/08/15.
 */
public interface GoatViewModel {
    public Command createCommand();
    public ValidationStatus allValidation();
}
