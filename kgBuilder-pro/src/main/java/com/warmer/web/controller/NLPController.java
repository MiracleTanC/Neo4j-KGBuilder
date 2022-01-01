package com.warmer.web.controller;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kg")
public class NLPController {
	
	@RequestMapping("/popse")
	public String popse(Model model) {
		return "kg/popse";
	}
	/*
	 * 关键字与其词性的map键值对集合 == 句子抽象
	 */
	@RequestMapping("/getnlpword")
	@ResponseBody
	public Map<String, Object> query(@RequestParam(value = "q") String question) throws Exception {
		Map<String, Object> result=new HashMap<String, Object>(); 
		//分词
		List<Map<String, Object>> resultMap=queryAbstract(question);
		result.put("code", 200);
		result.put("data", resultMap);
		return result;
	}


   
    public  List<Map<String, Object>> queryAbstract(String querySentence) {
		// 句子抽象化
		Segment segment = HanLP.newSegment().enableOffset(true).enableCustomDictionary(true);
		List<Term> terms = segment.seg(querySentence);
		List<Map<String, Object>> sens=new ArrayList<Map<String,Object>>();
		for (Term term : terms) {
			HashMap<String, Object> abstractMap = new HashMap<String, Object>();
			abstractMap.put("word", term.word);
			abstractMap.put("pos", term.offset);
			abstractMap.put("nature", term.nature.toString());
			sens.add(abstractMap);
		}
		return sens;
	}

}
