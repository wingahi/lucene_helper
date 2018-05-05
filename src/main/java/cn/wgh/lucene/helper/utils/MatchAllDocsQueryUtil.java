package cn.wgh.lucene.helper.utils;

import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Weight;

import cn.wgh.lucene.helper.entity.AbstractLuceneDoc;
import cn.wgh.lucene.helper.entity.LuceneDocItem;

public class MatchAllDocsQueryUtil<T extends AbstractLuceneDoc> extends
		LuceneIKUtil<T> {

	private String searchQuery;

	public MatchAllDocsQueryUtil(String indexFilePath, String searchQuery) {
		super(indexFilePath);
		this.searchQuery = searchQuery;
	}

	public String getSearchQuery() {
		return searchQuery;
	}

	public void setSearchQuery(String searchQuery) {
		this.searchQuery = searchQuery;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<LuceneDocItem> search(T abstractLuceneDoc, boolean autoFill,
			Map<String, Object> mapFilter,
			cn.wgh.lucene.helper.entity.SearchBean searchBean) {
		try {
			// 创建索引搜索器,且只读
			DirectoryReader indexReader = DirectoryReader.open(this.directory);
			this.indexSearcher = new IndexSearcher(indexReader);
			// create the term query object
			Query query = new MatchAllDocsQuery();
			Weight weight = query.createWeight(this.indexSearcher, true);
			this.query = query;
			// TODO Auto-generated method stub
			return super.search(abstractLuceneDoc, autoFill, mapFilter,
					searchBean);
		} catch (Exception e) {
		}
		return null;
	}
}
