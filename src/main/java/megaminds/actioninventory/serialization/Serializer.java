package megaminds.actioninventory.serialization;

import java.io.Reader;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.gui.AccessableAnimatedGuiElement;
import megaminds.actioninventory.gui.AccessableGuiElement;
import megaminds.actioninventory.gui.NamedGuiBuilder;
import megaminds.actioninventory.gui.SlotFunction;
import megaminds.actioninventory.openers.BasicOpener;
import net.minecraft.item.ItemStack;

public class Serializer {
	public static final Gson GSON;
	
	public static NamedGuiBuilder builderFromJson(Reader json) {
		return GSON.fromJson(json, NamedGuiBuilder.class);
	}
	
	public static NamedGuiBuilder builderFromJson(String json) {
		return GSON.fromJson(json, NamedGuiBuilder.class);
	}
	
	public static NamedGuiBuilder builderFromJson(JsonObject json) {
		return GSON.fromJson(json, NamedGuiBuilder.class);
	}
	
	public static BasicOpener openerFromJson(Reader json) {
		return GSON.fromJson(json, BasicOpener.class);
	}
	
	public static BasicOpener openerFromJson(JsonObject json) {
		return GSON.fromJson(json, BasicOpener.class);
	}
	
	public static BasicOpener openerFromJson(String json) {
		return GSON.fromJson(json, BasicOpener.class);
	}
	
	static {
		GSON = new GsonBuilder()
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.registerTypeHierarchyAdapter(BasicAction.class, new BasicActionSerializer())
				.registerTypeHierarchyAdapter(BasicOpener.class, new BasicOpenerSerializer())
				.registerTypeHierarchyAdapter(SlotFunction.class, new SlotFunctionSerializer())
				.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
				.registerTypeAdapter(NamedGuiBuilder.class, new NamedGuiBuilderSerializer())
				.registerTypeAdapter(Function.class, new SlotFunctionSerializer())
				.registerTypeAdapter(AccessableGuiElement.class, new AccessableGuiElementSerializer())
				.registerTypeAdapter(AccessableAnimatedGuiElement.class, new AccessableAnimatedGuiElementSerializer())
				.create();
	}
}