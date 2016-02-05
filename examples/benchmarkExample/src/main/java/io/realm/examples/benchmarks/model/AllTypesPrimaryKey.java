///*
// * Copyright 2014 Realm Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package io.realm.examples.benchmarks.model;
//
//import java.util.Date;
//
//import io.realm.RealmList;
//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//
//public class AllTypesPrimaryKey extends RealmObject {
//    private String columnString;
//    @PrimaryKey
//    private long columnLong;
//    private float columnFloat;
//    private double columnDouble;
//    private boolean columnBoolean;
//    private Date columnDate;
//    private byte[] columnBinary;
//    private DogPrimaryKey columnRealmObject;
//    private RealmList<DogPrimaryKey> columnRealmList;
//    private Boolean columnBoxedBoolean;
//
//    public String getColumnString() {
//        return realmGetter$columnString();
//    }
//
//    public void setColumnString(String columnString) {
//        realmSetter$columnString(columnString);
//    }
//
//    public String realmGetter$columnString() {
//        return columnString;
//    }
//
//    public void realmSetter$columnString(String columnString) {
//        this.columnString = columnString;
//    }
//
//    public long getColumnLong() {
//        return realmGetter$columnLong();
//    }
//
//    public void setColumnLong(long columnLong) {
//        realmSetter$columnLong(columnLong);
//    }
//
//    public long realmGetter$columnLong() {
//        return columnLong;
//    }
//
//    public void realmSetter$columnLong(long columnLong) {
//        this.columnLong = columnLong;
//    }
//
//    public float getColumnFloat() {
//        return realmGetter$columnFloat();
//    }
//
//    public void setColumnFloat(float columnFloat) {
//        realmSetter$columnFloat(columnFloat);
//    }
//
//    public float realmGetter$columnFloat() {
//        return columnFloat;
//    }
//
//    public void realmSetter$columnFloat(float columnFloat) {
//        this.columnFloat = columnFloat;
//    }
//
//    public double getColumnDouble() {
//        return realmGetter$columnDouble();
//    }
//
//    public void setColumnDouble(double columnDouble) {
//        realmSetter$columnDouble(columnDouble);
//    }
//
//    public double realmGetter$columnDouble() {
//        return columnDouble;
//    }
//
//    public void realmSetter$columnDouble(double columnDouble) {
//        this.columnDouble = columnDouble;
//    }
//
//    public boolean isColumnBoolean() {
//        return realmGetter$columnBoolean();
//    }
//
//    public void setColumnBoolean(boolean columnBoolean) {
//        realmSetter$columnBoolean(columnBoolean);
//    }
//
//    public boolean realmGetter$columnBoolean() {
//        return columnBoolean;
//    }
//
//    public void realmSetter$columnBoolean(boolean columnBoolean) {
//        this.columnBoolean = columnBoolean;
//    }
//
//    public Date getColumnDate() {
//        return realmGetter$columnDate();
//    }
//
//    public void setColumnDate(Date columnDate) {
//        realmSetter$columnDate(columnDate);
//    }
//
//    public Date realmGetter$columnDate() {
//        return columnDate;
//    }
//
//    public void realmSetter$columnDate(Date columnDate) {
//        this.columnDate = columnDate;
//    }
//
//    public byte[] getColumnBinary() {
//        return realmGetter$columnBinary();
//    }
//
//    public void setColumnBinary(byte[] columnBinary) {
//        realmSetter$columnBinary(columnBinary);
//    }
//
//    public byte[] realmGetter$columnBinary() {
//        return columnBinary;
//    }
//
//    public void realmSetter$columnBinary(byte[] columnBinary) {
//        this.columnBinary = columnBinary;
//    }
//
//    public DogPrimaryKey getColumnRealmObject() {
//        return realmGetter$columnRealmObject();
//    }
//
//    public void setColumnRealmObject(DogPrimaryKey columnRealmObject) {
//        realmSetter$columnRealmObject(columnRealmObject);
//    }
//
//    public DogPrimaryKey realmGetter$columnRealmObject() {
//        return columnRealmObject;
//    }
//
//    public void realmSetter$columnRealmObject(DogPrimaryKey columnRealmObject) {
//        this.columnRealmObject = columnRealmObject;
//    }
//
//    public RealmList<DogPrimaryKey> getColumnRealmList() {
//        return realmGetter$columnRealmList();
//    }
//
//    public void setColumnRealmList(RealmList<DogPrimaryKey> columnRealmList) {
//        realmSetter$columnRealmList(columnRealmList);
//    }
//
//    public RealmList<DogPrimaryKey> realmGetter$columnRealmList() {
//        return columnRealmList;
//    }
//
//    public void realmSetter$columnRealmList(RealmList<DogPrimaryKey> columnRealmList) {
//        this.columnRealmList = columnRealmList;
//    }
//
//    public Boolean getColumnBoxedBoolean() {
//        return realmGetter$columnBoxedBoolean();
//    }
//
//    public void setColumnBoxedBoolean(Boolean columnBoxedBoolean) {
//        realmSetter$columnBoxedBoolean(columnBoxedBoolean);
//    }
//
//    public Boolean realmGetter$columnBoxedBoolean() {
//        return columnBoxedBoolean;
//    }
//
//    public void realmSetter$columnBoxedBoolean(Boolean columnBoxedBoolean) {
//        this.columnBoxedBoolean = columnBoxedBoolean;
//    }
//}
