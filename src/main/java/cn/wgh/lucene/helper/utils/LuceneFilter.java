package cn.wgh.lucene.helper.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;

public class LuceneFilter {
	@SuppressWarnings("deprecation")
	private List<Filter> filterList; 
    public LuceneFilter(){ 
        filterList = new ArrayList<Filter>(); 
    } 
    @SuppressWarnings("deprecation")
	public void addFilter(String Field,String Value){ 
        Term term=new Term(Field,Value);//添加term 
        QueryWrapperFilter filter=new QueryWrapperFilter(new TermQuery(term));//添加过滤器 
        filterList.add(filter);//加入List，可以增加多個过滤 
    } 
    @SuppressWarnings("deprecation")
	public Query getFilterQuery(Query query){ 
        for(int i=0;i<filterList.size();i++){ 
            //取出多個过滤器，在结果中再次定位结果 
            query = new FilteredQuery(query, filterList.get(i)); 
        } 
        return query; 
    }    
}
