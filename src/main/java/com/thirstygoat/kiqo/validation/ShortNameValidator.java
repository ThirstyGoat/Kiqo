package com.thirstygoat.kiqo.validation;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by leroy on 6/09/15.
 */
public class ShortNameValidator<M extends Item> extends ObservableRuleBasedValidator {

    public static int MAX_LENGTH = 20;
    public static int MIN_LENGTH = 1;

    /**
     * A functional interface to define a supplier method of a {@link Collection} of objects which extend {@link Item}.
     * @param <M> The generic type of elements of the supplied list.
     */
    @FunctionalInterface
    public interface ItemSupplier<M> extends Supplier<Collection<M>> {
        @Override
        Collection<M> get();
    }

    /**
     * Create an ShortNameValidator. An mvvmFX {@link ObservableRuleBasedValidator} which checks that a shortName of an
     * item is unique within a given scope.
     *
     * @param shortNameProperty A string property containing the shortName to be validated.
     * @param item The item whose shortName uniqueness is to be validated.
     * @param itemSupplier A function which supplies a list of items among which the shortName must be unique.
     * @param scope The scope within which the shortName must be unique. For example, a project's shortName must be
     *              unique within an Organisation, so the scope would be "organisation".
     */
    public ShortNameValidator(StringProperty shortNameProperty, M item, ItemSupplier itemSupplier, String scope) {
        BooleanBinding shortNameIsUnique = Bindings.createBooleanBinding(() ->
                        Utilities.shortnameIsUnique(shortNameProperty.get(), item, itemSupplier.get()),
                shortNameProperty);
        this.addRule(shortNameProperty.isNotNull(), ValidationMessage.error("Name must not be empty"));
        this.addRule(shortNameProperty.length().greaterThanOrEqualTo(MIN_LENGTH),
                ValidationMessage.error("Name must not be empty"));
        this.addRule(shortNameProperty.length().lessThan(MAX_LENGTH), ValidationMessage.error("Name must be less than " +
                MAX_LENGTH + "characters"));
        this.addRule(shortNameIsUnique, ValidationMessage.error("Name must be unique within " + scope));
    }
}
