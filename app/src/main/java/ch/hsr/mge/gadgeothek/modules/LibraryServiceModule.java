package ch.hsr.mge.gadgeothek.modules;

import javax.inject.Singleton;

import ch.hsr.mge.gadgeothek.service.LibraryService;
import dagger.Module;
import dagger.Provides;

@Module
public class LibraryServiceModule {

    @Provides
    @Singleton
    LibraryService provideLibraryService() {
       return new LibraryService();
    }
}