package markehme.factionsplus;

import java.io.*;
import java.util.Scanner;

import markehme.factionsplus.Cmds.CmdSetJail;
import markehme.factionsplus.FactionsBridge.*;
import markehme.factionsplus.config.*;
import markehme.factionsplus.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.zcore.persist.*;

public class FactionsPlusJail {
	public static Server server;
	/**
	 * caches mappings between faction id (as String) and its jail Location 
	 */
	private static CacheMap<String, Location>	cachedJailLocations=new CacheMap<String, Location>(30);
	
	public static boolean removeFromJail(String unJailingPlayer, String id) {
		File jailingFile = new File(Config.folderJails, "jaildata." + id + "." + unJailingPlayer);
		if(jailingFile.exists()){
			jailingFile.delete();
//			cachedJailLocations.remove(id);could or could not have existed, hmm maybe not remove this due to possibility that
			//jailLocation can be used again, yep makes sense
			return true;
		} else {
			return false;
		}
	}
	
	
	
	
	public static Location getJailLocation(Player player) {
		FPlayer fplayer = FPlayers.i.get( player );//considering Factions' implementation of this, this is never null
		assert (null != fplayer)&&(FPlayers.i.isCreative());//if is creative, even if player didn't exist it will be instance-created
		//thing is, it's always creative, on both 1.6 and 1.7 (for players, not for factions)
		String fid = fplayer.getFactionId().trim();//just in case
		
		Location jailLocation=cachedJailLocations.get(fid);
		if (null != jailLocation) {
			System.out.println("found in cache: "+fid+"->"+jailLocation);
			return jailLocation;
		}
		System.out.println("not in cache: "+fid+"->"+jailLocation);
		
		Faction CWFaction = Factions.i.get(fid);
		assert null != CWFaction:"player wasn't in a faction ? like not even wilderness? this should basically not be null";
		assert fid.equals(CWFaction.getId());
		
		World world;
		
		File currentJailFile = new File(Config.folderJails, "loc." + CWFaction.getId());
				
		if(currentJailFile.exists()) {
			Scanner scanner=null;
			try {
				scanner = new Scanner(currentJailFile);
				String JailData =scanner.useDelimiter("\\A").next();
					
				String[] jail_data =  JailData.split(":");
					
			    double x = Double.parseDouble(jail_data[0]);
			    double y = Double.parseDouble(jail_data[1]); // Y-Axis
			    double z = Double.parseDouble(jail_data[2]);
			    
			    float Y = Float.parseFloat(jail_data[3]); // Yaw
			    float p = Float.parseFloat(jail_data[4]);
			        	
			    world = server.getWorld(jail_data[5]);
			    
			    jailLocation=new Location(world, x, y, z, Y, p);
			    Location existed = cachedJailLocations.put( fid, jailLocation );
			    System.out.println("added to cache: "+fid+"->"+jailLocation);
			    assert null == existed:"bad code logic, should not have existed";
			    return jailLocation;
			    
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if (null != scanner) {
					scanner.close();
				}
			}
		}
		return null;
	}
	
	@Deprecated
	public static void OrganiseJail(Player player) {
		// creates jail file for a certain player TODO: Implant timed jails 
		// 0 	=	Not jailed, so remove the file
		// -1	=	Permentaly Jailed
		// 1	=	Any number larger than 1 stands for minutes 
		FPlayer fplayer = FPlayers.i.get(player.getName());
		
		File jailingFile = new File(Config.folderJails,"jaildata." + fplayer.getFactionId() + "." + player.getName());
		
		if(!jailingFile.exists()) {
			try {
				jailingFile.createNewFile();
				
				FileWriter filewrite = new FileWriter(jailingFile, true);
				filewrite.write("0");
				
				filewrite.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Deprecated
	public static boolean doJailPlayer(Player player, String name, int time) {
		if(!FactionsPlus.permission.has(player, "factionsplus.jail")) {
			player.sendMessage(ChatColor.RED + "No permission!");
			return false;
		}
		
		String args[] = null;
		
		Player jplayer = server.getPlayer(name);
		
		FPlayer fjplayer = FPlayers.i.get(jplayer.getName());
		String jcurrentID = fjplayer.getFaction().getId();
		
		FPlayer fplayer = FPlayers.i.get(player.getName());
		String PcurrentID = fplayer.getFaction().getId();
		
		if(jcurrentID != PcurrentID) {
			player.sendMessage("You can only jail players in your Faction!");
			return false;
		}
		
		OrganiseJail(jplayer);
		
		name = jplayer == null ? name.toLowerCase() : jplayer.getName().toLowerCase();
		
		File jailingFile = new File(Config.folderJails, "jaildata." + PcurrentID + "." + player.getName());
		
		if(!jailingFile.exists()) {
			try {
				jailingFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (name != null) {
			jplayer.sendMessage(FactionsPlusTemplates.Go("jailed_message", args));
			return sendToJail(name, player, time);
		}
		
		return false;

	}
	
	public static boolean sendToJail(String jailingplayer, CommandSender sender, int argTime) {
		Player player = (Player)sender;
		
		FPlayer fplayer = FPlayers.i.get(sender.getName());
		Faction currentFaction = fplayer.getFaction();
		
		World world;
		Player jplayer = server.getPlayer(jailingplayer);
		
		
		if (!FPlayers.i.exists( jailingplayer )) {
			fplayer.msg("Cannot jail inexisting player '"+jailingplayer+"'");
			return false;
		}
		
		FPlayer fjplayer = FPlayers.i.get(jailingplayer);
//		fplayer.msg(jailingplayer+" "+fjplayer.getFactionId()+" "+fplayer.getFactionId());
		if(!fjplayer.getFactionId().equals(fplayer.getFactionId())) {//they are numbers in String
			fplayer.msg("You can only Jail players that are in your Faction!");
			return false;
		}
		
		File currentJailFile = new File(Config.folderJails, "loc." + currentFaction.getId());
		
		if(currentJailFile.exists()) {
			Scanner scanner=null;
			try {
				scanner=new Scanner(currentJailFile);
				String JailData = scanner.useDelimiter("\\A").next();
					
				String[] jail_data =  JailData.split(":");
					
			    double x = Double.parseDouble(jail_data[0]);
			    double y = Double.parseDouble(jail_data[1]); // y axis
			    double z = Double.parseDouble(jail_data[2]);
			    
			    float Y = Float.parseFloat(jail_data[3]); // yaw
			    float p = Float.parseFloat(jail_data[4]);
			    
			    world = server.getWorld(jail_data[5]);
			    
			    if(jplayer != null){
			    	jplayer.teleport(new Location(world, x, y, z, Y, p));
			    }
			    
			    Faction f = Factions.i.get(fjplayer.getName());
			    File jailingFile = new File(Config.folderJails, "jaildata." + fplayer.getFactionId() + "." + fjplayer.getName());
			    
			    if(!jailingFile.exists()){
			    	FileWriter filewrite = new FileWriter(jailingFile, true);
			    	filewrite.flush();
			    	filewrite.write(argTime);
			    	sender.sendMessage(ChatColor.GREEN + fjplayer.getName() +" has been jailed!");
			    	filewrite.close();
			    } else {
			    	sender.sendMessage(ChatColor.RED + fjplayer.getName() +" is already jailed!");
			    }
			       	
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Can not read the jail data, is a jail set?");
			}finally {
				if (null != scanner) {
					scanner.close();
				}
			}

		} else {
			sender.sendMessage(ChatColor.RED + "There is no jail currently set.");
		}
		
		return false;
	}
	
	public static boolean setJail(CommandSender sender) {
		if(!FactionsPlus.permission.has(sender, "factionsplus.setjail")) {
			sender.sendMessage(ChatColor.RED + "No permission!");
			return false;
		}
		
		FPlayer fplayer = FPlayers.i.get(sender.getName());
		Faction currentFaction = fplayer.getFaction();
		
		boolean authallow = ((Config._jails.leadersCanSetJails._) && (Utilities.isLeader( fplayer ))) 
		|| ((Config._jails.officersCanSetJails._) && (Utilities.isOfficer( fplayer )))
		|| (Config._jails.membersCanSetJails._);
		
		
		if(!authallow) {
			sender.sendMessage(ChatColor.RED + "Sorry, your faction rank is not allowed to do that!");
			//ie. leader maybe can't but officer can, depending on the options set in config (while clearly that's crazy to set,
			//it's possible and up to server admin)
			return false;
		}
		
		if(!fplayer.isInOwnTerritory()) {
			sender.sendMessage(ChatColor.RED + "You must be in your own territory to set the jail location!");
			return false;
		}
		
		if(FactionsPlus.economy != null) {
			if(Config._economy.costToSetJail._ > 0.0d) {//TODO: fill those empty strings
				if(!CmdSetJail.doFinanceCrap(Config._economy.costToSetJail._, "", "", FPlayers.i.get(Bukkit.getPlayer(sender.getName())))) {
					return false;
				}
			}
		}
		
		String cfid = currentFaction.getId();
		File currentJailFile = new File(Config.folderJails,"loc." + cfid);
		
		Player player = (Player)sender;
		
		Location loc = player.getLocation();
		
		String jailData = loc.getX() + ":" + 
        loc.getY() + ":" + 
        loc.getZ() + ":" + 
        loc.getYaw() + ":" + 
        loc.getPitch() + ":" + loc.getWorld().getName();
		
		DataOutputStream jailWrite=null;
		try {
			jailWrite = new DataOutputStream(new FileOutputStream(currentJailFile, false));
			jailWrite.write(jailData.getBytes());
			jailWrite.close();
			
			cachedJailLocations.put( cfid, loc );
			sender.sendMessage("Jail set!");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			
			sender.sendMessage("Failed to set jail (Internal error -2)");
			return false;
		}finally{
			if (null != jailWrite) {
				try {
					jailWrite.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
		
	}

	@Deprecated
	public static void unjailPlayer(String name, int id) {
		new File(Config.folderJails, "jaildata." + id + "." + name).delete();
	}

	@Deprecated
	public static double getTempJailTime(Player p) {
		// TODO: getTempJailTime Function
		return 0;
	}
}
