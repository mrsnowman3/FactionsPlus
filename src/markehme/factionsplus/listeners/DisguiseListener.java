package markehme.factionsplus.listeners;

import markehme.factionsplus.*;
import markehme.factionsplus.config.*;
import me.desmin88.mobdisguise.api.MobDisguiseAPI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

public class DisguiseListener implements Listener {
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if(event.isCancelled()) {
			return;
		}

		if(FactionsPlus.isMobDisguiseEnabled) {
			FPlayer fplayer = FPlayers.i.get(event.getPlayer());

			if(MobDisguiseAPI.isDisguised(event.getPlayer())) {
				if(Config._extras._disguise.unDisguiseIfInEnemyTerritory._) {
					if(fplayer.isInEnemyTerritory()) {
						MobDisguiseAPI.undisguisePlayer(event.getPlayer());
						event.getPlayer().sendMessage("You have been un-disguised!");
					}
				}

				if(Config._extras._disguise.unDisguiseIfInOwnTerritory._) {
					if(fplayer.isInOwnTerritory()) {
						MobDisguiseAPI.undisguisePlayer(event.getPlayer());
						event.getPlayer().sendMessage("You have been un-disguised!");
					}
				}
			}
		}

		if(FactionsPlus.isDisguiseCraftEnabled) {
			DisguiseCraftAPI dcAPI = DisguiseCraft.getAPI();
			FPlayer fplayer = FPlayers.i.get(event.getPlayer());

			if(dcAPI.isDisguised(event.getPlayer())) {
				if(Config._extras._disguise.unDisguiseIfInEnemyTerritory._) {
					if(fplayer.isInEnemyTerritory()) {
						dcAPI.undisguisePlayer(event.getPlayer());
						event.getPlayer().sendMessage("You have been un-disguised!");
					}
				}

				if(Config._extras._disguise.unDisguiseIfInOwnTerritory._) {
					if(fplayer.isInOwnTerritory()) {
						dcAPI.undisguisePlayer(event.getPlayer());
						event.getPlayer().sendMessage("You have been un-disguised!");
					}
				}
			}
		}


	}


}
