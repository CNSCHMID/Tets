SWP Basissystem	
===============

Der folgende Code sollte als Basis für das Software Projekt verwendet werden.


WIP
===


Maven
-----
Das Projekt kann mit Maven gebaut werden. 

Dazu auf der Hauptebene zunächst

<pre><code>mvn clean install
</code></pre>

aufrufen.

Um den Client zu bauen:
<pre><code>cd client
mvn clean package
</code></pre>

der Client ist dann unter traget zu finden und kann dann z.B. wie folgt aufgerufen werden:

<pre><code>cd target
java -jar client-1.0-SNAPSHOT-jar-with-dependencies.jar
</code></pre>

Analoges gilt für den Server.