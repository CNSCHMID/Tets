package de.uol.swp.server.communication;

import java.util.Objects;
import java.util.UUID;

public class UUIDSession implements de.uol.swp.common.user.Session {

	private static final long serialVersionUID = -3012502325550415132L;
	private final String sessionId;

	private UUIDSession() {
		synchronized (UUIDSession.class) {
			this.sessionId = String.valueOf(UUID.randomUUID());
		}
	}

    public static de.uol.swp.common.user.Session create() {
		return new UUIDSession();
    }

    @Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UUIDSession session = (UUIDSession) o;
		return Objects.equals(sessionId, session.sessionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sessionId);
	}

	@Override
	public String toString() {
		return "SessionId: "+sessionId;
	}
	
}
