package com.example.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: BYDylan
 * @date: 2022/2/5
 * @description: 测试属性注入
 */
@Component
@ConfigurationProperties(prefix = "person")
public class PersonModel {
    private String lastName;
    private int age;
    private boolean boss;
    private Date birth;
    private Map<String, Object> maps;
    private List<Object> lists;
    private DogModel dog;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isBoss() {
        return boss;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Map<String, Object> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, Object> maps) {
        this.maps = maps;
    }

    public List<Object> getLists() {
        return lists;
    }

    public void setLists(List<Object> lists) {
        this.lists = lists;
    }

    public DogModel getDog() {
        return dog;
    }

    public void setDog(DogModel dog) {
        this.dog = dog;
    }

    @Override
    public String toString() {
        return "PersonEntity{" + "lastName='" + lastName + '\'' + ", age=" + age + ", boss=" + boss + ", birth=" + birth
            + ", maps=" + maps + ", lists=" + lists + ", dog=" + dog + '}';
    }
}
