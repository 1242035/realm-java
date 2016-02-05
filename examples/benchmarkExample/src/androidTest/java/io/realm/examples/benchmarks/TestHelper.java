/*
 * Copyright 2014 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.examples.benchmarks;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Looper;
import android.util.Log;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmFieldType;
import io.realm.examples.benchmarks.model.AllTypes;
import io.realm.internal.Table;
import io.realm.internal.log.Logger;

import static junit.framework.Assert.fail;

public class TestHelper {

    public static RealmFieldType getColumnType(Object o){
        if (o instanceof Boolean)
            return RealmFieldType.BOOLEAN;
        if (o instanceof String)
            return RealmFieldType.STRING;
        if (o instanceof Long)
            return RealmFieldType.INTEGER;
        if (o instanceof Float)
            return RealmFieldType.FLOAT;
        if (o instanceof Double)
            return RealmFieldType.DOUBLE;
        if (o instanceof Date)
            return RealmFieldType.DATE;
        if (o instanceof byte[])
            return RealmFieldType.BINARY;
        return RealmFieldType.UNSUPPORTED_MIXED;
    }

    /**
     * Creates an empty table with 1 column of all our supported column types, currently 9 columns
     * @return
     */
    public static Table getTableWithAllColumnTypes(){
        Table t = new Table();

        t.addColumn(RealmFieldType.BINARY, "binary");
        t.addColumn(RealmFieldType.BOOLEAN, "boolean");
        t.addColumn(RealmFieldType.DATE, "date");
        t.addColumn(RealmFieldType.DOUBLE, "double");
        t.addColumn(RealmFieldType.FLOAT, "float");
        t.addColumn(RealmFieldType.INTEGER, "long");
        t.addColumn(RealmFieldType.UNSUPPORTED_MIXED, "mixed");
        t.addColumn(RealmFieldType.STRING, "string");
        t.addColumn(RealmFieldType.UNSUPPORTED_TABLE, "table");

        return t;
    }

    public static String streamToString(InputStream in) throws IOException {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }

        return sb.toString();
    }

    public static InputStream stringToStream(String str) {
        return new ByteArrayInputStream(str.getBytes(Charset.forName("UTF-8")));
    }

    // Copies a Realm file from assets to app files dir
    public static void copyRealmFromAssets(Context context, String realmPath, String newName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream is = assetManager.open(realmPath);
        File file = new File(context.getFilesDir(), newName);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = is.read(buf)) > -1) {
            outputStream.write(buf, 0, bytesRead);
        }
        outputStream.close();
        is.close();
    }

    // Deletes the old database and copies a new one into its place
    public static void prepareDatabaseFromAssets(Context context, String realmPath, String newName) throws IOException {
        Realm.deleteRealm(createConfiguration(context, newName));
        TestHelper.copyRealmFromAssets(context, realmPath, newName);
    }

    // Returns a random key used by encrypted Realms.
    public static byte[] getRandomKey() {
        byte[] key = new byte[64];
        new Random().nextBytes(key);
        return key;
    }

    // Returns a random key from the given seed. Used by encrypted Realms.
    public static byte[] getRandomKey(long seed) {
        byte[] key = new byte[64];
        new Random(seed).nextBytes(key);
        return key;
    }

    /**
     * Returns a Logger that will fail if it is asked to log a message above a certain level.
     *
     * @param failureLevel {@link Log} level from which the unit test will fail.
     * @return Logger implementation
     */
    public static Logger getFailureLogger(final int failureLevel) {
        return new Logger() {

            private void failIfEqualOrAbove(int logLevel, int failureLevel) {
                if (logLevel >= failureLevel) {
                    fail("Message logged that was above valid level: " + logLevel + " >= " + failureLevel);
                }
            }

            @Override
            public void v(String message) {
                failIfEqualOrAbove(Log.VERBOSE, failureLevel);
            }

            @Override
            public void v(String message, Throwable t) {
                failIfEqualOrAbove(Log.VERBOSE, failureLevel);
            }

            @Override
            public void d(String message) {
                failIfEqualOrAbove(Log.DEBUG, failureLevel);
            }

            @Override
            public void d(String message, Throwable t) {
                failIfEqualOrAbove(Log.DEBUG, failureLevel);
            }

            @Override
            public void i(String message) {
                failIfEqualOrAbove(Log.INFO, failureLevel);
            }

            @Override
            public void i(String message, Throwable t) {
                failIfEqualOrAbove(Log.INFO, failureLevel);
            }

            @Override
            public void w(String message) {
                failIfEqualOrAbove(Log.WARN, failureLevel);
            }

            @Override
            public void w(String message, Throwable t) {
                failIfEqualOrAbove(Log.WARN, failureLevel);
            }

            @Override
            public void e(String message) {
                failIfEqualOrAbove(Log.ERROR, failureLevel);
            }

            @Override
            public void e(String message, Throwable t) {
                failIfEqualOrAbove(Log.ERROR, failureLevel);
            }
        };
    }

    public static String getRandomString(int length) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) r.nextInt(128)); // Restrict to standard ASCII chars.
        }
        return sb.toString();
    }

    /**
     * Returns a naive logger that can be used to test the values that are sent to the logger.
     */
    public static class TestLogger implements Logger {

        public String message;
        public Throwable throwable;

        @Override
        public void v(String message) {
            this.message = message;
        }

        @Override
        public void v(String message, Throwable t) {
            this.message = message;
            this.throwable = t;
        }

        @Override
        public void d(String message) {
            this.message = message;
        }

        @Override
        public void d(String message, Throwable t) {
            this.message = message;
            this.throwable = t;
        }

        @Override
        public void i(String message) {
            this.message = message;
        }

        @Override
        public void i(String message, Throwable t) {
            this.message = message;
            this.throwable = t;
        }

        @Override
        public void w(String message) {
            this.message = message;
        }

        @Override
        public void w(String message, Throwable t) {
            this.message = message;
            this.throwable = t;
        }

        @Override
        public void e(String message) {
            this.message = message;
        }

        @Override
        public void e(String message, Throwable t) {
            this.message = message;
            this.throwable = t;
        }
    }

    public static class StubInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return 0; // Stub implementation
        }
    }

    // Alloc as much garbage as we can. Pass maxSize = 0 to use it.
    public static byte[] allocGarbage(int garbageSize) {
        if (garbageSize == 0) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            garbageSize = (int)(maxMemory - totalMemory)/10*9;
        }
        byte garbage[] = new byte[0];
        try {
            if (garbageSize > 0) {
                garbage = new byte[garbageSize];
                garbage[0] = 1;
                garbage[garbage.length - 1] = 1;
            }
        } catch (OutOfMemoryError oom) {
            return allocGarbage(garbageSize/10*9);
        }

        return garbage;
    }

    // Creates SHA512 hash of a String. Can be used as password for encrypted Realms.
    public static byte[] SHA512(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(str.getBytes("UTF-8"), 0, str.length());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @deprecated Use {@link TestRealmConfigurationFactory#createConfiguration()} instead.
     */
    @Deprecated
    public static RealmConfiguration createConfiguration(Context context) {
        return createConfiguration(context, Realm.DEFAULT_REALM_NAME);
    }

    /**
     * @deprecated Use {@link TestRealmConfigurationFactory#createConfiguration(String)} instead.
     */
    @Deprecated
    public static RealmConfiguration createConfiguration(Context context, String name) {
        return createConfiguration(context.getFilesDir(), name);
    }

    /**
     * @deprecated Use {@link TestRealmConfigurationFactory#createConfiguration(String)} instead.
     */
    @Deprecated
    public static RealmConfiguration createConfiguration(File folder, String name) {
        return createConfiguration(folder, name, null);
    }

    /**
     * @deprecated Use {@link TestRealmConfigurationFactory#createConfiguration(String, byte[])} instead.
     */
    @Deprecated
    public static RealmConfiguration createConfiguration(Context context, String name, byte[] key) {
        return createConfiguration(context.getFilesDir(), name, key);
    }

    /**
     * @deprecated Use {@link TestRealmConfigurationFactory#createConfiguration(String, byte[])} instead.
     */
    @Deprecated
    public static RealmConfiguration createConfiguration(File dir, String name, byte[] key) {
        RealmConfiguration.Builder config = new RealmConfiguration.Builder(dir).name(name);
        if (key != null) {
            config.encryptionKey(key);
        }

        return config.build();
    }

    public static void populateForMultiSort(Realm typedRealm) {
        DynamicRealm dynamicRealm = DynamicRealm.getInstance(typedRealm.getConfiguration());
        populateForMultiSort(dynamicRealm);
        dynamicRealm.close();
        typedRealm.refresh();
    }

    public static void populateForMultiSort(DynamicRealm realm) {
        realm.beginTransaction();
        realm.clear(AllTypes.CLASS_NAME);
        DynamicRealmObject object1 = realm.createObject(AllTypes.CLASS_NAME);
        object1.setLong(AllTypes.FIELD_LONG, 5);
        object1.setString(AllTypes.FIELD_STRING, "Adam");

        DynamicRealmObject object2 = realm.createObject(AllTypes.CLASS_NAME);
        object2.setLong(AllTypes.FIELD_LONG, 4);
        object2.setString(AllTypes.FIELD_STRING, "Brian");

        DynamicRealmObject object3 = realm.createObject(AllTypes.CLASS_NAME);
        object3.setLong(AllTypes.FIELD_LONG, 4);
        object3.setString(AllTypes.FIELD_STRING, "Adam");
        realm.commitTransaction();
    }

    public static void awaitOrFail(CountDownLatch latch) {
        awaitOrFail(latch, 7);
    }

    public static void awaitOrFail(CountDownLatch latch, int numberOfSeconds) {
        try {
            if (!latch.await(numberOfSeconds, TimeUnit.SECONDS)) {
                fail("Test took longer than " + numberOfSeconds + " seconds");
            }
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    // clean resource, shutdown the executor service & throw any background exception
    public static void exitOrThrow(final ExecutorService executorService,
                                   final CountDownLatch signalTestFinished,
                                   final CountDownLatch signalClosedRealm,
                                   final Looper[] looper,
                                   final Throwable[] throwable) throws Throwable {

        // wait for the signal indicating the test's use case is done
        try {
            // Even if this fails we want to try as hard as possible to cleanup. If we fail to close all resources
            // properly, the `after()` method will most likely throw as well because it tries do delete any Realms
            // used. Any exception in the `after()` code will mask the original error.
            TestHelper.awaitOrFail(signalTestFinished);
        } finally {
            // close the executor
            executorService.shutdownNow();
            if (looper[0] != null) {
                // failing to quit the looper will not execute the finally block responsible
                // of closing the Realm
                looper[0].quit();
            }

            // wait for the finally block to execute & close the Realm
            TestHelper.awaitOrFail(signalClosedRealm);

            if (throwable[0] != null) {
                // throw any assertion errors happened in the background thread
                throw throwable[0];
            }
        }
    }

    public static InputStream loadJsonFromAssets(Context context, String file) throws IOException {
        AssetManager assetManager = context.getAssets();
        return assetManager.open(file);
    }
}
