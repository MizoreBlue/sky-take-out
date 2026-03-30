package com.sky.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
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


    @Autowired
    private WebSocketServer webSocketServer;


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

//        拼接地址
        String address = addressBook.getProvinceName() + addressBook.getCityName() +
                addressBook.getDistrictName() + addressBook.getDetail();


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


//        通过 websocket向客户端推送消息 type orderId content
        HashMap map = new HashMap();
        map.put("type", 1);//1表示来的提醒，2表示客户催单
        map.put("orderId", ordersDB.getId());
        map.put("content", "订单号：" + outTradeNo);
        String message = JSONUtils.toJSONString(map);
//        群发信息 管理端
        webSocketServer.sendToAllClient(message);
    }


    /**
     * 催单
     *
     * @param id 订单 id
     */
    public void reminder(Long id) {
//        根据订单id查询订单
        Orders orders = orderMapper.getByOrderId(id);
        if (orders == null) {
//            抛出订单异常
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //        通过 websocket向客户端推送消息 type orderId content
        HashMap map = new HashMap();
        map.put("type", 2);//1表示来的提醒，2表示客户催单
        map.put("orderId", id);
        map.put("content", "订单号：" + orders.getNumber());
        String message = JSONUtils.toJSONString(map);
        webSocketServer.sendToAllClient(message);
    }


    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
//        面向切面的编程 AOP
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());

//        分页查询订单
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<Orders> orderList = page.getResult();
        List<OrderVO> list = new ArrayList<>();

        if (orderList != null && !orderList.isEmpty()) {

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
        return new PageResult(page.getTotal(), list);
    }


    /**
     * 再来一单
     *
     * @param id orderId
     */
    @Transactional
    public void repetition(Long id) {
//        重复相同的订单。将订单id查询出来更新数据再插入
        Orders orders = orderMapper.getByOrderId(id);
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
     *
     * @param id orderId
     */
    @Transactional
    public void orderCancel(Long id) throws Exception {
        Orders orders = orderMapper.getByOrderId(id);
        if (orders != null) {
            Integer status = orders.getStatus();
            if (Objects.equals(status, Orders.UN_PAID)) {
//                如果订单尚未支付，可以直接取消
//            更新订单状态
                orders.setStatus(Orders.CANCELLED);
//            修改订单
            } else if (Objects.equals(status, Orders.PAID)) {
//                如果订单已经支付，执行退款程序
//                TODO 执退款程序
//                String refund = this.refund(orders);
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
     *
     * @param orders entity
     */
    public String refund(Orders orders) throws Exception {
        return weChatPayUtil.refund(
                orders.getNumber(), //订单号
                String.valueOf(System.currentTimeMillis()), //使用当前时间戳来当作退款流水号
                orders.getAmount(),//退款金额
                orders.getAmount()//退款总金额
        );
    }


    /**
     * 查看订单详情
     *
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


    /**
     * 管理端取消订单
     *
     * @param ordersCancelDTO request body header 数据传输对象
     */
    @Transactional
    public void adminOrderCancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
//        根据主键id 查询订单
        Orders order = orderMapper.getByOrderId(ordersCancelDTO.getId());
        Integer status = order.getStatus();
        if (status == null || status.equals(Orders.COMPLETED) || status.equals(Orders.CANCELLED)) {
//            不存在该订单或者订单已完成、订单已取消，抛出异常
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

//        订单完成支付且 订单未完成配送 执行退款程序
        if (order.getPayStatus().equals(Orders.PAID)){
//            String refund = this.refund(order);
            order.setPayStatus(Orders.REFUND);
        }

        //        未支付订单，直接取消,

            order.setCancelReason(ordersCancelDTO.getCancelReason());
            order.setStatus(Orders.CANCELLED);
            order.setPayStatus(Orders.UN_PAID);
            order.setCancelTime(LocalDateTime.now());
//            更新订单
            orderMapper.update(order);

    }


    /**
     * 订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    public PageResult getOrderPage(OrdersPageQueryDTO ordersPageQueryDTO) {
//        TODO 封装订单菜品
//        分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        Page<Orders> page = orderMapper.pageQueryByDTO(ordersPageQueryDTO);
        List<Orders> records = page.getResult();
        long total = page.getTotal();
        return new PageResult(total, records);
    }


    /**
     * 各个状态的订单数量统计
     * @return 视图对象
     */
    public OrderStatisticsVO getOrderStatistics() {
//        使用sql 语句一步到位
        return orderMapper.getOrderStatistic();
    }
}
