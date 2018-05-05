package cn.wgh.lucene.helper.utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import cn.wgh.lucene.helper.entity.AbstractLuceneDoc;
import cn.wgh.lucene.helper.entity.EntityValueMapper;
import cn.wgh.lucene.helper.entity.LuceneDocItem;
import cn.wgh.lucene.helper.entity.SearchBean;

/**
 * 
 * LuenceProcess.java
 * 
 * @version ： 1.1
 * 
 * @author ： 苏若年 <a href="mailto:DennisIT@163.com">发送邮件</a>
 * 
 * @since ： 1.0 创建时间: Apr 3, 2013 11:48:11 AM
 * 
 *        TODO : Luence中使用IK分词器
 * 
 */

public class LuceneIKUtil<T extends AbstractLuceneDoc> {

	protected Directory directory;
	protected Analyzer analyzer;
	protected IndexSearcher indexSearcher = null;
	protected Query query = null;
	/**
	 * 带参数构造,参数用来指定索引文件目录
	 * 
	 * @param indexFilePath
	 */
	protected LuceneIKUtil(String indexFilePath) {
		try {

			directory = FSDirectory.open(Paths.get(indexFilePath));
			analyzer = new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);// 5X(JcsegTaskConfig.COMPLEX_MODE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 默认构造,使用系统默认的路径作为索引
	 */
	public LuceneIKUtil() {
		this("/luence/index");
	}

	/**
	 * 创建索引 Description：
	 * 
	 * @author dennisit@163.com Apr 3, 2013
	 * @throws Exception
	 */
	public void createIndex(List<T> luceneDocItems) throws Exception {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);// (Version.LUCENE_CURRENT,analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		indexWriter.deleteAll();
		for (int i = 0; i < luceneDocItems.size(); i++) {
			T luceneDocItem = luceneDocItems.get(i);
			Document document = addDocument(luceneDocItem);
			indexWriter.addDocument(document);
		}

		indexWriter.close();
	}

	/**
	 * 
	 * Description：
	 * 
	 * @author dennisit@163.com Apr 3, 2013
	 * @param id
	 * @param title
	 * @param content
	 * @return
	 */
	public Document addDocument(T abstractLuceneDoc) {
		Document doc = new Document();
		// Field.Index.NO 表示不索引
		// Field.Index.ANALYZED 表示分词且索引
		// Field.Index.NOT_ANALYZED 表示不分词且索引
		//自动解析abstractLuceneDoc
		Map<String,EntityValueMapper> fieldMap = EntityHelper.analyzeEntityValue(abstractLuceneDoc);
		EntityValueMapper valueMapper = null;
		for (Entry<String, EntityValueMapper> entry : fieldMap.entrySet()) {
			valueMapper = entry.getValue();
			doc.add(new Field(entry.getKey(), String.valueOf(valueMapper.getValue()), valueMapper.getStore(),
					valueMapper.getIndex()));
		}
		return doc;
	}

	/**
	 * 
	 * Description： 更新索引
	 * 
	 * @author dennisit@163.com Apr 3, 2013
	 * @param id
	 * @param title
	 * @param content
	 */
	public void update(AbstractLuceneDoc abstractLuceneDoc) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					analyzer);
			IndexWriter indexWriter = new IndexWriter(directory,
					indexWriterConfig);
			Document document = abstractLuceneDoc.addDocument(abstractLuceneDoc);
			Term term = new Term("strId", abstractLuceneDoc.getStrId());
			indexWriter.updateDocument(term, document);
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Description：按照ID进行索引
	 * 
	 * @author dennisit@163.com Apr 3, 2013
	 * @param id
	 */
	public void delete(String strId) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					analyzer);
			IndexWriter indexWriter = new IndexWriter(directory,
					indexWriterConfig);
			Term term = new Term("strId",strId);
			indexWriter.deleteDocuments(term);
			indexWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<LuceneDocItem> search(T abstractLuceneDoc, boolean autoFill,
			Map<String, Object> mapFilter, SearchBean searchBean){
		List<LuceneDocItem> result = new ArrayList<LuceneDocItem>();
		try{
			if(mapFilter!=null){
				LuceneFilter luceneFilter = new LuceneFilter(); 
				Set<String> keySet = mapFilter.keySet(); 
				for (String key : keySet) {
					luceneFilter.addFilter(key, mapFilter.get(key).toString()); 
				}
				query = luceneFilter.getFilterQuery(query);//结果过滤 
			}
			TopDocs topDocs = null;
			// 返回前number条记录
			if(searchBean!=null){
				if(searchBean.getSortFields()!=null && searchBean.getSortFields().length>0){
					topDocs = indexSearcher.search(query, searchBean.getN(),new Sort(searchBean.getSortFields()));//
				}else {
					topDocs = indexSearcher.search(query, searchBean.getN());//
				}
			}else {
				topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
			}
			
			// 信息展示
			int totalCount = topDocs.totalHits;
			System.out.println("共检索出 " + totalCount + " 条记录");
	
			// 高亮显示
			/*
			 * 创建高亮器,使搜索的结果高亮显示 SimpleHTMLFormatter：用来控制你要加亮的关键字的高亮方式 此类有2个构造方法
			 * 1：SimpleHTMLFormatter()默认的构造方法.加亮方式：<B>关键字</B>
			 * 2：SimpleHTMLFormatter(String preTag, String
			 * postTag).加亮方式：preTag关键字postTag
			 */
			Formatter formatter = new SimpleHTMLFormatter("<font color='red'>",
					"</font>");
	
			/*
			 * QueryScorer QueryScorer
			 * 是内置的计分器。计分器的工作首先是将片段排序。QueryScorer使用的项是从用户输入的查询中得到的；
			 * 它会从原始输入的单词、词组和布尔查询中提取项，并且基于相应的加权因子（boost factor）给它们加权。
			 * 为了便于QueryScoere使用，还必须对查询的原始形式进行重写。 比如，带通配符查询、模糊查询、前缀查询以及范围查询
			 * 等，都被重写为BoolenaQuery中所使用的项。
			 * 在将Query实例传递到QueryScorer之前，可以调用Query.rewrite
			 * (IndexReader)方法来重写Query对象
			 */
			Scorer fragmentScorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
			Fragmenter fragmenter = new SimpleFragmenter(100);
			/*
			 * Highlighter利用Fragmenter将原始文本分割成多个片段。
			 * 内置的SimpleFragmenter将原始文本分割成相同大小的片段，片段默认的大小为100个字符。这个大小是可控制的。
			 */
			highlighter.setTextFragmenter(fragmenter);
	
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			Map<String,Class> fieldMap = null;
		
			Map<String,Object> mapItem = null;
			if(autoFill){
				fieldMap = EntityHelper.analyzeEntity(abstractLuceneDoc);
			}
			int start=0,end = scoreDocs.length-1;
			if(searchBean.getStart()!=null){
				start = searchBean.getStart();
			}
			if(searchBean.getEnd()!=null){
				end = searchBean.getEnd();
			}
			for (;start<=end;start++) {
				ScoreDoc scDoc = scoreDocs[start];
				Document document = indexSearcher.doc(scDoc.doc);
				LuceneDocItem item = null;
				if(fieldMap!=null&&fieldMap.size()>0){
					mapItem = new HashMap<String, Object>();
					for (Entry<String, Class> fieldItem : fieldMap.entrySet()) {
						String value = document.get(fieldItem.getKey());
						if(value!=null && !value.isEmpty()){
							String lighterFunciton = highlighter.getBestFragment(analyzer,fieldItem.getKey(), value);
							if (null != lighterFunciton) {
								value = lighterFunciton;
							}
						}
						mapItem.put(fieldItem.getKey(), value);
					}
					item = (T) abstractLuceneDoc.getClass().newInstance();
					EntityHelper.transMap2Bean2(mapItem,  item);
				}else {
					item = abstractLuceneDoc.fillData(document,analyzer,highlighter);
					if(item==null){
						throw new Exception("fillData return is null");
					}
				}
				if(item!=null){
					result.add(item);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexSearcher.getIndexReader().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		return result;
	}
}
