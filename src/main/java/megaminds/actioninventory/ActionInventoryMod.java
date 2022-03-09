package megaminds.actioninventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import megaminds.actioninventory.commands.Commands;
import megaminds.actioninventory.loaders.BasicOpenerLoader;
import megaminds.actioninventory.loaders.ActionInventoryLoader;
import megaminds.actioninventory.openers.BlockOpener;
import megaminds.actioninventory.openers.EntityOpener;
import megaminds.actioninventory.openers.ItemOpener;

public class ActionInventoryMod implements ModInitializer {
	public static final Random RANDOM = new Random();
	public static final String MOD_ID = "actioninventory";
	public static final String MOD_NAME = "Action Inventory Mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ActionInventoryLoader INVENTORY_LOADER = new ActionInventoryLoader();
	public static final BasicOpenerLoader OPENER_LOADER = new BasicOpenerLoader();

	@Override
	public void onInitialize() {
		ItemOpener.registerCallbacks();
		BlockOpener.registerCallbacks();
		EntityOpener.registerCallbacks();
		CommandRegistrationCallback.EVENT.register(Commands::register);

		info("Initialized");
	}

	public static void reload(MinecraftServer server) {
		var manager = server.getResourceManager();
		INVENTORY_LOADER.reload(manager);
		OPENER_LOADER.reload(manager);
	}

	public static void info(String message) {
		LOGGER.info(message);
	}

	public static void warn(String message) {
		LOGGER.warn(message);
	}
}