package nova.core.gui.component;

import nova.core.gui.GuiComponent;
import nova.core.gui.nativeimpl.NativeGuiComponent;
import nova.core.gui.render.Graphics;
import nova.core.gui.render.text.FormattedText;
import nova.core.util.transform.vector.Vector2d;
import nova.core.util.transform.vector.Vector2i;

import java.util.Optional;

/**
 * A label is a component that defines a single piece of text. Labels have their
 * minimum size set to the boundaries of the provided {@link FormattedText}.
 * 
 * @author Vic Nightfall
 */
public class Label extends GuiComponent<Label, NativeGuiComponent> {

	private FormattedText text;

	public Label(String uniqueID, FormattedText text) {
		super(uniqueID, NativeGuiComponent.class);
		setText(text);
	}

	public Label(String uniqueID, String text) {
		this(uniqueID, FormattedText.parse(text));
	}

	public Label(FormattedText text) {
		this("", text);
	}

	public Label(String text) {
		this("", text);
	}

	public Label setText(String text) {
		this.text = FormattedText.parse(text);
		return this;
	}

	public FormattedText getText() {
		return text;
	}

	public Label setText(FormattedText text) {
		this.text = text;
		return this;
	}

	@Override
	public Optional<Vector2i> getMinimumSize() {
		Optional<Vector2i> inherited = super.getMinimumSize();
		if (inherited.isPresent())
			return inherited;
		if (getParentGui().isPresent()) {
			Vector2d dimensions = getParentGui().get().getTextMetrics().getBounds(getText());
			return Optional.of(new Vector2i(dimensions.xi(), dimensions.yi()));
		}
		return inherited;
	}

	@Override
	public void render(int mouseX, int mouseY, Graphics graphics) {
		super.render(mouseX, mouseY, graphics);
		graphics.drawCenteredString(0, 0, text, getOutline().getDimension());
	}
}
