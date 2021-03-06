package org.jboss.forge.arquillian.commandcompleter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.arquillian.container.Container;
import org.jboss.forge.arquillian.container.ContainerDirectoryParser;
import org.jboss.forge.arquillian.container.ContainerType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.completer.CommandCompleterState;
import org.jboss.forge.shell.completer.SimpleTokenCompleter;

/**
 * @Author Paul Bakker - paul.bakker.nl@gmail.com
 */
public class ContainerCommandCompleter extends SimpleTokenCompleter {

    public static final String OPTION_CONTAINER_TYPE = "containerType";

    @Inject
    private ContainerDirectoryParser parser;

    @Inject
    private Shell shell;

    private CommandCompleterState state;

    @Override
    public void complete(CommandCompleterState state) {
        this.state = state;
        super.complete(state);
    }

    @Override
    public Iterable<?> getCompletionTokens() {
        List<Container> containers = null;
        try {
            containers = parser.getContainers();
            ContainerType containerType = ContainerType.valueOf(getInformedContainerType());
            List<Container> filtered = new ArrayList<Container>();
            for (Container container : containers) {
                if (container.getContainerType() == containerType) {
                    filtered.add(container);
                }
            }
            return filtered;
        } catch (IndexOutOfBoundsException e) {
            // If not containerType was informed
            return containers;
        } catch (IOException e) {
            ShellMessages.error(shell, e.getMessage());
            return null;
        }
    }

    /**
     * Get the value of containerType command option
     * 
     * @return informed ContainerType as String
     */
    private String getInformedContainerType() {
        String completeCommand = state.getBuffer();
        String[] splitedCommand = completeCommand.split("[\\s]++"); // split by one or more whitespaces
        int cont = 0;
        for (String token : splitedCommand) {
            cont++;
            if (("--" + OPTION_CONTAINER_TYPE).equals(token)) {
                break;
            }
        }
        return splitedCommand[cont];
    }
}
