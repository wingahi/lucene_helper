package cn.wgh.lucene.helper.utils;

import java.util.List;
import java.util.Map;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;

import cn.wgh.lucene.helper.entity.AbstractLuceneDoc;
import cn.wgh.lucene.helper.entity.LuceneDocItem;

public class PhraseQueryUtil<T extends AbstractLuceneDoc> extends
		LuceneIKUtil<T> {

	private List<String> phrases;
	private String fieldName;
	
	public PhraseQueryUtil(String indexFilePath, List<String> phrases,
			String fieldName) {
		super(indexFilePath);
		this.phrases = phrases;
		this.fieldName = fieldName;
	}

	public List<String> getPhrases() {
		return phrases;
	}

	public void setPhrases(List<String> phrases) {
		this.phrases = phrases;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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
			PhraseQuery query = new PhraseQuery();
			query.setSlop(0);

			for (String word : phrases) {
				query.add(new Term(fieldName, word));
			}
			this.query = query;
			// TODO Auto-generated method stub
			return super.search(abstractLuceneDoc, autoFill, mapFilter,
					searchBean);
		} catch (Exception e) {
		}
		return null;
	}
}
