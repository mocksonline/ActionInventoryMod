package megaminds.actioninventory.gui;

import java.util.Arrays;

import eu.pb4.sgui.api.GuiHelpers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import megaminds.actioninventory.actions.BasicAction;
import megaminds.actioninventory.actions.EmptyAction;
import megaminds.actioninventory.gui.elements.SlotElement;
import megaminds.actioninventory.serialization.wrappers.Validated;
import megaminds.actioninventory.util.ValidationException;
import megaminds.actioninventory.util.annotations.Exclude;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Adapted from {@link eu.pb4.sgui.api.gui.SimpleGuiBuilder}.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NamedGuiBuilder implements Validated {
	private ScreenHandlerType<?> type;
	private Identifier name;
	@Setter
	private Text title;
	private boolean includePlayer;
	private boolean lockPlayerInventory;
	private SlotElement[] elements;
	private BasicAction openAction;
	private BasicAction closeAction;
	private BasicAction anyClickAction;
	
	@Exclude private int size;
		
	@Override
	public void validate() {
		Validated.validate(type!=null, "NamedGuiBuilder requires type to be non-null.");
		Validated.validate(name!=null, "NamedGuiBuilder requires name to be non-null.");
		if (title==null) title = LiteralText.EMPTY;
		if (openAction==null) openAction = EmptyAction.INSTANCE;
		if (closeAction==null) closeAction = EmptyAction.INSTANCE;
		if (anyClickAction==null) anyClickAction = EmptyAction.INSTANCE;
		
		size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer ? 36 : 0);
		
		int len = elements.length;
		Validated.validate(len<=size, ()->"Too many elements. Screen handler type "+Registry.SCREEN_HANDLER.getId(type)+" requires there to be a maximum of "+size+" SlotElements");
		boolean[] test = new boolean[size];
		for (SlotElement e : elements) {
			if (e!=null) {
				int i = e.getIndex();
				Validated.validate(i<size, ()->"Screen handler type "+Registry.SCREEN_HANDLER.getId(type)+" requires SlotElements to have an index below "+size);
				if (i>=0) {
					Validated.validate(!test[i], "A slot has declared an already used index: "+i);
					test[i] = true;
				}
			}
		}
	}

	public NamedGuiBuilder(ScreenHandlerType<?> type, Identifier name, boolean includePlayerInventorySlots) {
		this.type = type;
		this.name = name;
		this.includePlayer = includePlayerInventorySlots;
		this.lockPlayerInventory = includePlayerInventorySlots;
		
		this.title = LiteralText.EMPTY;
		this.closeAction = EmptyAction.INSTANCE;
		this.openAction = EmptyAction.INSTANCE;
		this.anyClickAction = EmptyAction.INSTANCE;

		this.size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer ? 36 : 0);
		this.elements = new SlotElement[this.size];
	}

	/**
	 * {@link #validate()} is called when using this constructor
	 */
	public NamedGuiBuilder(ScreenHandlerType<?> type, Identifier name, Text title, boolean includePlayerInventorySlots, SlotElement[] elements, BasicAction openAction, BasicAction closeAction, BasicAction anyClickAction) throws ValidationException {
		this.type = type;
		this.name = name;
		this.title = title;
		this.includePlayer = includePlayerInventorySlots;
		this.lockPlayerInventory = includePlayerInventorySlots;
		this.elements = elements;
		this.closeAction = closeAction;
		this.openAction = openAction;
		this.anyClickAction = anyClickAction;
		
		this.size = GuiHelpers.getHeight(type)*GuiHelpers.getWidth(type) + (includePlayer ? 36 : 0);
		
		validate();
	}

	public NamedGui build(ServerPlayerEntity player) {
		NamedGui gui = new NamedGui(type, player, includePlayer, name);
		gui.setTitle(title);
		gui.setLockPlayerInventory(true);

		for (SlotElement element : elements) {
			if (element != null) {
				element.apply(gui, player);
			}
		}

		return gui;
	}
	
	public void setLockPlayerInventory(boolean lockPlayerInventory) {
		if (!includePlayer) this.lockPlayerInventory = lockPlayerInventory;
	}

	public NamedGuiBuilder copy() {
		NamedGuiBuilder builder = new NamedGuiBuilder();
		builder.type = type;
		builder.name = name;
		builder.title = title.shallowCopy();
		builder.includePlayer = includePlayer;
		builder.lockPlayerInventory = lockPlayerInventory;
		builder.elements = Arrays.stream(elements).map(SlotElement::copy).toArray(SlotElement[]::new);
		
		builder.size = size;
		return builder;
	}
}