package markehme.factionsplus.config;


public class BanningSection {
	
	@ConfigOption(oldAliases={
		"leaderCanNotBeBanned"
	})
	public boolean leaderCanNotBeBanned=true;
	
	
	
	@ConfigOption(oldAliases={
		"banning.enableBans"
		,"enableBans"
	})
	public boolean enabled=true;
	
	
	@ConfigOption(oldAliases={
		"leadersCanFactionBan"
	})
	public boolean leadersCanFactionBan=true;
	
	
	@ConfigOption(oldAliases={
		"leadersCanFactionBan"
	})
	public boolean officersCanFactionBan=true;
	
	
	@ConfigOption(oldAliases={
		"leadersCanFactionUnban"
	})
	public boolean leadersCanFactionUnban=true;
	
	
	@ConfigOption(oldAliases={
		"officersCanFactionUnban"
	})
	public boolean officersCanFactionUnban=true;
}
