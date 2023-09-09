package com.zulfen.zulfbungee.spigot.elements.sections.networkvariable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.objects.PreparedNetworkVariable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprSecNetworkVariable extends SimpleExpression<Object> {

    private static PreparedNetworkVariable VARIABLE;

    static {
        Skript.registerExpression(ExprSecNetworkVariable.class, Object.class, ExpressionType.SIMPLE, "[the [(retrieved|loaded)]] [(proxy|network|bungeecord|bungee|velocity)] variable");
    }

    @Override
    protected Object[] get(@NotNull Event event) {
        if (VARIABLE != null) {
            Variable<Object> variable = Variable.newInstance(VARIABLE.getName(), VARIABLE.getClasses());
            return CollectionUtils.array(variable);
        } else {
            return null;
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Variable.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "network variable expression (section)";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {

        SkriptEvent event = getParser().getCurrentSkriptEvent();

        if (event instanceof SectionSkriptEvent) {

            SectionSkriptEvent sectionSkriptEvent = (SectionSkriptEvent) event;
            if (sectionSkriptEvent.isSection(SecNetworkVariable.class)) {
                return true;
            }

        }

        Skript.error("You can only use this expression in a network variable section!");
        return false;

    }

    @Override
    public Class<?>[] acceptChange(Changer.@NotNull ChangeMode mode) {

        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE) {
            return CollectionUtils.array(isSingle() ? Object.class : Object[].class);
        }

        return null;
    }

    @Override
    public void change(@NotNull Event e, Object[] delta, Changer.@NotNull ChangeMode mode) {
        if (VARIABLE != null) {
            ZulfBungeeSpigot.getPlugin().getConnectionManager().modifyNetworkVariable(delta, mode, VARIABLE.getName());
        }
    }

    protected synchronized static void clear() {
        VARIABLE = null;
    }

    protected synchronized static void setNetworkVariable(PreparedNetworkVariable varIn) {
        VARIABLE = varIn;
    }

}
