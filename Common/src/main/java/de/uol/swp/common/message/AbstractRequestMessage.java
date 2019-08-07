package de.uol.swp.common.message;

abstract public class AbstractRequestMessage extends AbstractMessage implements IRequestMessage {

    @Override
    public boolean authorizationNeeded() {
        return true;
    }
}
