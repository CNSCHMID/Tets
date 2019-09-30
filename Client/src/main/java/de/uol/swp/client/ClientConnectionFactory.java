package de.uol.swp.client;

public interface ClientConnectionFactory {
    ClientConnection create(String host, int port);
}
