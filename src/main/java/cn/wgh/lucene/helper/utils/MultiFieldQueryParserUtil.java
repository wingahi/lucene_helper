package cn.wgh.lucene.helper.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import cn.wgh.lucene.helper.entity.AbstractLuceneDoc;
import cn.wgh.lucene.helper.entity.LuceneDocItem;
import cn.wgh.lucene.helper.entity.SearchBean;

public class MultiFieldQueryParserUtil<T extends AbstractLuceneDoc> extends LuceneIKUtil<T> {
	private String[] fields;
	private String keyword;
	
	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public MultiFieldQueryParserUtil(String indexFilePath, String[] fields,
			String keyword) {
		super(indexFilePath);
		this.fields = fields;
		this.keyword = keyword;
	}

	/**
	 * 
	 * Description：查询
	 * 
	 * @author dennisit@163.com Apr 3, 2013
	 * @param where
	 *            查询条件
	 * @param scoreDoc
	 *            分页时用
	 */
	public List<LuceneDocItem> search(T abstractLuceneDoc,boolean autoFill,Map<String,Object> mapFilter,SearchBean searchBean) {
		List<LuceneDocItem> result = new ArrayList<LuceneDocItem>();
		try {
			// 创建索引搜索器,且只读
			DirectoryReader indexReader = DirectoryReader.open(this.directory);
			this.indexSearcher = new IndexSearcher(indexReader);

			MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
					fields, this.analyzer);
			 this.query = queryParser.parse(keyword);
			 return super.search(abstractLuceneDoc, autoFill, mapFilter, searchBean);
		}catch(Exception e){
		}
		return null;
	}
}
