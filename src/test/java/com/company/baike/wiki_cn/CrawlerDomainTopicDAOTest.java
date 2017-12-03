package com.company.baike.wiki_cn;

import com.company.baike.wiki_cn.domain.LayerRelation;
import junit.framework.TestCase;

import java.util.*;

/**
 *
 * Created by yuanhao on 2017/10/18.
 */
public class CrawlerDomainTopicDAOTest extends TestCase {

    public void testRemoveDuplicateLayerRelation() throws Exception {
//        List<LayerRelation> layerRelationList = new ArrayList<LayerRelation>();
//        LayerRelation layerRelation1 = new LayerRelation("a", 1, "b", 2, "c");
//        LayerRelation layerRelation2 = new LayerRelation("a", 1, "b", 2, "c");
//        LayerRelation layerRelation3 = new LayerRelation("a", 1, "b", 2, "d");
//        LayerRelation layerRelation4 = new LayerRelation("a", 1, "b", 2, "d");
//        LayerRelation layerRelation5 = new LayerRelation("a", 1, "b", 2, "d");
//        System.out.println(layerRelation1.equals(layerRelation2));
//        System.out.println(layerRelation1.equals(layerRelation3));
//        layerRelationList.add(layerRelation1);
//        layerRelationList.add(layerRelation2);
//        layerRelationList.add(layerRelation3);
//        layerRelationList.add(layerRelation4);
//        layerRelationList.add(layerRelation5);
//        List<LayerRelation> result = CrawlerDomainTopicDAO.removeDuplicateLayerRelation(layerRelationList);
//        for (int i = 0; i < result.size(); i++) {
//            System.out.println(result.get(i).toString());
//        }
//
//        System.out.println("-------------------------------------------");
//        Set<LayerRelation> layerRelationSet = new LinkedHashSet<LayerRelation>(layerRelationList);
//        for (LayerRelation layerRelation : layerRelationSet) {
//            System.out.println(layerRelation.toString());
//        }

        List<LayerRelation> layerRelationList = MysqlReadWriteDAO.getDomainLayerRelation("农业史");
        for (int i = 0; i < layerRelationList.size(); i++) {
            System.out.println(layerRelationList.get(i).toString()


            );
        }
        // 从 domain_layer_relation 删除重复主题关系保存到 domain_layer_relation2
        Set<LayerRelation> layerRelationSet = new LinkedHashSet<LayerRelation>(layerRelationList);
//        MysqlReadWriteDAO.storeDomainLayerRelation(layerRelationSet);
    }

}