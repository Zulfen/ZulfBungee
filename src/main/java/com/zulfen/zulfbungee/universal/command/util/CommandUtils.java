package com.zulfen.zulfbungee.universal.command.util;

public class CommandUtils {

    public static String getScriptNameArgs(String[] argsIn) {

        StringBuilder scriptNameBuilder = new StringBuilder();

        for (int i = 0; i < argsIn.length; i++) {

            String arg = argsIn[i];

            scriptNameBuilder.append(arg);

            if (i != argsIn.length - 1) {
                scriptNameBuilder.append(" ");
            } else {
                if (!arg.endsWith(".sk")) {
                    scriptNameBuilder.append(".sk");
                }
            }

        }

        return scriptNameBuilder.toString();

    }
}
