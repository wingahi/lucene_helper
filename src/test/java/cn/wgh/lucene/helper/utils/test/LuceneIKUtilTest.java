package cn.wgh.lucene.helper.utils.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;

import cn.wgh.lucene.helper.entiry.test.DataFactory;
import cn.wgh.lucene.helper.entiry.test.Medicine;
import cn.wgh.lucene.helper.entity.BooleanQueryItem;
import cn.wgh.lucene.helper.entity.LuceneDocItem;
import cn.wgh.lucene.helper.entity.SearchBean;
import cn.wgh.lucene.helper.utils.BooleanQueryUtil;
import cn.wgh.lucene.helper.utils.LuceneIKUtil;
import cn.wgh.lucene.helper.utils.MultiFieldQueryParserUtil;
import cn.wgh.lucene.helper.utils.TermRangeQueryUtil;

public class LuceneIKUtilTest {


	public static void main(String[] args) {
		String[] fields = { "name", "function" };
		//LuceneIKUtil<Medicine> luceneProcess = new MultiFieldQueryParserUtil<Medicine>("F:/index",fields, "利咽");
		//List<BooleanQueryItem> queryItems =new ArrayList<BooleanQueryItem>();
		//LuceneIKUtil<Medicine> luceneProcess = new BooleanQueryUtil<Medicine>("F:/index", queryItems);
		LuceneIKUtil<Medicine> luceneProcess = new TermRangeQueryUtil<Medicine>("I:/index", "id", "1", "4");
		try {
			luceneProcess.createIndex(DataFactory.getInstance().getData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 修改测试
		//luceneProcess.update(new Medicine(1,"银花 感冒颗粒12522jjjhh33","功能主治：银花感冒颗粒 ，头痛,清热，解表，利咽。"));

		// 查询测试
		
		
		Map<String,Object> mapFilter = new HashMap<String,Object>(); 
		//mapFilter.put("id", 1);
		SearchBean searchBean = new SearchBean();
		//searchBean.setN(1);
		SortField[] sortFields=new SortField[1];
		sortFields[0]=new SortField(null,Type.DOC,false);
		//searchBean.setShardHits(new TopDocs[]);
		searchBean.setSortFields(sortFields);
		List<LuceneDocItem> list = luceneProcess.search(new Medicine(),true,mapFilter,searchBean);
		for (int i = 0; i < list.size(); i++) {
			Medicine medicine = (Medicine)(list.get(i));
			System.out.println("(" + medicine.getId() + ")"
					+ medicine.getName() + "\t" + medicine.getFunction()+"\t"+medicine.getStrId()+"\t"+medicine.getDataType());
		}
		// 删除测试
		//luceneProcess.delete("Medicine:1");
	}
	
	
	
}
