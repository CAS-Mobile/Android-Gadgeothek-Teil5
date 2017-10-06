package ch.hsr.mge.gadgeothek.modules;

import javax.inject.Singleton;

import ch.hsr.mge.gadgeothek.ui.AbstractAuthenticationActivity;
import ch.hsr.mge.gadgeothek.ui.GadgeothekActivity;
import ch.hsr.mge.gadgeothek.ui.RegisterActivity;
import ch.hsr.mge.gadgeothek.ui.SettingsActivity;
import ch.hsr.mge.gadgeothek.ui.SplashActivity;
import ch.hsr.mge.gadgeothek.ui.loans.LoansFragment;
import ch.hsr.mge.gadgeothek.ui.reservations.NewReservationActivityFragment;
import ch.hsr.mge.gadgeothek.ui.reservations.ReservationsFragment;
import dagger.Component;

@Singleton
@Component(modules = { LibraryServiceModule.class })
public interface GadgeothekComponent {

    void inject(GadgeothekActivity activity);
    void inject(AbstractAuthenticationActivity activity);
    void inject(RegisterActivity activity);
    void inject(SettingsActivity activity);
    void inject(SplashActivity activity);

    void inject(LoansFragment fragment);
    void inject(ReservationsFragment fragment);
    void inject(NewReservationActivityFragment fragment);

}