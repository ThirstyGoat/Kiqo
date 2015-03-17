package seng302.group4.undo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import seng302.group4.Project;

public class CreateProjectCommand implements Command<Project> {
    final Object[] args;
    private Constructor<Project> constructor; // TODO not null
    private Project project = null;

    CreateProjectCommand(final Object... args) {
        this.args = args;
        final Class[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        try {
            this.constructor = Project.class.getDeclaredConstructor(argTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public Project execute() {
        try {
            this.project = this.constructor.newInstance(this.args);
        } catch (final InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return this.project; // FIXME warn on null-ness
    }

    @Override
    public void undo() {
        // FIXME destroy
        this.project.prepareForDestruction();
        this.project = null;
    }
}
