package megaminds.actioninventory.actions;

import org.jetbrains.annotations.NotNull;

import eu.pb4.sgui.api.ClickType;
import lombok.NoArgsConstructor;
import megaminds.actioninventory.gui.NamedSlotGuiInterface;
import megaminds.actioninventory.util.annotations.PolyName;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

@NoArgsConstructor
@PolyName("CloseGui")
public final class CloseAction extends BasicAction {	
	public CloseAction(Integer requiredIndex, ClickType clicktype, SlotActionType actionType, Boolean requireShift, Identifier requiredRecipe, Identifier requiredGuiName) {
		super(requiredIndex, clicktype, actionType, requireShift, requiredRecipe, requiredGuiName);
	}
	
	@Override
	public void validate() {
		//Unused
	}

	@Override
	public BasicAction copy() {
		return new CloseAction(getRequiredIndex(), getRequiredClickType(), getRequiredSlotActionType(), getRequireShift(), getRequiredRecipe(), getRequiredGuiName());
	}

	@Override
	public void execute(@NotNull NamedSlotGuiInterface gui) {
		gui.close();
	}
}