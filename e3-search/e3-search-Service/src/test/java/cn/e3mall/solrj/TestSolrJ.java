package cn.e3mall.solrj;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {

	@Test
	public void test1(){
		System.out.println("1");
	}
	@Test
	public void addDocument() throws Exception {
		// 第一步：把solrJ的jar包添加到工程中。
		// 第二步：创建一个SolrServer，使用HttpSolrServer创建对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8082/solr");
		// 第三步：创建一个文档对象SolrInputDocument对象。
		SolrInputDocument document = new SolrInputDocument();
		// 第四步：向文档中添加域。必须有id域，域的名称必须在schema.xml中定义。
		document.addField("id", "test001");
		document.addField("item_title", "测试商品");
		document.addField("item_price", "199");
		// 第五步：把文档添加到索引库中。
		solrServer.add(document);
		// 第六步：提交。
		solrServer.commit();
	}
	
	@Test
	public void deleteDocumentById() throws Exception {
		// 第一步：创建一个SolrServer对象。
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8082/solr");
		// 第二步：调用SolrServer对象的根据id删除的方法。
		solrServer.deleteById("1");
		// 第三步：提交。
		solrServer.commit();
	}

	@Test
	public void deleteDocumentByQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8082/solr");
		solrServer.deleteByQuery("title:change.me");
		solrServer.commit();
	}
	
	//使用solrJ实现查询
		@Test
		public void queryDocument() throws Exception {
			//创建一个SolrServer对象
			SolrServer solrServer = new HttpSolrServer("http://192.168.25.128:8082/solr");
			//创建一个查询对象，可以参考solr的后台的查询功能设置条件
			SolrQuery query = new SolrQuery();
			//设置查询条件
//			query.setQuery("阿尔卡特");
			query.set("q","*:*");
			//设置分页条件
			query.setStart(1);
			query.setRows(2);
			//开启高亮
			query.setHighlight(true);
			query.addHighlightField("item_title");
			query.setHighlightSimplePre("<em>");
			query.setHighlightSimplePost("</em>");
			//设置默认搜索域
			query.set("df", "item_title");
			//执行查询，得到一个QueryResponse对象。
			QueryResponse queryResponse = solrServer.query(query);
			//取查询结果总记录数
			SolrDocumentList solrDocumentList = queryResponse.getResults();
			System.out.println("查询结果总记录数：" + solrDocumentList.getNumFound());
			//取查询结果
			Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
			for (SolrDocument solrDocument : solrDocumentList) {
				System.out.println(solrDocument.get("id"));
				//取高亮后的结果
				List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
				String title= "";
				if (list != null && list.size() > 0) {
					//取高亮后的结果
					title = list.get(0);
				} else {
					title = (String) solrDocument.get("item_title");
				}
				System.out.println(title);
				System.out.println(solrDocument.get("item_sell_point"));
				System.out.println(solrDocument.get("item_price"));
				System.out.println(solrDocument.get("item_image"));
				System.out.println(solrDocument.get("item_category_name"));
			}
			
		}


}
