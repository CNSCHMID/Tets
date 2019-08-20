package de.uol.swp.server.communication;

import java.util.Objects;
import java.util.UUID;

public class Session implements de.uol.swp.common.user.Session {

	private static final long serialVersionUID = -3012502325550415132L;
	private final String sessionId;

	private Session() {
		synchronized (Session.class) {
			this.sessionId = String.valueOf(UUID.randomUUID());
		}
	}

    public static de.uol.swp.common.user.Session create() {
		return new Session();
    }

    @Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Session session = (Session) o;
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
