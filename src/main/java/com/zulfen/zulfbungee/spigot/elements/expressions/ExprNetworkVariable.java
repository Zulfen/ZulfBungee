package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.objects.PreparedNetworkVariable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;

import java.util.Optional;


// Some code I have written here has been referenced from Skungee 2.0.0:
// https://github.com/Skungee/Skungee-2.0.0/blob/master/src/main/java/com/skungee/spigot/elements/expressions/ExprNetworkVariable.java

public class ExprNetworkVariable extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprNetworkVariable.class, Object.class, ExpressionType.SIMPLE, "(proxy|network|bungeecord|bungee|velocity) variable %objects%");
    }

    private Variable<?> givenVariable;

    @Override
    protected Object[] get(@NotNull Event event) {

        ConnectionManager<?> connectionManager = ZulfBungeeSpigot.getPlugin().getConnectionManager();

        Optional<PreparedNetworkVariable> variableOptional = connectionManager.requestNetworkVariable(givenVariable.getName().toString(event), event);
        if (variableOptional.isPresent()) {
            PreparedNetworkVariable preparedVar = variableOptional.get();
            return preparedVar.getData();
        }

        return null;

    }

    @Override
    public boolean isSingle() {
        return givenVariable.isSingle();
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return givenVariable.toString(event, b);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {

        Expression<?> expression = expressions[0];

        if (expression instanceof Variable) {
            givenVariable = (Variable<?>) expressions[0];
        } else {
            return false;
        }

        if (givenVariable.isLocal()) {
            Skript.error("A network variable cannot be a local variable!");
            return false;
        }

        return true;
    }

    @Override
    public void change(@NotNull Event e, Object[] delta, Changer.@NotNull ChangeMode mode) {
        ZulfBungeeSpigot.getPlugin().getConnectionManager().modifyNetworkVariable(delta, mode, givenVariable.getName().toString(e));
    }

    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {

        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(isSingle() ? Object.class : Object[].class);
        }

        return null;
    }
}
