package de.uol.swp.server.communication;

import java.util.UUID;

public class Session implements de.uol.swp.common.user.Session {

	private static final long serialVersionUID = -3012502325550415132L;
	private final String sessionId;

	public Session() {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "SessionId: "+sessionId;
	}
	
}
