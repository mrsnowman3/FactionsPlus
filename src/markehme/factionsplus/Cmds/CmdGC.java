package markehme.factionsplus.Cmds;

import markehme.factionsplus.FactionsBridge.*;

public class CmdGC extends BaseCmdChatMode  {
	public CmdGC() {
		super(FactionsAny.ChatMode.PUBLIC,"factionsplus.factionglobalchatcommand", "gc","cg","cp" );
	}
	
}