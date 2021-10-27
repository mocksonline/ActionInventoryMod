package megaminds.actioninventory.inventory.helpers;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.util.UUIDTypeAdapter;

import megaminds.actioninventory.ExcludeAnnotationExclusionStrategy;
import megaminds.actioninventory.inventory.ActionInventory;
import megaminds.actioninventory.inventory.actions.Action;
import megaminds.actioninventory.inventory.actions.ActionSerializer;
import megaminds.actioninventory.inventory.openers.Opener;
import megaminds.actioninventory.inventory.openers.OpenerSerializer;
import megaminds.actioninventory.inventory.requirements.Requirement;
import megaminds.actioninventory.inventory.requirements.RequirementSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import net.minecraft.util.math.Vec3i;

public class ActionJsonHelper {
	public static final Gson GSON;

	public static String toJson(Object o) {
		return GSON.toJson(o);
	}

	public static ActionInventory fromJson(String json) {
		return GSON.fromJson(json, ActionInventory.class);
	}

	public static class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
		@Override
		public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return ItemStack.fromNbt(context.deserialize(json, NbtCompound.class));
		}

		@Override
		public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
			return context.serialize(src.writeNbt(new NbtCompound()), NbtCompound.class);
		}
	}

	public static class NbtSerializer implements JsonSerializer<NbtElement>, JsonDeserializer<NbtElement> {
		@Override
		public NbtElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				return StringNbtReader.parse(json.getAsString());
			} catch (CommandSyntaxException e) {
				throw new JsonParseException(e);
			}
		}

		@Override
		public JsonElement serialize(NbtElement src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(new StringNbtWriter().apply(src));
		}
	}

	public static class Vec3iSerializer implements JsonSerializer<Vec3i>, JsonDeserializer<Vec3i> {
		@Override
		public Vec3i deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonArray arr = json.getAsJsonArray();
			return new Vec3i(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
		}

		@Override
		public JsonElement serialize(Vec3i src, Type typeOfSrc, JsonSerializationContext context) {
			return context.serialize(new int[] {src.getX(), src.getY(), src.getZ()});
		}
	}

	static {
		GSON = ExcludeAnnotationExclusionStrategy.newBuilder()
				.setPrettyPrinting()
				.disableHtmlEscaping()
				.registerTypeAdapter(Action.class, new ActionSerializer())
				.registerTypeAdapter(Requirement.class, new RequirementSerializer())
				.registerTypeAdapter(Opener.class, new OpenerSerializer())
				.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
				.registerTypeAdapter(UUID.class, new UUIDTypeAdapter())
				.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
				.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
				.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory())
				.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
				.registerTypeHierarchyAdapter(NbtElement.class, new NbtSerializer())
				.registerTypeHierarchyAdapter(Vec3i.class, new Vec3iSerializer())
				.create();
	}
}