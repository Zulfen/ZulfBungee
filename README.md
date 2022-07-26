

![zulfbungee](https://user-images.githubusercontent.com/38318382/180299758-f01f2373-6f47-4f24-ba1b-f69343c61424.png)


# **ZulfBungee - a proxy Skript addon!**

*The project is currently based around Minecraft 1.12.2 and Java 8 to ensure a level of legacy support, but will be dropped soon.*

Small portions of this plugin were referenced from another addon to aid development ([Skungee by LimeGlass](https://github.com/Skungee/Skungee-2.0.0)), and code referenced will be credited accordingly.

Building this addon by yourself might be a bit of a pain right now, sorry! I am currently planning on switching from Maven to Gradle. For the meantime, you can load the repo as an IntelliJ project and you should be able to build it from there.

## **Known Bugs:**

* None right now.

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

- (proxy|bungeecord|bungee) server [(named|called)] %string%

- (proxy|bungeecord|bungee) player [(named|called)] %string%

- proxyserver's (player limit|max player count)

- this server

- proxyserver's name

## **Conditions:**

- if player %-proxyplayer% (1¦is|2¦is(n't| not)) online on the (proxy|bungeecord|bungee)

- if %-proxyserver% (1¦is|2¦is(n't| not)) online

