package com.company.baike.wiki_cn.ranktext;
/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2017年10月18日 下午7:43:47 
 */
public class Term {
	private int termID;

    private String termName;

    private String termText;


    public int getTermID() {
        return termID;
    }

    public void setTermID(int termID) {
        this.termID = termID;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermText() {
        return termText;
    }

    public void setTermText(String termText) {
        this.termText = termText;
    }
}
