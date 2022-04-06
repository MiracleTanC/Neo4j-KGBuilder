package com.warmer.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface IWorkFlowDirectorService {
     /**
      * 导演方法。
      * 从config json串中提取组件和连接信息
      * 然后依次执行执行流程
      * 1. 先执行开始节点组件，
      * 2. 根据last pre nodes是否执行完毕，调用后置组件服务
      *
      * @param dataAnalyseId 数据分析配置id
      */
     void direct(int dataAnalyseId) throws JsonProcessingException;
}
