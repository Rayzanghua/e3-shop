package cn.e3mall.cart.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3.common.jedis.JedisClient;
import cn.e3.common.utils.E3Result;
import cn.e3.common.utils.JsonUtils;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_CART_PRE}")
	private String REDIS_CART_PRE;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public E3Result addCart(long userId, long itemId,int num) {
		// TODO Auto-generated method stub  如果库存不够应该判断
		//向redis中添加购物车
		//数据类型是hash key：用户id field：商品id value：商品信息
		//判断商品是否存在
		Boolean hexists = jedisClient.hexists(REDIS_CART_PRE+":"+userId,itemId+"");
		//如果存在数量相加
		if(hexists){
			String json = jedisClient.hget(REDIS_CART_PRE+":"+userId, itemId+"");
			//把json转成TbItem
			TbItem item=JsonUtils.jsonToPojo(json, TbItem.class);
			item.setNum(item.getNum()+num);
			//写回redis
			jedisClient.hset(REDIS_CART_PRE+":"+userId, itemId+"",JsonUtils.objectToJson(item));
			return E3Result.ok();
		}
		//如果不存在，根据商品id取商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		//设置购物车数量
		item.setNum(num);
		//取一张图片
		String image=item.getImage();
		if(StringUtils.isNoneBlank(image)){
			item.setImage(image.split(",")[0]);
		}
		//返回成功
		jedisClient.hset(REDIS_CART_PRE+":"+userId, itemId+"",JsonUtils.objectToJson(item));
		return E3Result.ok();
	}

	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
		// 现有cookie中的商品列表，和redis中的商品列表，
		//通过遍历cookie中的列表，如果在redis中存在，则数量加1
		//不存在redis中添加
		for(TbItem tbItem:itemList){
			addCart(userId, tbItem.getId(), tbItem.getNum());
		}
		//返回成功
		return E3Result.ok();
	}

	@Override
	public List<TbItem> getCartList(long userId) {
		// 根据用户id查询购物车列表
		List<String> jsonList = jedisClient.hvals(REDIS_CART_PRE+":"+userId);
		List<TbItem> itemList=new ArrayList<>();
		for(String string:jsonList){
			TbItem item=JsonUtils.jsonToPojo(string, TbItem.class);
			//添加到列表
			itemList.add(item);
		}
		return itemList;
	}

	@Override
	public E3Result updateCartNum(long userId, long itemId, int num) {
		String json=jedisClient.hget(REDIS_CART_PRE+":"+userId, itemId+"");
		
		TbItem tbItem=JsonUtils.jsonToPojo(json, TbItem.class);
		tbItem.setNum(num);
		jedisClient.hset(REDIS_CART_PRE+":"+userId, itemId+"",JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}

	@Override
	public E3Result deleteCartItem(long userId, long itemId) {
		// 删除购物车商品
		jedisClient.hdel(REDIS_CART_PRE+":"+userId, itemId+"");
		return E3Result.ok();
	}

	@Override
	public E3Result clearCartItem(long userId) {
		// 删除购物车信息
		jedisClient.del(REDIS_CART_PRE+":"+userId);
		return E3Result.ok();
	}

}
