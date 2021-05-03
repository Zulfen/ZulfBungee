package tk.zulfengaming.bungeesk.spigot.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import tk.zulfengaming.bungeesk.universal.utilclasses.skript.ProxyPlayer;

public class SkriptTypes {

    static {
        Classes.registerClass(new ClassInfo<>(ProxyPlayer.class, "proxyplayer")
                .user("proxyplayers?")
                .name("Proxy Player")
                .description("Represents a player on the Bungeecord network.")
                .defaultExpression(new EventValueExpression<>(ProxyPlayer.class))
                .parser(new Parser<ProxyPlayer>() {

                    @Override
                    public ProxyPlayer parse(String s, ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(ProxyPlayer proxyPlayer, int i) {
                        return proxyPlayer.getName();
                    }

                    @Override
                    public String toVariableNameString(ProxyPlayer proxyPlayer) {
                        return proxyPlayer.getName();
                    }

                    @Override
                    public String getVariableNamePattern() {
                        return "\\s+";
                    }
                }));
    }
}
