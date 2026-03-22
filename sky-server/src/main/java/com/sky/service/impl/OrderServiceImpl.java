package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private WeChatPayUtil weChatPayUtil;




    /**
     * 用户下单
     *
     * @param ordersSubmitDTO body
     * @return Vo
     */
    @Transactional
    public OrderSubmitVO orderSubmit(OrdersSubmitDTO ordersSubmitDTO) {

//        处理各种业务异常(地址簿为空,购物车为空)
        AddressBook addressBook = addressBookMapper.getAddressByAddressId(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
//            抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

//        拼接`地址
        String address = addressBook.getProvinceName()+addressBook.getCityName()+
                addressBook.getDistrictName()+addressBook.getDetail();


//        插叙当前用户车购物车数据
        ShoppingCart cart = new ShoppingCart();
        cart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        if (list == null || list.isEmpty()) {
//            抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }


//        向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));//当前时间戳
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress(address);
        orders.setUserId(BaseContext.getCurrentId());

        orderMapper.insert(orders);

//        向订单明细表插入n 条数据 批量插入
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart Cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(Cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetails);

//        清空当前用户购物车数据
        shoppingCartMapper.cleanByUserId(BaseContext.getCurrentId());

//        封装VO 返回结果
        OrderSubmitVO build = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderAmount(orders.getAmount())
                .orderNumber(orders.getNumber())
                .build();

        return build;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(),  //商户订单号
                new BigDecimal("0.01"), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的 openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }


    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }


    /**
     * 催单
     * @param id 订单 id
     */
//    TODO 催单
    public void reminder(Long id) {
    }


    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
//        面向切面的编程 AOP
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

//        分页查询订单
        Page<Orders> page =  orderMapper.pageQuery(ordersPageQueryDTO);
        List<Orders> orderList = page.getResult();
        List<OrderVO> list = new ArrayList<>();

        if (orderList != null && !orderList.isEmpty()) {

//            TODO 由于订单未支付超时自动取消。需要设置订单状态
//        查出来的订单不为空 提取订单Id列表
            List<Long> orderIds = orderList.stream()
                    .map(Orders::getId)
                    .collect(Collectors.toList());

//            批量查询所有订单明细
           List<OrderDetail> allDetails = orderDetailMapper.getByOrderIds(orderIds);

//            按照订单id 分组便于查找
            Map<Long, List<OrderDetail>> detailMap = allDetails.stream()
                    .collect(Collectors.groupingBy(OrderDetail::getOrderId));

//            组装 OrderVO
            for (Orders orders : orderList) {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                List<OrderDetail> orderDetails = detailMap.getOrDefault(orders.getId(), Collections.emptyList());

                orderVO.setOrderDetailList(orderDetails);
                list.add(orderVO);

            }
        }
        return new PageResult(page.getTotal(),list);
    }


    /**
     * 再来一单
     * @param id orderId
     */
    @Transactional
    public void repetition(Long id) {
//        重复相同的订单。将订单id查询出来更新数据再插入
        Orders orders =  orderMapper.getByOrderId(id);
        if (orders != null) {
//            重新设置订单数据
            orders.setId(null);
            orders.setOrderTime(LocalDateTime.now());
            orders.setPayStatus(Orders.UN_PAID);
            orders.setStatus(Orders.PENDING_PAYMENT);
            orders.setNumber(String.valueOf(System.currentTimeMillis()));//当前时间戳
            orderMapper.insert(orders);

//            向订单明细表插入数据
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderIds(Collections.singletonList(id));
            for (OrderDetail orderDetail : orderDetails) {
//                插入回显回来的主键id 数据
                orderDetail.setOrderId(orders.getId());
            }
            orderDetailMapper.insertBatch(orderDetails);
        }
    }


    /**
     * 取消订单
     * @param id orderId
     */
    @Transactional
    public void orderCancel(Long id) {
        Orders orders = orderMapper.getByOrderId(id);
        if (orders != null) {
            Integer status = orders.getStatus();
            if (Objects.equals(status, Orders.UN_PAID)) {
//                如果订单尚未支付，可以直接取消
//            更新订单状态
                orders.setStatus(Orders.CANCELLED);
//            修改订单
            } else if(Objects.equals(status, Orders.PAID)) {
//                如果订单已经支付，执行退款程序
//                TODO 执退款程序
                this.refund(id);
//                退款成功，设置订单状态
                orders.setStatus(Orders.CANCELLED);
                orders.setPayStatus(Orders.REFUND);
                orders.setCancelTime(LocalDateTime.now());
            }
            orderMapper.update(orders);
        }
    }


    /**
     * 订单退款
     * @param id orderId
     */
    public void refund(Long id) {

    }


    /**
     * 查看订单详情
     * @param id orderId
     * @return
     */
    public OrderVO getOrderDetail(Long id) {
        Orders orders = orderMapper.getByOrderId(id);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderIds(Collections.singletonList(id));
//        组装 VO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }
}
