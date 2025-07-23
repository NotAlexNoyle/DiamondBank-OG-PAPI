package net.trueog.diamondbankogpapi;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.trueog.diamondbankog.DiamondBankException;
import net.trueog.utilitiesog.UtilitiesOG;

/**
 * Internal PlaceholderAPI expansion for DiamondBank-OG.
 * Identifier: diamondbankog
 * Placeholders provided:
 *   %diamondbankog_balance%
 */
public class PAPIHook extends PlaceholderExpansion {

	private final DiamondBankOGPAPI plugin;

	public PAPIHook(DiamondBankOGPAPI plugin) {
		
		this.plugin = plugin;
		
		register();
		
	}

	@Override @NotNull
	public String getIdentifier() {
		return "diamondbankog";
	}

	@Override @NotNull
	public String getAuthor() {
		return String.join(", ", plugin.getPluginMeta().getAuthors());
	}

	@Override @NotNull
	public String getVersion() {
		return plugin.getPluginMeta().getVersion();
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public String onRequest(OfflinePlayer player, @NotNull String params) {
		
		String balance;
		// %diamondbankog_balance% Makes the second part functional.
		if (params.equalsIgnoreCase("balance")) {
			
	        // Make sure to never run any other Bukkit functions in runTaskAsynchronously() (for example accessing players'
	        // inventories)
	        // runTaskAsynchronously() is needed in this case since getTotalShards() calls a database which can be
	        // too slow to run on the main thread
	        Bukkit.getScheduler().runTaskAsynchronously(DiamondBankOGPAPI.getInstance(), () -> {
	        	
	        	UUID playerUUID = player.getUniqueId();
	        	int totalShards;
	            try {
	                totalShards = DiamondBankOGPAPI.diamondBankAPI().getTotalShards(playerUUID);
	            } catch (DiamondBankException.EconomyDisabledException e) {
	                UtilitiesOG.trueogMessage(playerUUID, "<red>The economy is disabled.");
	                return;
	            } catch (DiamondBankException.TransactionsLockedException e) {
	                UtilitiesOG.trueogMessage(playerUUID, "<red>Transactions are currently locked for you.");
	                return;
	            } catch (DiamondBankException.DatabaseException e) {
	                UtilitiesOG.trueogMessage(playerUUID, "<red>Something went wrong with the database.");
	                return;
	            }

	            // Pass on the fetched balance;
	            balance = "&b" + String.valueOf(totalShards / 9);

	        });
			
	        // Need to return the diamond bank balance from this scope.
			return balance;
			
		}
		
		// Error state.
		return null;
	}
	
	
}