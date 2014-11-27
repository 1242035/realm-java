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
package io.realm;

import android.content.Context;
import android.test.AndroidTestCase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.realm.entities.AllTypes;
import io.realm.entities.Dog;
import io.realm.entities.NonLatinFieldNames;
import io.realm.internal.Table;


public class RealmTest extends AndroidTestCase {

    protected final static int TEST_DATA_SIZE = 159;

    protected Realm testRealm;

    protected List<String> columnData = new ArrayList<String>();

    private final static String FIELD_STRING = "columnString";
    private final static String FIELD_LONG = "columnLong";
    private final static String FIELD_FLOAT = "columnFloat";
    private final static String FIELD_DOUBLE = "columnDouble";
    private final static String FIELD_BOOLEAN = "columnBoolean";
    private final static String FIELD_DATE = "columnDate";
    private final static String FIELD_LONG_KOREAN_CHAR = "델타";
    private final static String FIELD_LONG_GREEK_CHAR = "Δέλτα";
    private final static String FIELD_FLOAT_KOREAN_CHAR = "베타";
    private final static String FIELD_FLOAT_GREEK_CHAR = "βήτα";
    private final static String FIELD_BYTE = "columnBinary";
    private final static String FIELD_DOG = "columnRealmObject";

    protected void setColumnData() {
        columnData.add(0, FIELD_BOOLEAN);
        columnData.add(1, FIELD_DATE);
        columnData.add(2, FIELD_DOUBLE);
        columnData.add(3, FIELD_FLOAT);
        columnData.add(4, FIELD_STRING);
        columnData.add(5, FIELD_LONG);
    }

    @Override
    protected void setUp() throws Exception {
        Realm.deleteRealmFile(getContext());
        testRealm = Realm.getInstance(getContext());

        testRealm.beginTransaction();
        testRealm.allObjects(AllTypes.class).clear();
        testRealm.allObjects(NonLatinFieldNames.class).clear();
        for (int i = 0; i < TEST_DATA_SIZE; ++i) {
            AllTypes allTypes = testRealm.createObject(AllTypes.class);
            allTypes.setColumnBoolean((i % 3) == 0);
            allTypes.setColumnBinary(new byte[]{1, 2, 3});
            allTypes.setColumnDate(new Date());
            allTypes.setColumnDouble(3.1415);
            allTypes.setColumnFloat(1.234567f + i);
            allTypes.setColumnString("test data " + i);
            allTypes.setColumnLong(i);
            NonLatinFieldNames nonLatinFieldNames = testRealm.createObject(NonLatinFieldNames.class);
            nonLatinFieldNames.set델타(i);
            nonLatinFieldNames.setΔέλτα(i);
            nonLatinFieldNames.set베타(1.234567f + i);
            nonLatinFieldNames.setΒήτα(1.234567f + i);
        }
        testRealm.commitTransaction();
    }


    private final static int BACKGROUND_COMMIT_TEST_DATA_SET_SIZE = 5;


    public void testRealmCache() {
        assertEquals(testRealm, Realm.getInstance(getContext()));
    }

    public void testShouldCreateRealm() {
        Realm realm = Realm.getInstance(getContext());
        assertNotNull("Realm.getInstance unexpectedly returns null", realm);
        assertTrue("Realm.getInstance does not contain expected table", realm.contains(AllTypes.class));
    }

    public void testShouldNotFailCreateRealmWithNullContext() {
        Context c = null;  // throws when c.getDirectory() is called; has nothing to do with Realm

        try {
            Realm realm = Realm.getInstance(c);
            fail("Should throw an exception");
        } catch (NullPointerException e) {
        }

    }

    // Table getTable(Class<?> clazz)
    public void testShouldGetTable() {
        Table table = testRealm.getTable(AllTypes.class);
        assertNotNull("getTable is returning a null Table object", table);
        assertEquals("Unexpected query result after getTable", TEST_DATA_SIZE, table.count(table.getColumnIndex(FIELD_DOUBLE), 3.1415));
    }

    // <E> void remove(Class<E> clazz, long objectIndex)
    public void testShouldRemoveRow() {

        testRealm.beginTransaction();
        testRealm.remove(AllTypes.class, 0);
        testRealm.commitTransaction();

        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).findAll();
        assertEquals("Realm.delete has not deleted record correctly", TEST_DATA_SIZE - 1, resultList.size());

    }

    // <E extends RealmObject> E get(Class<E> clazz, long rowIndex)
    public void testShouldGetObject() {

        AllTypes allTypes = testRealm.get(AllTypes.class, 0);
        assertNotNull("get has returned null object", allTypes);
        assertEquals("get has returned wrong object", "test data 0", allTypes.getColumnString());
    }

    // boolean contains(Class<?> clazz)
    public void testShouldContainTable() {
        testRealm.beginTransaction();
        Dog dog = testRealm.createObject(Dog.class);
        testRealm.commitTransaction();
        assertTrue("contains returns false for newly created table", testRealm.contains(Dog.class));
    }

    // boolean contains(Class<?> clazz)
    public void testShouldNotContainTable() {

        assertFalse("contains returns true for non-existing table", testRealm.contains(RealmTest.class));
    }

    // <E extends RealmObject> RealmQuery<E> where(Class<E> clazz)
    public void testShouldReturnResultSet() {

        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).findAll();

        assertEquals("Realm.get is returning wrong number of objects", TEST_DATA_SIZE, resultList.size());
    }

    // Note that this test is relying on the values set while initializing the test dataset
    public void testQueriesResults() throws IOException {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).equalTo(FIELD_LONG, 33).findAll();
        assertEquals("ResultList.where not returning expected result", 1, resultList.size());

        resultList = testRealm.where(AllTypes.class).equalTo(FIELD_LONG, 3333).findAll();
        assertEquals("ResultList.where not returning expected result", 0, resultList.size());

        resultList = testRealm.where(AllTypes.class).equalTo(FIELD_STRING, "test data 0").findAll();
        assertEquals(1, resultList.size());

        resultList = testRealm.where(AllTypes.class).equalTo(FIELD_STRING, "test data 0", RealmQuery.CASE_INSENSITIVE).findAll();
        assertEquals(1, resultList.size());

        resultList = testRealm.where(AllTypes.class).equalTo(FIELD_STRING, "Test data 0", RealmQuery.CASE_SENSITIVE).findAll();
        assertEquals(0, resultList.size());
    }

    public void testQueriesWithDataTypes() throws IOException {
        RealmResults<AllTypes> resultList = null;
        setColumnData();

        for (int i = 0; i < columnData.size(); i++) {
            try {
                resultList = testRealm.where(AllTypes.class).equalTo(columnData.get(i), true).findAll();
                if (i != 0) {
                    fail("Realm.where should fail with illegal argument");
                }
            } catch (IllegalArgumentException e) {
            }

            try {
                resultList = testRealm.where(AllTypes.class).equalTo(columnData.get(i), new Date()).findAll();
                if (i != 1) {
                    fail("Realm.where should fail with illegal argument");
                }
            } catch (IllegalArgumentException e) {
            }

            try {
                resultList = testRealm.where(AllTypes.class).equalTo(columnData.get(i), 13.37d).findAll();
                if (i != 2) {
                    fail("Realm.where should fail with illegal argument");
                }
            } catch (IllegalArgumentException e) {
            }

            try {
                resultList = testRealm.where(AllTypes.class).equalTo(columnData.get(i), 13.3711f).findAll();
                if (i != 3) {
                    fail("Realm.where should fail with illegal argument");
                }
            } catch (IllegalArgumentException e) {
            }

            try {
                resultList = testRealm.where(AllTypes.class).equalTo(columnData.get(i), "test").findAll();
                if (i != 4) {
                    fail("Realm.where should fail with illegal argument");
                }
            } catch (IllegalArgumentException e) {
            }

            try {
                resultList = testRealm.where(AllTypes.class).equalTo(columnData.get(i), 1337).findAll();
                if (i != 5) {
                    fail("Realm.where should fail with illegal argument");
                }
            } catch (IllegalArgumentException e) {
            }
        }
    }

    public void testQueriesFailWithInvalidDataTypes() throws IOException {
        RealmResults<AllTypes> resultList = null;

        try {
            resultList = testRealm.where(AllTypes.class).equalTo("invalidcolumnname", 33).findAll();
            fail("Invalid field name");
        } catch (Exception e) {
        }

        try {
            resultList = testRealm.where(AllTypes.class).equalTo("invalidcolumnname", "test").findAll();
            fail("Invalid field name");
        } catch (Exception e) {
        }

        try {
            resultList = testRealm.where(AllTypes.class).equalTo("invalidcolumnname", true).findAll();
            fail("Invalid field name");
        } catch (Exception e) {
        }

        try {
            resultList = testRealm.where(AllTypes.class).equalTo("invalidcolumnname", 3.1415d).findAll();
            fail("Invalid field name");
        } catch (Exception e) {
        }

        try {
            resultList = testRealm.where(AllTypes.class).equalTo("invalidcolumnname", 3.1415f).findAll();
            fail("Invalid field name");
        } catch (Exception e) {
        }
    }

    public void testQueriesFailWithNullQueryValue() throws IOException {
        RealmResults<AllTypes> resultList = null;

        String nullString = null;
        Float nullFloat = null;
        Long nullLong = null;
        Boolean nullBoolean = null;

        try {
            resultList = testRealm.where(AllTypes.class).equalTo(FIELD_STRING, nullString).findAll();
            fail("Realm.where should fail with illegal argument");
        } catch (IllegalArgumentException e) {
        }

        try {
            resultList = testRealm.where(AllTypes.class).equalTo(FIELD_LONG, nullLong).findAll();
            fail("Realm.where should fail with illegal argument");

        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
        }

        try {
            resultList = testRealm.where(AllTypes.class).equalTo(FIELD_BOOLEAN, nullBoolean).findAll();
            fail("Realm.where should fail with illegal argument");
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
        }

        try {
            resultList = testRealm.where(AllTypes.class).equalTo(FIELD_FLOAT, nullFloat).findAll();
            fail("Realm.where should fail with illegal argument");
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
        }
    }

    // <E extends RealmObject> RealmTableOrViewList<E> allObjects(Class<E> clazz)
    public void testShouldReturnTableOrViewList() {
        RealmResults<AllTypes> resultList = testRealm.allObjects(AllTypes.class);
        assertEquals("Realm.get is returning wrong result set", TEST_DATA_SIZE, resultList.size());
    }

    // void beginTransaction()
    public void testBeginTransaction() throws IOException {

        testRealm.beginTransaction();

        AllTypes allTypes = testRealm.createObject(AllTypes.class);
        allTypes.setColumnFloat(3.1415f);
        allTypes.setColumnString("a unique string");
        testRealm.commitTransaction();

        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).findAll();
        assertEquals("Change has not been committed", TEST_DATA_SIZE + 1, resultList.size());

        resultList = testRealm.where(AllTypes.class).equalTo(FIELD_STRING, "a unique string").findAll();
        assertEquals("Change has not been committed correctly", 1, resultList.size());
        resultList = testRealm.where(AllTypes.class).equalTo(FIELD_FLOAT, 3.1415f).findAll();
        assertEquals("Change has not been committed", 1, resultList.size());
    }

    public void testNestedTransaction() {
        testRealm.beginTransaction();
        try {
            testRealm.beginTransaction();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Nested transactions are not allowed. Use commitTransaction() after each beginTransaction().", e.getMessage());
        }
        testRealm.commitTransaction();
    }

    private enum TransactionMethod {
        METHOD_BEGIN,
        METHOD_COMMIT,
        METHOD_CANCEL
    }

    // Starting a transaction on the wrong thread will fail
    public boolean transactionMethodWrongThread(final TransactionMethod method) throws InterruptedException, ExecutionException {
        final Realm realm = Realm.getInstance(getContext());

        if (method != TransactionMethod.METHOD_BEGIN) {
            realm.beginTransaction();
            Dog dog = realm.createObject(Dog.class); // FIXME: Empty transactions cannot be cancelled
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    switch (method) {
                        case METHOD_BEGIN:
                            realm.beginTransaction();
                            break;
                        case METHOD_COMMIT:
                            realm.commitTransaction();
                            break;
                        case METHOD_CANCEL:
                            realm.cancelTransaction();
                            break;
                    }
                    return false;
                } catch (IllegalStateException ignored) {
                    return true;
                }
            }
        });

        boolean result = future.get();
        if (result && method != TransactionMethod.METHOD_BEGIN) {
            realm.cancelTransaction();
        }
        return result;
    }

    public void testTransactionWrongThread() throws ExecutionException, InterruptedException {
        for (TransactionMethod method : TransactionMethod.values()) {
            assertTrue(method.toString(), transactionMethodWrongThread(method));
        }
    }

    // void commitTransaction()
    public void testCommitTransaction() {
        testRealm.beginTransaction();
        AllTypes allTypes = testRealm.createObject(AllTypes.class);
        allTypes.setColumnBoolean(true);
        testRealm.commitTransaction();

        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).findAll();
        assertEquals("Change has not been committed", TEST_DATA_SIZE + 1, resultList.size());
    }


    public void testCancelTransaction() {
        testRealm.beginTransaction();
        AllTypes allTypes = testRealm.createObject(AllTypes.class);
        testRealm.cancelTransaction();
        assertEquals(TEST_DATA_SIZE, testRealm.allObjects(AllTypes.class).size());

        try {
            testRealm.cancelTransaction();
            fail();
        } catch (IllegalStateException ignored) {}
    }

    // void clear(Class<?> classSpec)
    public void testClassClear() {

        // Currently clear will not work outside a transaction:

        testRealm.beginTransaction();
        testRealm.clear(AllTypes.class);
        testRealm.commitTransaction();

        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).findAll();
        assertEquals("Realm.clear does not empty table", 0, resultList.size());
    }

    // void clear(Class<?> classSpec)
    public void testClassClearWithTwoTables() {
        testRealm.beginTransaction();

        Dog dog = testRealm.createObject(Dog.class);
        dog.setName("Castro");

        testRealm.commitTransaction();

        // NOTE:
        // Currently clear will not work outside a transaction
        // if you want this test not to fail, add begin- and commitTransaction

        testRealm.beginTransaction();
        testRealm.clear(Dog.class);
        testRealm.commitTransaction();

        RealmResults<AllTypes> resultListTypes = testRealm.where(AllTypes.class).findAll();
        RealmResults<Dog> resultListDogs = testRealm.where(Dog.class).findAll();

        assertEquals("Realm.clear does not clear table", 0, resultListDogs.size());
        assertEquals("Realm.clear cleared wrong table", TEST_DATA_SIZE, resultListTypes.size());


        testRealm.beginTransaction();
        testRealm.clear(AllTypes.class);
        testRealm.commitTransaction();

        resultListTypes = testRealm.where(AllTypes.class).findAll();
        assertEquals("Realm.clear does not remove table", 0, resultListTypes.size());
    }

    // int getVersion()
    public void testGetVersion() throws IOException {

        long version = testRealm.getVersion();

        assertTrue("Realm.version returns invalid version number", version >= 0);
    }

    // void setVersion(int version)setVersion(int version)
    public void testSetVersion() {
        long version = 42;

        testRealm.beginTransaction();
        testRealm.setVersion(version);
        testRealm.commitTransaction();

        assertEquals("Realm.version has not been set by setVersion", version, testRealm.getVersion());
    }

    public void testShouldFailOutsideTransaction() {

        // These API calls should fail outside a Transaction:
        try {
            AllTypes aT = testRealm.createObject(AllTypes.class);
            fail("Realm.createObject should fail outside write transaction");
        } catch (IllegalStateException e) {
        }
        try {
            testRealm.remove(AllTypes.class, 0);
            fail("Realm.remove should fail outside write transaction");
        } catch (IllegalStateException e) {
        }
    }

    public void testRealmQueryBetween() {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).between(FIELD_LONG, 0, 9).findAll();
        assertEquals(10, resultList.size());

        resultList = testRealm.where(AllTypes.class).beginsWith(FIELD_STRING, "test data ").findAll();
        assertEquals(TEST_DATA_SIZE, resultList.size());

        resultList = testRealm.where(AllTypes.class).beginsWith(FIELD_STRING, "test data 1").between(FIELD_LONG, 2, 20).findAll();
        assertEquals(10, resultList.size());

        resultList = testRealm.where(AllTypes.class).between(FIELD_LONG, 2, 20).beginsWith(FIELD_STRING, "test data 1").findAll();
        assertEquals(10, resultList.size());
    }

    public void testRealmQueryGreaterThan() {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).greaterThan(FIELD_FLOAT, 10.234567f).findAll();
        assertEquals(TEST_DATA_SIZE - 10, resultList.size());

        resultList = testRealm.where(AllTypes.class).beginsWith(FIELD_STRING, "test data 1").greaterThan(FIELD_FLOAT, 50.234567f).findAll();
        assertEquals(TEST_DATA_SIZE - 100, resultList.size());

        RealmQuery<AllTypes> query = testRealm.where(AllTypes.class).greaterThan(FIELD_FLOAT, 11.234567f);
        resultList = query.between(FIELD_LONG, 1, 20).findAll();
        assertEquals(10, resultList.size());
    }


    public void testRealmQueryGreaterThanOrEqualTo() {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).greaterThanOrEqualTo(FIELD_FLOAT, 10.234567f).findAll();
        assertEquals(TEST_DATA_SIZE - 9, resultList.size());

        resultList = testRealm.where(AllTypes.class).beginsWith(FIELD_STRING, "test data 1").greaterThanOrEqualTo(FIELD_FLOAT, 50.234567f).findAll();
        assertEquals(TEST_DATA_SIZE - 100, resultList.size());

        RealmQuery<AllTypes> query = testRealm.where(AllTypes.class).greaterThanOrEqualTo(FIELD_FLOAT, 11.234567f);
        query = query.between(FIELD_LONG, 1, 20);

        resultList = query.beginsWith(FIELD_STRING, "test data 15").findAll();
        assertEquals(1, resultList.size());
    }

    public void testRealmQueryOr() {
        RealmQuery<AllTypes> query = testRealm.where(AllTypes.class).equalTo(FIELD_FLOAT, 31.234567f);
        RealmResults<AllTypes> resultList = query.or().between(FIELD_LONG, 1, 20).findAll();
        assertEquals(21, resultList.size());

        resultList = query.or().equalTo(FIELD_STRING, "test data 15").findAll();
        assertEquals(21, resultList.size());

        resultList = query.or().equalTo(FIELD_STRING, "test data 117").findAll();
        assertEquals(22, resultList.size());
    }

    public void testRealmQueryImplicitAnd() {
        RealmQuery<AllTypes> query = testRealm.where(AllTypes.class).equalTo(FIELD_FLOAT, 31.234567f);
        RealmResults<AllTypes> resultList = query.between(FIELD_LONG, 1, 10).findAll();
        assertEquals(0, resultList.size());

        query = testRealm.where(AllTypes.class).equalTo(FIELD_FLOAT, 81.234567f);
        resultList = query.between(FIELD_LONG, 1, 100).findAll();
        assertEquals(1, resultList.size());
    }

    public void testRealmQueryLessThan() {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).lessThan(FIELD_FLOAT, 31.234567f).findAll();
        assertEquals(30, resultList.size());
        RealmQuery<AllTypes> query = testRealm.where(AllTypes.class).lessThan(FIELD_FLOAT, 31.234567f);
        resultList = query.between(FIELD_LONG, 1, 10).findAll();
        assertEquals(10, resultList.size());
    }

    public void testRealmQueryLessThanOrEqual() {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).lessThanOrEqualTo(FIELD_FLOAT, 31.234567f).findAll();
        assertEquals(31, resultList.size());
        resultList = testRealm.where(AllTypes.class).lessThanOrEqualTo(FIELD_FLOAT, 31.234567f).between(FIELD_LONG, 11, 20).findAll();
        assertEquals(10, resultList.size());
    }

    public void testRealmQueryEqualTo() {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).equalTo(FIELD_FLOAT, 31.234567f).findAll();
        assertEquals(1, resultList.size());
        resultList = testRealm.where(AllTypes.class).greaterThan(FIELD_FLOAT, 11.0f).equalTo(FIELD_LONG, 10).findAll();
        assertEquals(1, resultList.size());
        resultList = testRealm.where(AllTypes.class).greaterThan(FIELD_FLOAT, 11.0f).equalTo(FIELD_LONG, 1).findAll();
        assertEquals(0, resultList.size());
    }

    public void testRealmQueryEqualToNonLatinCharacters() {
        RealmResults<NonLatinFieldNames> resultList = testRealm.where(NonLatinFieldNames.class).equalTo(FIELD_LONG_KOREAN_CHAR, 13).findAll();
        assertEquals(1, resultList.size());
        resultList = testRealm.where(NonLatinFieldNames.class).greaterThan(FIELD_FLOAT_KOREAN_CHAR, 11.0f).equalTo(FIELD_LONG_KOREAN_CHAR, 10).findAll();
        assertEquals(1, resultList.size());
        resultList = testRealm.where(NonLatinFieldNames.class).greaterThan(FIELD_FLOAT_KOREAN_CHAR, 11.0f).equalTo(FIELD_LONG_KOREAN_CHAR, 1).findAll();
        assertEquals(0, resultList.size());

        resultList = testRealm.where(NonLatinFieldNames.class).equalTo(FIELD_LONG_GREEK_CHAR, 13).findAll();
        assertEquals(1, resultList.size());
        resultList = testRealm.where(NonLatinFieldNames.class).greaterThan(FIELD_FLOAT_GREEK_CHAR, 11.0f).equalTo(FIELD_LONG_GREEK_CHAR, 10).findAll();
        assertEquals(1, resultList.size());
        resultList = testRealm.where(NonLatinFieldNames.class).greaterThan(FIELD_FLOAT_GREEK_CHAR, 11.0f).equalTo(FIELD_LONG_GREEK_CHAR, 1).findAll();
        assertEquals(0, resultList.size());
    }

    public void testRealmQueryNotEqualTo() {
        RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).notEqualTo(FIELD_LONG, 31).findAll();
        assertEquals(TEST_DATA_SIZE - 1, resultList.size());
        resultList = testRealm.where(AllTypes.class).notEqualTo(FIELD_FLOAT, 11.234567f).equalTo(FIELD_LONG, 10).findAll();
        assertEquals(0, resultList.size());
        resultList = testRealm.where(AllTypes.class).notEqualTo(FIELD_FLOAT, 11.234567f).equalTo(FIELD_LONG, 1).findAll();
        assertEquals(1, resultList.size());
    }

    public void testQueryWithNonExistingField () {
        try {
            RealmResults<AllTypes> resultList = testRealm.where(AllTypes.class).equalTo("NotAField", 13).findAll();
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {

        }
    }

    public void createAndTestFilename(String language, String fileName) {
        Realm.deleteRealmFile(getContext(), fileName);
        Realm realm1 = Realm.getInstance(getContext(), fileName);
        realm1.beginTransaction();
        Dog dog1 = realm1.createObject(Dog.class);
        dog1.setName("Rex");
        realm1.commitTransaction();

        File file = new File(getContext().getFilesDir()+"/"+fileName);
        assertTrue(language, file.exists());

        Realm realm2 = Realm.getInstance(getContext(), fileName);
        Dog dog2 = realm2.allObjects(Dog.class).first();
        assertEquals(language, "Rex", dog2.getName());
    }

    public void testCreateFile() {
        createAndTestFilename("American", "Washington");
        createAndTestFilename("Danish", "København");
        createAndTestFilename("Russian", "Москва");
        createAndTestFilename("Greek", "Αθήνα");
        createAndTestFilename("Chinese", "北京市");
        createAndTestFilename("Korean", "서울시");
        createAndTestFilename("Arabic", "الرياض");
        createAndTestFilename("India", "नई दिल्ली");
        createAndTestFilename("Japanese", "東京都");
    }

    /* NOTE: This is commented out while we fix the other unit tests to be good citizens in Closeable land :)

    public void testReferenceCounting() {
        // At this point reference count should be one because of the setUp method
        try {
            testRealm.where(AllTypes.class).count();
        } catch (IllegalStateException e) {
            fail();
        }

        // Raise the reference
        Realm realm = Realm.getInstance(getContext());
        realm.close();
        try {
            // This should not fail because the reference is now 2
            realm.where(AllTypes.class).count();
        } catch (IllegalStateException e) {
            fail();
        }

        testRealm.close();
        try {
            testRealm.where(AllTypes.class).count();
            fail();
        } catch (IllegalStateException ignored) {}
    }
    */
}
