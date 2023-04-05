package tk.zulfengaming.zulfbungee.spigot.elements.sections.networkvariable;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventNetworkVariable;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;

import java.util.List;
import java.util.Optional;

// Referenced from https://github.com/Ankoki/SkJade/blob/2.0/src/main/java/com/ankoki/skjade/elements/binflop/elements/SecBinflopCreate.java, a part of SkJade.
// (understanding how the Section API works is a little difficult!)

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

        ZulfBungeeSpigot plugin = ZulfBungeeSpigot.getPlugin();
        TriggerItem item = walk(event, false);

        plugin.getTaskManager().newAsyncTask(() -> {

            Thread.currentThread().setName("SecNetworkVariable");

            Optional<NetworkVariable> requestNetworkVariable = plugin.getConnectionManager()
                    .requestNetworkVariable(variableExpression.getName().toString(event));

            if (requestNetworkVariable.isPresent()) {
                ExprSecNetworkVariable.setNetworkVariable(requestNetworkVariable.get());
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
