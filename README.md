# Omegle API Java

This is an Omegle API made specifically for Java.

It supports ALL of the current Omegle functions, except for webcam chatting.

Examples are in src/org/nikki/omegle/examples

# Disclaimer

This api was intended for good, not bad. If you see somebody using it to spam omegle with bots, I cannot do anything about it. Anybody can download this, and anybody can use it.

# Requirements

This library requires the JSON api from https://github.com/douglascrockford/JSON-java, without it, the library won't function.

# Additional notes

If you wish to add the EventListener later on for some reason and still need the waiting event, you need to call Omegle.setFirstEvents(false), it will ignore the events called when first connected to allow you to add a listener first.