package com.example.model;

import java.util.Date;

/***
 * @author: BYDylan
 * @date: 2022/2/21
 * @description:
 */
public class UserInfoModel {
    private String name;
    private int age;
    private float salary;
    private String address;
    private String remark;
    private Date createTime;
    private String birthDate;

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

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "UserInfoEntity{" + "name='" + name + '\'' + ", age=" + age + ", salary=" + salary + ", address='"
            + address + '\'' + ", remark='" + remark + '\'' + ", createTime=" + createTime + ", birthDate='" + birthDate
            + '\'' + '}';
    }
}
