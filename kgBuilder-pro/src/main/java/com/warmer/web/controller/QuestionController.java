package com.warmer.web.controller;

import com.warmer.base.util.R;
import com.warmer.web.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController extends BaseController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/ask")
    public R<String> ask(@RequestParam("q") String question) {
        try {
            String answer = questionService.answer(question);
            return R.success(answer);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("处理问题时发生错误: " + e.getMessage());
        }
    }
}
