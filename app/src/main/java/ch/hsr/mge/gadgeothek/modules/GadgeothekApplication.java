package ch.hsr.mge.gadgeothek.modules;

import android.app.Application;

public class GadgeothekApplication extends Application {

    protected GadgeothekComponent gadgeothekComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        gadgeothekComponent = DaggerGadgeothekComponent.builder().build();
    }

    public GadgeothekComponent getComponent() {
        return gadgeothekComponent;
    }
}