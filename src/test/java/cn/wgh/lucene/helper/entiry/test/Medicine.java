package cn.wgh.lucene.helper.entiry.test;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.w3c.dom.views.AbstractView;

import cn.wgh.lucene.helper.annotation.IndexField;
import cn.wgh.lucene.helper.entity.AbstractLuceneDoc;
import cn.wgh.lucene.helper.entity.LuceneDocItem;

/**
 * 
 *
 *  @version ： 1.0
 *  
 *  @author  ： 苏若年              <a href="mailto:DennisIT@163.com">发送邮件</a>
 *    
 *  @since   ： 1.0        创建时间:    2013-4-7    下午01:52:49
 *     
 *  @function： TODO        
 *
 */
public class Medicine extends AbstractLuceneDoc {

    private Integer id;
    private String name;
    private String function;
    
    
    public Medicine() {
    	
    }
    
    
    public Medicine(Integer id, String name, String function) {
        super();
        this.setStrId("Medicine:"+id);
        this.id = id;
        this.name = name;
        this.function = function;
    }

    //getter and setter()    
    @IndexField(index = Field.Index.NOT_ANALYZED, store = Field.Store.YES)
    public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
		this.setStrId("Medicine:"+id);
	}

	@IndexField(index = Field.Index.ANALYZED, store = Field.Store.YES)
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	@IndexField(index = Field.Index.ANALYZED, store = Field.Store.YES)
	public String getFunction() {
		return function;
	}


	public void setFunction(String function) {
		this.function = function;
	}


	public String toString(){
        return this.id + "," +this.name + "," + this.function;
    }

//	public Document addDocument(LuceneDocItem luceneDocItem) {
//		// TODO Auto-generated method stub
//		Document doc = new Document();
//		// Field.Index.NO 表示不索引
//		// Field.Index.ANALYZED 表示分词且索引
//		// Field.Index.NOT_ANALYZED 表示不分词且索引
//		//自动解析abstractLuceneDoc
//		doc.add(new Field("id", String.valueOf(id), Field.Store.YES,
//				Field.Index.NOT_ANALYZED));
//		doc.add(new Field("name", name, Field.Store.YES, Field.Index.ANALYZED));
//		doc.add(new Field("function", function, Field.Store.YES,
//				Field.Index.ANALYZED));
//		return doc;
//	}
//
//
//	public LuceneDocItem fillData(Document document, Analyzer analyzer,
//			Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {
//		// TODO Auto-generated method stub
//		Integer id = Integer.parseInt(document.get("id"));
//		String name = document.get("name");
//		String function = document.get("function");
//		// float score = scDoc.score; //相似度
//
//		String lighterName = highlighter.getBestFragment(analyzer,
//				"name", name);
//		if (null == lighterName) {
//			lighterName = name;
//		}
//
//		String lighterFunciton = highlighter.getBestFragment(analyzer,
//				"function", function);
//		if (null == lighterFunciton) {
//			lighterFunciton = function;
//		}
//
//		Medicine medicine = new Medicine();
//		medicine.setId(id);
//		medicine.setName(lighterName);
//		medicine.setFunction(lighterFunciton);
//		return medicine;
//	}
	
}