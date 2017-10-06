package ch.hsr.mge.gadgeothek.ui;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.HashMap;

import ch.hsr.mge.gadgeothek.domain.Gadget;

public class ImageProvider {
    static private final ColorGenerator generator = ColorGenerator.MATERIAL;

    /**
     * In a real application, we should be concerned about memory usage!
     */
    static private final HashMap<String, TextDrawable> drawablesCache = new HashMap<>();

    static public TextDrawable getIconFor(Gadget gadget) {
        return getIconFor(gadget.getName());
    }

    private static TextDrawable createNewTextDrawable(String name) {
        int color = generator.getColor(name);
        return TextDrawable.builder()
                .beginConfig()
                .width(80)  // width in px
                .height(80) // height in px
                .endConfig()
                .buildRound(name.substring(0, 1), color);
    }

    public static TextDrawable getIconFor(String name) {
        if (drawablesCache.containsKey(name)) {
            return drawablesCache.get(name);
        } else {
            TextDrawable drawable = createNewTextDrawable(name);
            drawablesCache.put(name, drawable);
            return drawable;
        }
    }
}
