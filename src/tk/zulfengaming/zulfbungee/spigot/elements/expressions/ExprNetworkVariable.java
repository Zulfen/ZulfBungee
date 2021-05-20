package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.SerializedVariable;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.Value;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

// Just wanted to put a little comment here to show appreciation for LimeGlass' work on Skungee 2.0.
// Some of the code I have written here has been referenced from their project, which I will link accordingly:
// https://github.com/Skungee/Skungee-2.0.0/blob/master/src/main/java/com/skungee/spigot/elements/expressions/ExprNetworkVariable.java



public class ExprNetworkVariable extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprNetworkVariable.class, Object.class, ExpressionType.SIMPLE, "(proxy|network|bungeecord) variable %objects%");
    }

    private Variable<?> networkVariable;

    @Override
    protected Object[] get(Event event) {
        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        try {
            Optional<Packet> response = connection.send(new Packet(PacketTypes.NETWORK_VARIABLE_GET, false, false, networkVariable.getName().toString(event)));

            if (response.isPresent()) {
                Packet packetIn = response.get();

                NetworkVariable variable = (NetworkVariable) packetIn.getDataSingle();

                return Stream.of(variable.getValueArray())
                        .map(value -> Classes.deserialize(value.type, value.data))
                        .toArray(Object[]::new);

            }

        } catch (InterruptedException ignored) {

        }

        return null;
    }

    @Override
    public boolean isSingle() {
        return !networkVariable.isList();
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return networkVariable.toString(event, b);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Expression<?> expression = expressions[0];

        if (expression instanceof Variable) {
            networkVariable = (Variable<?>) expressions[0];
        }

        if (networkVariable == null) {
            Skript.error("Variable passed in does not exist!");
            return false;
        } else if (networkVariable.isLocal()) {
            Skript.error("A network variable cannot be a local variable!");
            return false;
        }

        return true;
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        if (mode == null) {
            return;
        }

        ArrayList<Value> valuesOut = new ArrayList<>();

        if (delta != null && !mode.equals(Changer.ChangeMode.DELETE)) {
            for (Object o : delta) {
                SerializedVariable.Value value = Classes.serialize(o);
                valuesOut.add(new Value(value.type, value.data));
            }
        }

        NetworkVariable variableOut = new NetworkVariable(networkVariable.getName().toString(e), mode.name(), valuesOut.toArray(new Value[0]));
        connection.send_direct(new Packet(PacketTypes.NETWORK_VARIABLE_MODIFY, false, false, variableOut));

    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.ADD) {
            return CollectionUtils.array(isSingle() ? Object.class : Object[].class);
        }

        return null;
    }
}
