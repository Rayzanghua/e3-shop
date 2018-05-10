package cn.e3mall.order.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.e3.common.utils.E3Result;
import cn.e3mall.cart.service.CartService;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

@Controller
public class OrderController {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@RequestMapping(value="/order/create", method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo, HttpServletRequest request) {
		// 1、接收表单提交的数据OrderInfo。
		// 2、补全用户信息。
		TbUser user = (TbUser) request.getAttribute("user");
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		// 3、调用Service创建订单。
		E3Result result = orderService.createOrder(orderInfo);
		//取订单号
		if(result.getStatus()==200){
			cartService.clearCartItem(user.getId());
		}
		// a)需要Service返回订单号
		request.setAttribute("orderId", result.getData());
		request.setAttribute("payment", orderInfo.getPayment());
		// b)当前日期加三天。
		// 4、返回逻辑视图展示成功页面
		return "success";
	}

	
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request){
		//取用户id
		TbUser user = (TbUser) request.getAttribute("user");
		//根据用户id取收货地址
		//使用静态数据
		//取支付方式列表
		//静态数据
		//根据用户id取购物车列表
		List<TbItem> cartList = cartService.getCartList(user.getId());
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
}
