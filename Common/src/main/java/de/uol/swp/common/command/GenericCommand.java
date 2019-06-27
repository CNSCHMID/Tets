package de.uol.swp.common.command;

import de.uol.swp.common.message.AbstractMessage;
import de.uol.swp.common.user.Session;

/**
 * A simple way to send a simple parameterless command (e.g. to retrieve all current logged in users)
 * 
 * @author Marco Grawunder
 *
 */
public class GenericCommand extends AbstractMessage {
	
	private static final long serialVersionUID = -1066190839599491556L;
	private GenericCommands command;

	public GenericCommand(GenericCommands command, Session session){
		super(session);
		this.command = command;
	}
	
	public GenericCommands getCommand() {
		return command;
	}
	
}
