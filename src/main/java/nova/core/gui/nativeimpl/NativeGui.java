package nova.core.gui.nativeimpl;

import nova.core.gui.Gui;
import nova.core.gui.GuiEvent;
import nova.core.gui.GuiFactory;
import nova.core.gui.Outline;
import nova.core.network.Packet;

public interface NativeGui extends NativeContainer {

	// TODO This is a little bit inconvenient
	public Packet createPacket();

	public void dispatchNetworkEvent(Packet packet);

	/**
	 * Called when the GUI was resized and the child components need to
	 * re-validate their layout. The new size has to be set before calling this.
	 *
	 * @param oldOutline
	 */
	public default void onResized(Outline oldOutline) {
		getElement().onEvent(new GuiEvent.ResizeEvent(oldOutline));
	}

	public default void recieveNetworkEvent(Packet packet) {
		Gui gui = (Gui) getElement();
		gui.triggerEvent(GuiFactory.get(gui.modID).constructEvent(packet, gui));
	}
}
