package de.uol.swp.server.message;

public class ServerExceptionMessage extends AbstractServerInternalMessage {

    private final Exception e;

    public ServerExceptionMessage(Exception e) {
        super();
        this.e = e;
    }

    public Exception getException(){
        return e;
    }
}
