

![zulfbungee](https://user-images.githubusercontent.com/38318382/180299758-f01f2373-6f47-4f24-ba1b-f69343c61424.png)


# **ZulfBungee - a proxy Skript addon!**

*The project is currently based around Minecraft 1.12.2 and Java 8 to ensure a level of legacy support, but will be dropped soon.*

**Supports Bungeecord and Velocity!**

Small portions of this plugin were referenced from another addon to aid development ([Skungee by LimeGlass](https://github.com/Skungee/Skungee-2.0.0)), and code referenced will be credited accordingly.

Building this addon by yourself might be a bit of a pain right now, sorry! I am currently planning on switching from Maven to Gradle. For the meantime, you can load the repo as an IntelliJ project and you should be able to build it from there.

## **Known Bugs:**

* Proper colour parsing from Skript might be a little broken - would require some code reshuffling to fix. (use & colour codes for now)
* Using `localhost` in Bungeecord configurations causes a false positive while doing the security check, meaning the proxy will ignore the connection. (fixing this!)

# **Supported Syntax:**

## **Effects:**

- (proxy|bungeecord|bungee) message player %-proxyplayers% [the message] %string%

- (proxy|bungeecord|bungee) message server %-proxyservers% [the message] %string% (named|called|with title) %string%

- (proxy|bungeecord|bungee) (send|transfer) %-proxyplayers% to %-proxyserver%

## **Events:**

- on (proxy|bungeecord|bungee) player connect

- on (proxy|bungeecord|bungee) player switch server

- on (bungeecord|bungee|proxy) server message [(titled|called)] %string%

- on (proxy|bungeecord|bungee) player disconnect

- on (proxy|bungeecord|bungee) player kick

## **Expressions:**

- [(all [[of] the]|the)] online [(proxy|bungeecord|bungee)] servers

- (proxy|network|bungeecord|bungee) variable %objects%

- proxyplayer's [(current|connected)] server[s]

- [(all [[of] the]|the)] (bungeecord|bungee|proxy) players [on %-proxyservers%]

- proxyserver's (player limit|max player count)

- this [script's] (server|client|proxy server)

## **Conditions:**

- if player %-proxyplayer% (1¦is|2¦is(n't| not)) online on the (proxy|bungeecord|bungee|network)

- if %-proxyserver% (1¦is|2¦is(n't| not)) online

# Commands / Permissions

- `zulfen.bungee.admin.script.load` /zulfbungee scripts load <name>
- `zulfen.bungee.admin.script.unload` /zulfbungee scripts unload <name>
- `zulfen.bungee.admin.script.unload` /zulfbungee scripts reload <name>

- `zulfen.bungee.admin.update.check` /zulfbungee update check
- `zulfen.bungee.admin.debug` /zulfbungee debug
- `zulfen.bungee.admin.ping` /zulfbungee ping
