package net.trueog.diamondbankogpapi;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.trueog.diamondbankog.DiamondBankAPIJava;

public final class DiamondBankOGPAPI extends JavaPlugin {

	private static Plugin instance;
	private static DiamondBankAPIJava diamondBankAPI;

	@Override
	public void onEnable() {

		saveDefaultConfig();

		RegisteredServiceProvider<DiamondBankAPIJava> provider =
				getServer().getServicesManager().getRegistration(DiamondBankAPIJava.class);

		if (provider == null) {

			getLogger().severe("DiamondBank-OG API is null – disabling plugin.");
			Bukkit.getPluginManager().disablePlugin(this);

			return;
		}

		diamondBankAPI = provider.getProvider();

	    if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
	        new PAPIHook(this);
	    } else {
	        getLogger().warning("PlaceholderAPI not found — placeholders disabled.");
	    }

		// Populate the plugin instance.
		instance = (Plugin) this;

	}

	public static DiamondBankAPIJava diamondBankAPI() {

		return diamondBankAPI;
	}

	public static Plugin getInstance() {

		return instance;

	}

}