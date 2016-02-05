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
//import io.realm.RealmList;
//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//
//public class OwnerPrimaryKey extends RealmObject {
//
//    @PrimaryKey
//    private long id;
//
//    private String name;
//    private DogPrimaryKey dog;
//    private RealmList<Dog> dogs;
//
//    public OwnerPrimaryKey() {
//    }
//
//    public OwnerPrimaryKey(long id, String name) {
//        this.id = id;
//        this.name = name;
//    }
//
//    public long getId() {
//        return realmGetter$id();
//    }
//
//    public void setId(long id) {
//        realmSetter$id(id);
//    }
//
//    public long realmGetter$id() {
//        return id;
//    }
//
//    public void realmSetter$id(long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return realmGetter$name();
//    }
//
//    public void setName(String name) {
//        realmSetter$name(name);
//    }
//
//    public String realmGetter$name() {
//        return name;
//    }
//
//    public void realmSetter$name(String name) {
//        this.name = name;
//    }
//
//    public DogPrimaryKey getDog() {
//        return realmGetter$dog();
//    }
//
//    public void setDog(DogPrimaryKey dog) {
//        realmSetter$dog(dog);
//    }
//
//    public DogPrimaryKey realmGetter$dog() {
//        return dog;
//    }
//
//    public void realmSetter$dog(DogPrimaryKey dog) {
//        this.dog = dog;
//    }
//
//    public RealmList<Dog> getDogs() {
//        return realmGetter$dogs();
//    }
//
//    public void setDogs(RealmList<Dog> dogs) {
//        realmSetter$dogs(dogs);
//    }
//
//    public RealmList<Dog> realmGetter$dogs() {
//        return dogs;
//    }
//
//    public void realmSetter$dogs(RealmList<Dog> dogs) {
//        this.dogs = dogs;
//    }
//}
