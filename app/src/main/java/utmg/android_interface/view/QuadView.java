package utmg.android_interface.view;

import utmg.android_interface.model.entity.Quad;

/**
 * Created by Tucker Haydon on 10/14/17.
 */

public class QuadView {

    private final Quad quad;
    private final int color;

    public QuadView(
            final Quad quad,
            final int color) {
        this.quad = quad;
        this.color = color;
    }
}
