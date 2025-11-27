package com.warmer.web.service;

/**
 * 问答服务接口
 *
 * 提供字典路径展示与自然语言问答功能。
 */
public interface QuestionService {

      /**
       * 展示词典路径（用于调试或诊断）
       */
	  void showDictPath();
	  /**
	   * 问答接口
	   * @param question 问题文本
	   * @return 答案
	   * @throws Exception 处理过程中可能抛出的异常
	   */
	  String answer(String question) throws Exception;
}
