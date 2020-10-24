package net.dxtrus.maintenance.events;

import net.dxtrus.maintenance.Maintenance;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

public class PlayerEventListener implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e){
		
		boolean maintenanceEnabled = Maintenance.getInstance().getConfig().getBoolean("maintenance.enabled");
		boolean bypassEnabled = Maintenance.getInstance().getConfig().getBoolean("maintenance.bypass.enabled");
		String bypassPermission = Maintenance.getInstance().getConfig().getString("maintenance.bypass.permission");
		
		if (maintenanceEnabled){
			if (!bypassEnabled)
			{
				denyLogin(e);
				return;
			}
			
			if (!e.getPlayer().hasPermission(bypassPermission))
			{
				denyLogin(e);
				return;
			}
		}
		
		
		
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent e){
		
	}
	
	private final void denyLogin(PlayerLoginEvent e) {
		
		
		e.disallow(Result.KICK_OTHER, Maintenance.getKickMessageString());
	}
	
}
