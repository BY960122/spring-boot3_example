package com.example.model;

/**
 * @author: BYDylan
 * @date: 2022/2/20
 * @description:
 */
public class StatusModel {
    private Integer weight;
    private Integer height;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "StatusEntity{" + "weight=" + weight + ", height=" + height + '}';
    }
}
