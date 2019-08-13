package de.uol.swp.common.message;

abstract public class AbstractRequestMessage extends AbstractMessage implements RequestMessage {

    @Override
    public boolean authorizationNeeded() {
        return true;
    }
}
