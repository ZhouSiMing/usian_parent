package com.usian.mq;


import com.usian.mapper.LocalMessageMapper;
import com.usian.pojo.LocalMessage;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * 发送消息
 * 消息确认成功返回后修改local_message
 * */
@Component
public class MQSender implements ReturnCallback, ConfirmCallback {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private LocalMessageMapper localMessageMapper;


    /*
    * 消息发送失败时调用
    * */

    @Override
    public void returnedMessage(Message message, int replyCode, String s, String exchange, String routingKey) {
        System.out.println("return message:"+ message.getBody().toString()+ ",exchange:" + exchange + ",routingKey:" + routingKey);
    }

    /*
    * 下游服务消息确认成功返回后调用
    * */

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData.getId();
        if (ack){
            //修改本地消息表的状态
            LocalMessage localMessage = new LocalMessage();
            localMessage.setTxNo(id);
            localMessage.setState(1);
            localMessageMapper.updateByPrimaryKeySelective(localMessage);
        }
    }

    /*
    * 发送消息
    * */
    public void sendMsg(LocalMessage localMessage) {

        RabbitTemplate rabbitTemplate = (RabbitTemplate) this.amqpTemplate;
        rabbitTemplate.setMandatory(true);//开启消息发送失败回退
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setConfirmCallback(this);
        //消息确认对象，消息id:用户消息确认成功返回后修改本地消息表的状态--->消息发送端的confim方法
        CorrelationData correlationData = new CorrelationData(localMessage.getTxNo());
        rabbitTemplate.convertAndSend("order_exchange","order.add", JsonUtils.objectToJson(localMessage),correlationData);
    }
}
