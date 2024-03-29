package com.zulfen.zulfbungee.spigot.elements.sections.networkvariable;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.event.events.EventNetworkVariable;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.managers.TaskManager;
import com.zulfen.zulfbungee.spigot.objects.PreparedNetworkVariable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class SecNetworkVariable extends Section {

    private Trigger trigger;

    private Variable<?> variableExpression;

    static {
        Skript.registerSection(SecNetworkVariable.class, "(using|load|get|with) (proxy|network|bungeecord|bungee|velocity) variable %objects% [[and] do]");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> list) {

        Expression<?> expression = expressions[0];

        if (getParser().isCurrentSection(SecNetworkVariable.class)) {
            Skript.error("You can't get another network variable inside this section!");
            return false;
        }

        if (expression instanceof Variable) {

            variableExpression = (Variable<?>) expressions[0];

            if (!variableExpression.isLocal()) {

                trigger = loadCode(sectionNode, "using network variable", EventNetworkVariable.class);
                return true;

            } else {
                Skript.error("A network variable cannot be a local variable!");
            }

        } else {
            return false;
        }

        return false;

    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {

        // copies local variables from outside this section
        Object localVars = Variables.copyLocalVariables(event);
        EventNetworkVariable dummy = new EventNetworkVariable();
        Variables.setLocalVariables(dummy, localVars);

        ConnectionManager<?> connectionManager = ZulfBungeeSpigot.getPlugin().getConnectionManager();
        TaskManager taskManager = ZulfBungeeSpigot.getPlugin().getTaskManager();

        TriggerItem item = walk(event, false);

        taskManager.newAsyncTask(() -> {

            Optional<PreparedNetworkVariable> requestNetworkVariable = connectionManager
                    .requestNetworkVariable(variableExpression.getName().toString(event), event);

            if (requestNetworkVariable.isPresent()) {
                PreparedNetworkVariable variable = requestNetworkVariable.get();
                ExprSecNetworkVariable.setNetworkVariable(variable);
            } else {
                ExprSecNetworkVariable.clear();
            }

            trigger.execute(dummy);


        });

        return item;

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return String.format("section network variable with %s", variableExpression.toString(event, b));
    }
}
