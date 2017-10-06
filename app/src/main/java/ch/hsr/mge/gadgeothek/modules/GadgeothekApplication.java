package ch.hsr.mge.gadgeothek.modules;

import android.app.Application;
import android.content.Context;

public class GadgeothekApplication extends Application {

    protected GadgeothekComponent gadgeothekComponent;

    public static GadgeothekApplication get(Context context) {
        return (GadgeothekApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gadgeothekComponent = DaggerGadgeothekComponent.builder().build();
    }

    public GadgeothekComponent getComponent() {
        return gadgeothekComponent;
    }
}