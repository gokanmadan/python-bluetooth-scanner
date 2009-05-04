The process of getting this to work, was a pain.

hopefully I can remember enough of my tricks.

The one absolute key thing was to recompile smack adding
the following lines to the META-INF/smack.providers

(I might have been able to do this inside my 'jar' as well, but I know
for a fact that adding these lines solved all my problems. I needed some
method of registering these extensions, and this took me forever to figure out.)

-----

<iqProvider>
    <elementName>pubsub</elementName>
    <namespace>http://jabber.org/protocol/pubsub</namespace>
    <className>se.su.it.smack.provider.PubSubProvider</className>
</iqProvider>
<extensionProvider>
	<elementName>event</elementName>
	<namespace>http://jabber.org/protocol/pubsub#event</namespace>
	<className>se.su.it.smack.provider.PubSubEventProvider</className>
</extensionProvider>
<extensionProvider>
	<elementName>x</elementName>
	<namespace>http://jabber.org/protocol/pubsub#event</namespace>
	<className>se.su.it.smack.provider.PubSubXEventProvider</className>
</extensionProvider>

-----

It literally took me months to figure that out:
http://www.igniterealtime.org/builds/smack/docs/latest/documentation/providers.html

I think the next step is to figure out how much I can streamline the build process,
but the fact remains that I now have a viable method of subscribing and publishing pub/sub
messages in JAVA.  Meaning, I could integrate this into all sorts of wonderful things.

[iROS still has a few advantages for a few applications, because it has this server/side pattern matching
but I believe XMPP is the future, and thus all of SSAPP is going to be XMPP based.]

-----

Next steps:

Refactor the java into something that actually works, while I still remember how to make it work?

