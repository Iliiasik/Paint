package bookeditor.client.gui.widget.button.colorpicker;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ColorPickerInputHandler {

    private final ColorPickerState state;
    private final java.util.function.Consumer<Integer> onColorChange;

    public ColorPickerInputHandler(ColorPickerState state, java.util.function.Consumer<Integer> onColorChange) {
        this.state = state;
        this.onColorChange = onColorChange;
    }

    public boolean handleMouseClick(double mouseX, double mouseY, int dropX, int dropY) {
        if (!state.expanded) {
            return false;
        }

        int paletteX = dropX + ColorPickerConstants.GAP;
        int paletteY = dropY + ColorPickerConstants.GAP;

        if (mouseX >= paletteX && mouseX < paletteX + ColorPickerConstants.PALETTE_WIDTH &&
                mouseY >= paletteY && mouseY < paletteY + ColorPickerConstants.PALETTE_HEIGHT) {
            state.draggingPalette = true;
            updateFromPalette(mouseX, mouseY, paletteX, paletteY);
            return true;
        }

        int hueX = paletteX + ColorPickerConstants.PALETTE_WIDTH + ColorPickerConstants.GAP;
        if (mouseX >= hueX && mouseX < hueX + ColorPickerConstants.HUE_BAR_WIDTH &&
                mouseY >= paletteY && mouseY < paletteY + ColorPickerConstants.HUE_BAR_HEIGHT) {
            state.draggingHue = true;
            updateFromHueBar(mouseY, paletteY);
            return true;
        }

        int hexY = paletteY + ColorPickerConstants.PALETTE_HEIGHT + ColorPickerConstants.GAP;
        if (mouseX >= paletteX && mouseX < paletteX + ColorPickerConstants.HEX_FIELD_WIDTH &&
                mouseY >= hexY && mouseY < hexY + ColorPickerConstants.HEX_FIELD_HEIGHT) {
            state.hexFieldFocused = true;
            state.cursorPos = state.hexInput.length();
            state.cursorVisible = true;
            state.lastBlinkTime = System.currentTimeMillis();
            return true;
        } else {
            state.hexFieldFocused = false;
        }


        int totalWidth = ColorPickerConstants.calculateTotalWidth();
        int totalHeight = ColorPickerConstants.calculateTotalHeight();

        if (mouseX < dropX || mouseX >= dropX + totalWidth ||
                mouseY < dropY || mouseY >= dropY + totalHeight) {
            state.expanded = false;
            state.hexFieldFocused = false;
            return false;
        }

        return true;
    }

    public boolean handleMouseDrag(double mouseX, double mouseY, int dropX, int dropY) {
        if (!state.expanded) return false;

        int paletteX = dropX + ColorPickerConstants.GAP;
        int paletteY = dropY + ColorPickerConstants.GAP;

        if (state.draggingPalette) {
            updateFromPalette(mouseX, mouseY, paletteX, paletteY);
            return true;
        }

        if (state.draggingHue) {
            updateFromHueBar(mouseY, paletteY);
            return true;
        }

        return false;
    }

    public void handleMouseRelease() {
        state.draggingPalette = false;
        state.draggingHue = false;
    }

    private void updateFromPalette(double mouseX, double mouseY, int paletteX, int paletteY) {
        float sat = (float) Math.max(0, Math.min(ColorPickerConstants.PALETTE_WIDTH - 1, mouseX - paletteX)) / (ColorPickerConstants.PALETTE_WIDTH - 1);
        float bright = 1f - (float) Math.max(0, Math.min(ColorPickerConstants.PALETTE_HEIGHT - 1, mouseY - paletteY)) / (ColorPickerConstants.PALETTE_HEIGHT - 1);

        state.saturation = sat;
        state.brightness = bright;
        state.updateFromHSB();
        onColorChange.accept(state.argb);
    }

    private void updateFromHueBar(double mouseY, int hueY) {
        state.hue = (float) Math.max(0, Math.min(ColorPickerConstants.HUE_BAR_HEIGHT - 1, mouseY - hueY)) / (ColorPickerConstants.HUE_BAR_HEIGHT - 1);
        state.updateFromHSB();
        onColorChange.accept(state.argb);
    }

    public boolean handleKeyPress(int keyCode, int scanCode, int modifiers) {
        if (!state.hexFieldFocused) return false;

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (state.cursorPos > 0 && !state.hexInput.isEmpty()) {
                state.hexInput = state.hexInput.substring(0, state.cursorPos - 1) + state.hexInput.substring(state.cursorPos);
                state.cursorPos--;
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            if (state.cursorPos < state.hexInput.length()) {
                state.hexInput = state.hexInput.substring(0, state.cursorPos) + state.hexInput.substring(state.cursorPos + 1);
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            state.cursorPos = Math.max(0, state.cursorPos - 1);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            state.cursorPos = Math.min(state.hexInput.length(), state.cursorPos + 1);
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_HOME) {
            state.cursorPos = 0;
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_END) {
            state.cursorPos = state.hexInput.length();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            state.hexFieldFocused = false;
            tryApplyHex();
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            state.hexFieldFocused = false;
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_V && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            String clipboard = MinecraftClient.getInstance().keyboard.getClipboard();
            if (clipboard != null) {
                clipboard = clipboard.replace("#", "").toUpperCase();
                clipboard = clipboard.replaceAll("[^0-9A-F]", "");
                int available = 6 - state.hexInput.length();
                if (available > 0 && !clipboard.isEmpty()) {
                    String toInsert = clipboard.substring(0, Math.min(clipboard.length(), available));
                    state.hexInput = state.hexInput.substring(0, state.cursorPos) + toInsert + state.hexInput.substring(state.cursorPos);
                    state.cursorPos += toInsert.length();
                }
            }
            return true;
        }

        return false;
    }

    public boolean handleCharTyped(char chr, int modifiers) {
        if (!state.hexFieldFocused) return false;

        if ((chr >= '0' && chr <= '9') || (chr >= 'a' && chr <= 'f') || (chr >= 'A' && chr <= 'F')) {
            if (state.hexInput.length() < 6) {
                char upper = Character.toUpperCase(chr);
                state.hexInput = state.hexInput.substring(0, state.cursorPos) + upper + state.hexInput.substring(state.cursorPos);
                state.cursorPos++;
            }
            return true;
        }

        return false;
    }

    private void tryApplyHex() {
        if (state.hexInput.length() == 6) {
            try {
                int r = Integer.parseInt(state.hexInput.substring(0, 2), 16);
                int g = Integer.parseInt(state.hexInput.substring(2, 4), 16);
                int b = Integer.parseInt(state.hexInput.substring(4, 6), 16);
                state.updateFromArgb(0xFF000000 | (r << 16) | (g << 8) | b);
                onColorChange.accept(state.argb);
            } catch (NumberFormatException ignored) {
            }
        } else if (state.hexInput.length() == 3) {
            try {
                int r = Integer.parseInt(String.valueOf(state.hexInput.charAt(0)), 16);
                int g = Integer.parseInt(String.valueOf(state.hexInput.charAt(1)), 16);
                int b = Integer.parseInt(String.valueOf(state.hexInput.charAt(2)), 16);
                r = r * 17;
                g = g * 17;
                b = b * 17;
                state.updateFromArgb(0xFF000000 | (r << 16) | (g << 8) | b);
                onColorChange.accept(state.argb);
            } catch (NumberFormatException ignored) {
            }
        }
    }
}

