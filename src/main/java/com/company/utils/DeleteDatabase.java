package com.company.utils;

import com.company.app.Config;

import java.util.*;

public class DeleteDatabase {

    public static void main(String[] args) {
        String domainName = "软件工程";
        // 删除数据库中这门课程的数据
        List<String> tableList = new ArrayList<>();
        tableList.add(Config.DOMAIN_TABLE);
        tableList.add(Config.DOMAIN_LAYER_TABLE);
        tableList.add(Config.DOMAIN_LAYER_FUZHU_TABLE);
        tableList.add(Config.DOMAIN_LAYER_FUZHU2_TABLE);
        tableList.add(Config.DOMAIN_LAYER_RELATION_TABLE);
        tableList.add(Config.DOMAIN_TOPIC_TABLE);
        tableList.add(Config.DOMAIN_TOPIC_RELATION_TABLE);
        tableList.add(Config.FACET_TABLE);
        tableList.add(Config.FACET_RELATION_TABLE);
        tableList.add(Config.SPIDER_TEXT_TABLE);
        tableList.add(Config.SPIDER_IMAGE_TABLE);
        tableList.add(Config.ASSEMBLE_TEXT_TABLE);
        tableList.add(Config.ASSEMBLE_IMAGE_TABLE);
        tableList.add(Config.ASSEMBLE_FRAGMENT_TABLE);
        tableList.add(Config.DEPENDENCY);
        for (int i = 0; i < tableList.size(); i++) {
            deleteByTableAndDomain(tableList.get(i), domainName);
        }
        // 更新数据库表格自动增长ID的值
        HashMap<String, Integer> map = getMaxId();
        resetTableIncrement(map);
    }

    /**
     * 根据领域名删除数据
     * @param table 数据库表格名
     * @param domainName 领域名
     */
    public static void deleteByTableAndDomain(String table, String domainName) {
        mysqlUtils mysql = new mysqlUtils();
        String sql = "delete from " + table + " where ClassName = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(domainName);
        try {
            mysql.addDeleteModify(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mysql.closeconnection();
    }

    /**
     * 获取所有表格当前最大的编号值+1，设置每个表格自动增长的值为该值
     */
    public static HashMap<String, Integer> getMaxId() {
        HashMap<String, Integer> map = new HashMap<>();
        mysqlUtils mysql = new mysqlUtils();
        List<Object> params = new ArrayList<>();
        String sqlDomain = "select max(ClassID) as maxClassID from " + Config.DOMAIN_TABLE;
        String sqlDomainLayer = "select max(TermID) as maxTermID from " + Config.DOMAIN_LAYER_TABLE;
        String sqlDomainLayerFuzhu = "select max(TermID) as maxTermID from " + Config.DOMAIN_LAYER_FUZHU_TABLE;
        String sqlDomainLayerFuzhu2 = "select max(TermID) as maxTermID from " + Config.DOMAIN_LAYER_FUZHU2_TABLE;
        String sqlDomainLayerRelation = "select max(TopicRelationId) as maxTopicRelationId from " + Config.DOMAIN_LAYER_RELATION_TABLE;
        String sqlDomainTopic = "select max(TermID) as maxTermID from " + Config.DOMAIN_TOPIC_TABLE;
        String sqlSpiderText = "select max(FragmentID) as maxFragmentID from " + Config.SPIDER_TEXT_TABLE;
        String sqlSpiderImage = "select max(ImageID) as maxImageID from " + Config.SPIDER_IMAGE_TABLE;
        String sqlAssembleText = "select max(FragmentID) as maxFragmentID from " + Config.ASSEMBLE_TEXT_TABLE;
        String sqlAssembleImage = "select max(ImageID) as maxImageID from " + Config.ASSEMBLE_IMAGE_TABLE;
        String sqlAssembleFragment = "select max(FragmentID) as maxFragmentID from " + Config.ASSEMBLE_FRAGMENT_TABLE;
        try {
            map.put(Config.DOMAIN_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlDomain, params).get("maxClassID").toString()) + 1);
            map.put(Config.DOMAIN_LAYER_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlDomainLayer, params).get("maxTermID").toString()) + 1);
            map.put(Config.DOMAIN_LAYER_FUZHU_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlDomainLayerFuzhu, params).get("maxTermID").toString()) + 1);
            map.put(Config.DOMAIN_LAYER_FUZHU2_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlDomainLayerFuzhu2, params).get("maxTermID").toString()) + 1);
            map.put(Config.DOMAIN_LAYER_RELATION_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlDomainLayerRelation, params).get("maxTopicRelationId").toString()) + 1);
            map.put(Config.DOMAIN_TOPIC_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlDomainTopic, params).get("maxTermID").toString()) + 1);
            map.put(Config.SPIDER_TEXT_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlSpiderText, params).get("maxFragmentID").toString()) + 1);
            map.put(Config.SPIDER_IMAGE_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlSpiderImage, params).get("maxImageID").toString()) + 1);
            map.put(Config.ASSEMBLE_TEXT_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlAssembleText, params).get("maxFragmentID").toString()) + 1);
            map.put(Config.ASSEMBLE_IMAGE_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlAssembleImage, params).get("maxImageID").toString()) + 1);
            map.put(Config.ASSEMBLE_FRAGMENT_TABLE, Integer.parseInt(mysql.returnSimpleResult(sqlAssembleFragment, params).get("maxFragmentID").toString()) + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mysql.closeconnection();

        for (String table : map.keySet()) {
            System.out.println(table + " \t --> 最大编号为：" + map.get(table));
        }

        return map;
    }

    /**
     * 更新每个表格的auto_increment值
     * @param map
     */
    public static void resetTableIncrement(HashMap<String, Integer> map) {
        mysqlUtils mysql = new mysqlUtils();
        for (String table : map.keySet()) {
            String sql = "alter table " + table + " auto_increment = ?";
            List<Object> params = new ArrayList<>();
            params.add(map.get(table));
            try {
                mysql.addDeleteModify(sql, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mysql.closeconnection();
    }

}
