**BungeeSk - a bungeecord Skript addon!**

Inspired by Limeglass' work on Skungee, but custom coded for my network.
This plugin is currently *closed source* for now!

**Known Bugs:**

* Closing socket too quickly can throw some exceptions on the client side, but fixes itself. This can be observed in the routine that checks for duplicate server names.

**Supported Syntax:**

message bungeecord player %-proxyplayers% [the message] %string% **(effect)**
message (proxy|network|bungeecord) server %-proxyservers% [the message] %string% **(effect)**

player switch server **(event)**
server message %string% **(event)**

[(all [[of] the]|the)] bungeecord players **(expression)**
(proxy|network|bungeecord) variable %objects% **(expression)**
[(all [[of] the]|the)] online servers **(expression)**

proxy server %-proxyserver% (1¦is|2¦is(n't| not)) online **(condition)**
