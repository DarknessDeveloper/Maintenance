package net.dxtrus.maintenance.commands;

import net.dxtrus.maintenance.Maintenance;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMaintenance implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl,
			String[] args) {
		if (!cmd.getName().equalsIgnoreCase("maintenance"))
			return false;

		if (!sender.hasPermission("maintenance.use")) {
			sender.sendMessage(Maintenance.i18n("messages.no-permission", "maintenance.use"));
			return true;
		}

		boolean maintEnabled = Maintenance.getInstance().getConfig()
				.getBoolean("maintenance.enabled");

		if (args.length < 1) {
			sender.sendMessage(Maintenance.i18n("messages.maintenance.status",
					maintEnabled ? "on" : "off"));

			return true;
		}
		
		if (args[0].equalsIgnoreCase("reload")) {
			if (!(sender.hasPermission("maintenance.reload"))) {
				sender.sendMessage(Maintenance.i18n("messages.no-permission", "maintenance.reload"));
				return true;
			}
			
			Maintenance.getInstance().reloadConfig();
			sender.sendMessage(Maintenance.i18n("messages.commands.reload-success"));
			return true;
		}

		if (!sender.hasPermission("maintenance.toggle")) {
			sender.sendMessage(Maintenance.i18n("messages.no-permission", "maintenance.toggle"));
			return true;
		}

		if (equalsIgnoreCase(args[0], "on", "enable", "true")) {
			if (maintEnabled) {
				sender.sendMessage(Maintenance.i18n("messages.maintenance.already-enabled"));
				return true;
			}
			
			Maintenance.setMaintenance(true);
			sender.sendMessage(Maintenance.i18n("messages.maintenance.toggled", "on"));
			
			boolean bypassEnabled = Maintenance.getInstance().getConfig().getBoolean("maintenance.bypass.enabled");
			String bypassPermission = Maintenance.getInstance().getConfig().getString("maintenance.bypass.permission");
			
			for (Player p : Bukkit.getOnlinePlayers()){
				if (bypassEnabled && p.hasPermission(bypassPermission))
					continue;
					
				p.kickPlayer(Maintenance.getKickMessageString());
			}
			
			return true;
		}
		
		if (equalsIgnoreCase(args[0], "off", "disable", "false")) {
			if (!maintEnabled){
				sender.sendMessage(Maintenance.i18n("messages.maintenance.already-disabled"));
				return true;
			}
			
			Maintenance.setMaintenance(false);
			sender.sendMessage(Maintenance.i18n("messages.maintenance.toggled", "off"));
			return true;
		}

		sender.sendMessage(Maintenance.i18n("messages.invalid-args"));
		return true;
	}
	
	public boolean equalsIgnoreCase(String original, String... comparison){
		
		for (String str : comparison){
			if (original.equalsIgnoreCase(str))
				return true;
		}
		
		return false;
	}

}
