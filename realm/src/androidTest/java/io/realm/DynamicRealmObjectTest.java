/*
 * Copyright 2015 Realm Inc.
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

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.dynamic.DynamicRealmList;
import io.realm.dynamic.DynamicRealmObject;
import io.realm.entities.AllJavaTypes;

import static io.realm.internal.test.ExtraTests.assertArrayEquals;

public class DynamicRealmObjectTest extends AndroidTestCase {

    private Realm realm;
    private DynamicRealmObject dObj;

    @Override
    protected void setUp() throws Exception {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).schema(AllJavaTypes.class).build();
        Realm.deleteRealm(realmConfig);
        realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        AllJavaTypes obj = realm.createObject(AllJavaTypes.class);
        obj.setFieldString("str");
        obj.setFieldShort((short) 1);
        obj.setFieldInt(1);
        obj.setFieldLong(1);
        obj.setFieldFloat(1.23f);
        obj.setFieldDouble(1.234d);
        obj.setFieldBinary(new byte[]{1, 2, 3});
        obj.setFieldBoolean(true);
        obj.setFieldDate(new Date(1000));
        obj.setFieldObject(obj);
        obj.getFieldList().add(obj);
        dObj = new DynamicRealmObject(realm, obj.row);
        realm.commitTransaction();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        realm.close();
    }

    public enum SupportedType {
        BOOLEAN, SHORT, INT, LONG, FLOAT, DOUBLE, STRING, BINARY, DATE, OBJECT, LIST;
    }

    // Test that all getters fail if given invalid field name
    public void testGetXXXIllegalArgumentsThrows() {

        // Set arguments
        String linkedField = AllJavaTypes.FIELD_OBJECT + "." + AllJavaTypes.FIELD_STRING;
        List<String> arguments = Arrays.asList(null, "foo", AllJavaTypes.FIELD_STRING, linkedField);
        List<String> stringArguments = Arrays.asList(null, "foo", AllJavaTypes.FIELD_BOOLEAN, linkedField);

        // Test all getters
        for (SupportedType type : SupportedType.values()) {
            List<String> args = (type == SupportedType.STRING) ? stringArguments : arguments;
            try {
                callGetter(type, args);
                fail();
            } catch(IllegalArgumentException expected) {
            }
        }
    }

    private void callGetter(SupportedType type, List<String> arguments) {
        for (String fieldName : arguments) {
            switch(type) {
                case BOOLEAN: dObj.getBoolean(fieldName); break;
                case SHORT: dObj.getShort(fieldName); break;
                case INT: dObj.getInt(fieldName); break;
                case LONG: dObj.getLong(fieldName); break;
                case FLOAT: dObj.getFloat(fieldName); break;
                case DOUBLE: dObj.getDouble(fieldName); break;
                case STRING: dObj.getString(fieldName); break;
                case BINARY: dObj.getBytes(fieldName); break;
                case DATE: dObj.getDate(fieldName); break;
                case OBJECT: dObj.getRealmObject(fieldName); break;
                case LIST: dObj.getRealmList(fieldName); break;
                default:
                    fail();
            }
        }
    }

    // Test all simple getters
    public void testGetXXX() {
        for (SupportedType type : SupportedType.values()) {
            switch(type) {
                case BOOLEAN: assertTrue(dObj.getBoolean(AllJavaTypes.FIELD_BOOLEAN)); break;
                case SHORT: assertEquals(1, dObj.getShort(AllJavaTypes.FIELD_SHORT)); break;
                case INT: assertEquals(1, dObj.getInt(AllJavaTypes.FIELD_INT)); break;
                case LONG: assertEquals(1, dObj.getLong(AllJavaTypes.FIELD_LONG)); break;
                case FLOAT: assertEquals(1.23f, dObj.getFloat(AllJavaTypes.FIELD_FLOAT)); break;
                case DOUBLE: assertEquals(1.234d, dObj.getDouble(AllJavaTypes.FIELD_DOUBLE)); break;
                case STRING: assertEquals("str", dObj.getString(AllJavaTypes.FIELD_STRING)); break;
                case BINARY: assertArrayEquals(new byte[]{1, 2, 3}, dObj.getBytes(AllJavaTypes.FIELD_BINARY)); break;
                case DATE: assertEquals(new Date(1000), dObj.getDate(AllJavaTypes.FIELD_DATE)); break;
                case OBJECT: assertEquals(dObj, dObj.getRealmObject(AllJavaTypes.FIELD_OBJECT)); break;
                case LIST: /* ignore */ break;
                default:
                    fail();
            }
        }
    }

    // List is not a simple getter, test separately.
    public void testGetList() {
        DynamicRealmList list = dObj.getRealmList(AllJavaTypes.FIELD_LIST);
        assertEquals(1, list.size());
        assertEquals(dObj, list.get(0));
    }

    public void testIsNullWithNullNotSupportedField() {
        assertFalse(dObj.isNull(AllJavaTypes.FIELD_INT));
    }

    public void testIsNullTrue() {
        realm.beginTransaction();
        AllJavaTypes obj = realm.createObject(AllJavaTypes.class);
        realm.commitTransaction();

        assertTrue(new DynamicRealmObject(obj.realm, obj.row).isNull(AllJavaTypes.FIELD_OBJECT));
    }

    public void testIsNullFalse() {
        assertFalse(dObj.isNull(AllJavaTypes.FIELD_OBJECT));
    }

    public void testGetFieldNames() {
        String[] expectedKeys = { AllJavaTypes.FIELD_STRING, AllJavaTypes.FIELD_SHORT, AllJavaTypes.FIELD_INT,
                AllJavaTypes.FIELD_LONG, AllJavaTypes.FIELD_FLOAT, AllJavaTypes.FIELD_DOUBLE, AllJavaTypes.FIELD_BOOLEAN,
                AllJavaTypes.FIELD_DATE, AllJavaTypes.FIELD_BINARY, AllJavaTypes.FIELD_OBJECT, AllJavaTypes.FIELD_LIST };
        String[] keys = dObj.getFieldNames();
        assertArrayEquals(expectedKeys, keys);
    }

    public void testHasFieldFalse() {
        assertFalse(dObj.hasField(null));
        assertFalse(dObj.hasField(""));
        assertFalse(dObj.hasField("foo"));
        assertFalse(dObj.hasField("foo.bar"));
    }

    public void testHasFieldTrue() {
        assertTrue(dObj.hasField(AllJavaTypes.FIELD_STRING));
    }

//    public void testEquals() {
//        AllJavaTypes obj1 = realm.where(AllJavaTypes.class).findFirst();
//        AllJavaTypes obj2 = realm.where(AllJavaTypes.class).findFirst();
//        DynamicRealmObject dObj1 = new DynamicRealmObject(realm, obj1.row);
//        DynamicRealmObject dObj2 = new DynamicRealmObject(realm, obj2.row);
//        assertTrue(dObj1.equals(dObj2));
//    }

    public void testHashcode() {
        AllJavaTypes obj1 = realm.where(AllJavaTypes.class).findFirst();
        DynamicRealmObject dObj1 = new DynamicRealmObject(realm, obj1.row);
        assertEquals(obj1.hashCode(), dObj1.hashCode());
    }

//    public void testToString() {
//        // TODO
//    }
}
