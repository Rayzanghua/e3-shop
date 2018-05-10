package cn.e3mall.search.message;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3.common.pojo.SearchItem;
import cn.e3mall.search.mapper.ItemMapper;

public class ItemAddMessageListener implements MessageListener {
	
	@Autowired
	private ItemMapper itemMapper;
	
	@Autowired
	private SolrServer solrServer;

	@Override
	public void onMessage(Message message) {
		try {
			//从信息中取商品ID
			TextMessage textMessage = (TextMessage) message;
			String text=textMessage.getText();
			Long itemId=new Long(text);
			//等待事务提交
			Thread.sleep(1000);
			//取商品id
			SearchItem searchItem=itemMapper.getItemById(itemId);
			//向索引库添加文档
			SolrInputDocument document=new SolrInputDocument();
			//向文档中添加域
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			//文档写入索引库
			solrServer.add(document);
			//提交
			solrServer.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

