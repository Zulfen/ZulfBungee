**ZulfBungee - a bungeecord Skript addon!**

Inspired by Limeglass' work on Skungee, but custom coded for my network.

**Known Bugs:**

* None right now.

**Supported Syntax:**

**Effect:**

- message (proxy|network|bungeecord|bungee) player %-proxyplayers% [the message] %string%

- message (proxy|network|bungeecord|bungee) server %-proxyservers% [the message] %string% (named|called|with title) %string%

**Events:**

- on (proxy|bungeecord|bungee) player connect

- on (proxy|bungeecord|bungee) player switch server

- on (bungeecord|bungee|proxy) server message %string%

- on (proxy|bungeecord|bungee) player disconnect

- on (proxy|bungeecord|bungee) player kick

**Expressions:**

- [(all [[of] the]|the)] online [(proxy|bungeecord|bungee)] servers

- (proxy|network|bungeecord|bungee) variable %objects%

- proxyplayer's [(current|connected)] server[s]

- [(all [[of] the]|the)] (bungeecord|bungee|proxy) players [on %-proxyservers%]

- (proxy|bungeecord|bungee) server %string%

- proxyserver's (player limit|max player count)

- [the] name of this [script's] server

**Conditions:**

- if (proxy|bungeecord|bungee) player %-proxyplayer% (1¦is|2¦is(n't| not)) online

- if %-proxyserver% (1¦is|2¦is(n't| not)) online

