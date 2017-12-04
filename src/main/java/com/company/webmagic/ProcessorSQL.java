package com.company.webmagic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.company.utils.mysqlUtils;

public class ProcessorSQL {
    /**
     * 判断表格，判断某门课程的数据是否已经在这个数据表中存在
     * 适用表格：domain_layer，domain_topic，dependency
     * @param table
     * @param domain
     * @return true表示该领域已经爬取
     */
    public Boolean judgeByClass(String table, String domain){
        mysqlUtils mysql = new mysqlUtils();
        Boolean exist = false;
        String sql = "select * from " + table + " where ClassName=?";
        List<Object> params = new ArrayList<Object>();
        params.add(domain);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            if (results.size()!=0) {
                exist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return exist;
    }
    /**
    *获取表格中的所有课程
     * 使用表格:domain
     * @param table 表格名
     * @return 课程列表
    */
    public List<String> getCourses(String table){
        List<String> courses = new ArrayList<>();
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select ClassName from "+ table;
        List<Object> params = new ArrayList<Object>();
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql,params);
            for(Map<String,Object> m:results){
                courses.add((String) m.get("ClassName"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return courses;
    }



    /**判断：某个分面是否已经存在数据库
     *适用表格：facet
     * @param table 表名
     * @param className 课程名
     * @param termName  主题名
     * @param facetName 分面名
     * @return true表示该课程下该主题的该分面已经存在数据库
     */
    public Boolean judgeFacetByClassAndTerm(String table, String className, String termName, String facetName){
        mysqlUtils mysql = new mysqlUtils();
        Boolean exist = false;
        String sql = "select * from " + table + " where ClassName=? and TermName=? and FacetName=?";
        List<Object> params = new ArrayList<Object>();
        params.add(className);
        params.add(termName);
        params.add(facetName);
        try {
            List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
            if (results.size()!=0) {
                exist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return exist;
    }
    /**获取分面信息（其中包括课程、主题、分面）
     * @param facetTable 分面表
     * @return allFacetsInformation 所有的分面
     * */
    public List<Map<String, Object>> getAllFacets(String facetTable,String courseName){
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        mysqlUtils mysql = new mysqlUtils();
        String sql = "select * from "+  facetTable +" where ClassName=?";
        List<Object> params = new ArrayList<Object>();
        params.add(courseName);
        try {
            results = mysql.returnMultipleResult(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mysql.closeconnection();
        }
        return results;
    }
}
