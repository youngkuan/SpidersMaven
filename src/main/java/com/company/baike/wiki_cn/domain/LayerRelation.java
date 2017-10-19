package com.company.baike.wiki_cn.domain;

/**
 * 主题的上下位关系
 * Created by yuanhao on 2017/10/18.
 */
public class LayerRelation {

    public static void main(String[] args) {
        LayerRelation layerRelation1 = new LayerRelation("a", 1, "b", 2, "c");
        LayerRelation layerRelation2 = new LayerRelation("a", 1, "b", 2, "c");
        LayerRelation layerRelation3 = new LayerRelation("a", 1, "b", 2, "d");
        System.out.println(layerRelation1.equals(layerRelation2));
        System.out.println(layerRelation1.equals(layerRelation3));
    }

    public String parentName;
    public int parentLayer;
    public String childName;
    public int childLayer;
    public String domain;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LayerRelation that = (LayerRelation) o;

        if (parentLayer != that.parentLayer) {
            return false;
        }
        if (childLayer != that.childLayer) {
            return false;
        }
        if (parentName != null ? !parentName.equals(that.parentName) : that.parentName != null) {
            return false;
        }
        if (childName != null ? !childName.equals(that.childName) : that.childName != null) {
            return false;
        }
        return domain != null ? domain.equals(that.domain) : that.domain == null;
    }

    @Override
    public int hashCode() {
        int result = parentName != null ? parentName.hashCode() : 0;
        result = 31 * result + parentLayer;
        result = 31 * result + (childName != null ? childName.hashCode() : 0);
        result = 31 * result + childLayer;
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LayerRelation{" +
                "parentName='" + parentName + '\'' +
                ", parentLayer=" + parentLayer +
                ", childName='" + childName + '\'' +
                ", childLayer=" + childLayer +
                ", domain='" + domain + '\'' +
                '}';
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public int getParentLayer() {
        return parentLayer;
    }

    public void setParentLayer(int parentLayer) {
        this.parentLayer = parentLayer;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public int getChildLayer() {
        return childLayer;
    }

    public void setChildLayer(int childLayer) {
        this.childLayer = childLayer;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public LayerRelation() {

    }

    public LayerRelation(String parentName, int parentLayer, String childName, int childLayer, String domain) {

        this.parentName = parentName;
        this.parentLayer = parentLayer;
        this.childName = childName;
        this.childLayer = childLayer;
        this.domain = domain;
    }
}
