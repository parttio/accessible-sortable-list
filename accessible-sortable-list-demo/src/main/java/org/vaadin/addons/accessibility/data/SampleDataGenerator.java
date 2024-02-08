package org.vaadin.addons.accessibility.data;

import java.util.ArrayList;
import java.util.List;

public class SampleDataGenerator {


    public static List<PersonRecord> getPersonRecordList() {
        List<PersonRecord> personRecords = new ArrayList<>();
        personRecords.add(new PersonRecord(1, "Smith"));
        personRecords.add(new PersonRecord(2, "Doe"));
        personRecords.add(new PersonRecord(3, "Norton"));
        personRecords.add(new PersonRecord(4, "Montoya"));
        personRecords.add(new PersonRecord(5, "Summers"));
        personRecords.add(new PersonRecord(6, "Diaz"));
        return personRecords;
    }


    public static List<PersonBean> getPersonBeanList() {
        List<PersonBean> personBeans = new ArrayList<>();
        personBeans.add(new PersonBean(1, "Joyce", "Norton"));
        personBeans.add(new PersonBean(2, "Neo", "Montoya"));
        personBeans.add(new PersonBean(3, "Cerys", "Cortez"));
        personBeans.add(new PersonBean(4, "Ben", "Warren"));
        personBeans.add(new PersonBean(5, "Isra", "Summers"));
        personBeans.add(new PersonBean(6, "Lyndon", "Guzman"));
        personBeans.add(new PersonBean(7, "Nicholas", "Diaz"));
        personBeans.add(new PersonBean(8, "Alessia", "Mueller"));
        personBeans.add(new PersonBean(9, "Esme", "Weaver"));
        personBeans.add(new PersonBean(10, "Mustafa", "Edwards"));
        return personBeans;
    }


}
