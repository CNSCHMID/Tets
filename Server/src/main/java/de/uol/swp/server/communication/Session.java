package de.uol.swp.server.communication;

// FIXME: DO NOT USE COUNTERS FOR SESSIONS!
import de.uol.swp.common.user.ISession;


public class Session implements ISession {

	private static final long serialVersionUID = -3012502325550415132L;
	// JUST FOR DEMO. DO NOT USE THIS KINDS OF SESSIONS, THEY CAN BE GUESSED!
	static long count = 0l;
	private final String sessionId;
	public static final Session invalid = new Session(false);

	public Session() {
		synchronized (Session.class) {
			this.sessionId = String.valueOf(count++);
		}
	}
	
	private Session(boolean state){
		sessionId = null;
	}

    public static ISession create() {
		return new Session();
    }

    @Override
	public String getSessionId() {
		return sessionId;
	}

	@Override
	public boolean isValid() {
		return sessionId != null;
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
