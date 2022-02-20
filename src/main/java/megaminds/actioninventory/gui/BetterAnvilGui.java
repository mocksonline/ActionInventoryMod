package megaminds.actioninventory.gui;

import java.util.function.Consumer;
import java.util.function.Function;

import eu.pb4.sgui.api.gui.AnvilInputGui;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import megaminds.actioninventory.gui.callback.BetterClickCallback;
import megaminds.actioninventory.util.ElementHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class BetterAnvilGui extends AnvilInputGui implements BetterGuiI {
	private Identifier id;
	/**
	 * Note, this is set on open.
	 */
	@Setter(AccessLevel.NONE) private BetterGuiI previousGui;
	private boolean chained;
	/**
	 * Happens every character.
	 */
	private Function<String, String> filter;
	/**
	 * Happens before finishing.
	 */
	private Function<String, String> postFilter;
	private Consumer<String> onFinish;

	public BetterAnvilGui(ServerPlayerEntity player, boolean includePlayerInventorySlots) {
		super(player, includePlayerInventorySlots);
		setSlot(1, ElementHelper.getCancel(null));
		setSlot(2, ElementHelper.getConfirm(CONFIRM));
	}
	
	private static final BetterClickCallback CONFIRM = (i,t,a,g)->{
		if (g instanceof BetterAnvilGui v) {
			if (v.doFilter(v.getInput(), v.postFilter)) {
				if (v.onFinish!=null) v.onFinish.accept(v.getInput());
			} else {
				return true;
			}
		}
		return false;
	};

	@Override
	public void onInput(String input) {
		doFilter(input, filter);
	}
	
	/**
	 * True if input passed the filter.
	 */
	private boolean doFilter(String input, Function<String, String> filter) {
		String filtered;
		if (filter!=null && !input.equals(filtered = filter.apply(input))) {
			setDefaultInputValue(filtered);
			return false;
		}
		return true;
	}

	@Override
	public boolean reOpen() {
		if (isOpen()) sendGui();
		return false;
	}

	@Override
	public boolean open(BetterGuiI previous) {
		previousGui = previous;
		return open();
	}

	@Override
	public void clearPrevious() {
		previousGui = null;
	}
}