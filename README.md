# lucene_helper
使用策略模式+摸板模式封装的lucene添加、查询、修改、删除索引一系列操作，方便使用的工具包。 一、支持搜索功能 基类：LuceneIKUtil 不同策略： BooleanQueryUtil
FuzzyQueryUtil MatchAllDocsQueryUtil MultiFieldQueryParserUtil PhraseQueryUtil PrefixQueryUtil TermRangeQueryUtil

二、详解 1、自定义注解，协助添加文档索引使用 @Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) public @interface IndexField { /** * 是否存储 * @return / public Field.Store store(); /* * 是否建索引或索引类型 * @return / public Field.Index index(); } 2、实体基类 public abstract class AbstractLuceneDoc implements LuceneDocItem { //为使不同数据类型可以使用该索引唯一支持，积累增加该表示已解决记录唯一性问题，易于维护管理 /* * 用作记录唯一性使用 / private String strId=""; /* * 数据类型 */ private Integer dataType=0;

@IndexField(index = Field.Index.NOT_ANALYZED, store = Field.Store.YES)
public String getStrId() {
	return strId;
}

public void setStrId(String strId) {
	this.strId = strId;
}

@IndexField(index = Field.Index.NOT_ANALYZED, store = Field.Store.YES)
public Integer getDataType() {
	return dataType;
}

public void setDataType(Integer dataType) {
	this.dataType = dataType;
}

public Document addDocument(LuceneDocItem luceneDocItem) {
	// TODO Auto-generated method stub
	return null;
}
public LuceneDocItem fillData(Document document, Analyzer analyzer,
		Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {
	// TODO Auto-generated method stub
	return null;
}
}

三、使用方法示例1： 首先使用到的实体必须继承与类AbstractLuceneDoc，如： public class Medicine extends AbstractLuceneDoc {

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
  //可自定义解析添加文档索引方法，不自定义的话，那么可以选择自动解析填充
	public Document addDocument(LuceneDocItem luceneDocItem) {
		// TODO Auto-generated method stub
		Document doc = new Document();
		// Field.Index.NO 表示不索引
		// Field.Index.ANALYZED 表示分词且索引
		// Field.Index.NOT_ANALYZED 表示不分词且索引
		//自动解析abstractLuceneDoc
		doc.add(new Field("id", String.valueOf(id), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field("name", name, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("function", function, Field.Store.YES,
				Field.Index.ANALYZED));
		return doc;
	}

  //可自定义查询数据后填充数据的方法，不自定义的话，那么可以选择自动解析填充
	public LuceneDocItem fillData(Document document, Analyzer analyzer,
			Highlighter highlighter) throws IOException, InvalidTokenOffsetsException {
		// TODO Auto-generated method stub
		Integer id = Integer.parseInt(document.get("id"));
		String name = document.get("name");
		String function = document.get("function");
		// float score = scDoc.score; //相似度

		String lighterName = highlighter.getBestFragment(analyzer,
				"name", name);
		if (null == lighterName) {
			lighterName = name;
		}

		String lighterFunciton = highlighter.getBestFragment(analyzer,
				"function", function);
		if (null == lighterFunciton) {
			lighterFunciton = function;
		}

		Medicine medicine = new Medicine();
		medicine.setId(id);
		medicine.setName(lighterName);
		medicine.setFunction(lighterFunciton);
		return medicine;
	}
}
然后使用相应的query工具类进行查询，如MultiFieldQueryParserUtil的使用：
String[] fields = { "name", "function" };
	LuceneIKUtil<Medicine> luceneProcess = new MultiFieldQueryParserUtil<Medicine>("F:/index",fields, "利咽");
	try {
		luceneProcess.createIndex(DataFactory.getInstance().getData());
	} catch (Exception e) {
		e.printStackTrace();
	}
	// 修改测试
	//luceneProcess.update(new Medicine(1,"银花 感冒颗粒12522jjjhh33","功能主治：银花感冒颗粒 ，头痛,清热，解表，利咽。"));
	//添加条线过滤器
	Map<String,Object> mapFilter = new HashMap<String,Object>(); 
	mapFilter.put("id", 1);
	//查询条件实体，具体看类SearchBean定义
	SearchBean searchBean = new SearchBean();
	//searchBean.setN(1);
	//排序
	SortField[] sortFields=new SortField[1];
	sortFields[0]=new SortField(null,Type.DOC,false);
	//searchBean.setShardHits(new TopDocs[]);
	searchBean.setSortFields(sortFields);
	//查询
	List<LuceneDocItem> list = luceneProcess.search(new Medicine(),true,mapFilter,searchBean);
	//打印
	for (int i = 0; i < list.size(); i++) {
		Medicine medicine = (Medicine)(list.get(i));
		System.out.println("(" + medicine.getId() + ")"
				+ medicine.getName() + "\t" + medicine.getFunction()+"\t"+medicine.getStrId()+"\t"+medicine.getDataType());
	}
	// 删除测试
	//luceneProcess.delete("Medicine:1");
