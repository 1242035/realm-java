package io.realm;

import android.test.AndroidTestCase;

import java.io.File;
import java.io.IOException;

import io.realm.dynamic.RealmSpec;
import io.realm.entities.AllTypes;
import io.realm.entities.Dog;
import io.realm.exceptions.RealmMigrationNeededException;

public class RealmMigrationTests extends AndroidTestCase {

    public void testRealmClosedAfterMigrationException() throws IOException {
        String REALM_NAME = "default0.realm";
        Realm.deleteRealmFile(getContext(), REALM_NAME);
        TestHelper.copyRealmFromAssets(getContext(), REALM_NAME, REALM_NAME);
        try {
            Realm.getInstance(getContext(), REALM_NAME);
            fail("A migration should be triggered");
        } catch (RealmMigrationNeededException expected) {
            Realm.deleteRealmFile(getContext(), REALM_NAME); // Delete old realm
        }

        // This should recreate the Realm with proper schema
        Realm realm = Realm.getInstance(getContext(), REALM_NAME);
        int result = realm.where(AllTypes.class).equalTo("columnString", "Foo").findAll().size();
        assertEquals(0, result);
    }

    // Create a Realm file with no Realm classes
    private void createEmptyDefaultRealm() {
        Realm.setSchema(AllTypes.class);
        Realm.deleteRealmFile(getContext());
        Realm realm = Realm.getInstance(getContext());
        realm.close();
    }

    private String getDefaultRealm() {
        return new File(getContext().getFilesDir(), "default.realm").getAbsolutePath();
    }

    public void testAddClass() {
        createEmptyDefaultRealm();
        Realm.migrateRealmAtPath(getDefaultRealm(), new RealmMigration() {
            @Override
            public void migrate(RealmSpec realm, long oldVersion, long newVersion) {
            }
        });
        fail();
    }
}
