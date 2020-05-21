package com.person.tm.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class testComparator {
    public static void main(String[] args) {

        List<Person> people = new ArrayList<>();
        people.add(new Person("张三", 19));
        people.add(new Person("李白", 22));
        people.add(new Person("莉莉安", 40));
        people.add(new Person("绝地武士", 19));
        people.add(new Person("张琪", 16));

        people.sort(Comparator.comparing(Person::getName));

        people.sort(Comparator.comparing(Person::getAge).reversed());

        people.forEach(person -> {
            System.out.println("p :" + person);
        });


    }
}

class Person {
    private String name;
    private int age;


    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}