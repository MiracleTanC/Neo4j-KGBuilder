package com.warmer.kgmaker.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.classification.classifiers.IClassifier;
import com.hankcs.hanlp.classification.classifiers.NaiveBayesClassifier;
import com.hankcs.hanlp.classification.models.NaiveBayesModel;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.warmer.kgmaker.util.TestUtility;

@RestController
@RequestMapping("/kg")
public class QuestionController {
	
	/**
     * 搜狗文本分类语料库5个类目，每个类目下1000篇文章，共计5000篇文章
     */
    public static final String CORPUS_FOLDER = TestUtility.ensureTestData("搜狗文本分类语料库迷你版", "http://hanlp.linrunsoft.com/release/corpus/sogou-text-classification-corpus-mini.zip");
    /**
     * 模型保存路径
     */
    public static final String MODEL_PATH = "data/test/classification-model.ser";
    /**
	 * 关键字与其词性的map键值对集合 == 句子抽象
	 */
	Map<String, String> abstractMap;
	@RequestMapping("/query")
	public HashMap<String, String> query(@RequestParam(value = "question") String question) throws Exception {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		 IClassifier classifier = new NaiveBayesClassifier(trainOrLoadModel());
	     predict(classifier, question);
	     //分词
	     String words=queryAbstract(question);
	     resultMap.put("domain", classifier.classify(question));
	     resultMap.put("words", words);
		return resultMap;
	}


    private static void predict(IClassifier classifier, String text)
    {
        System.out.printf("《%s》 属于分类 【%s】\n", text, classifier.classify(text));
    }

    private static NaiveBayesModel trainOrLoadModel() throws IOException
    {
        NaiveBayesModel model = (NaiveBayesModel) IOUtil.readObjectFrom(MODEL_PATH);
        if (model != null) return model;

        File corpusFolder = new File(CORPUS_FOLDER);
        if (!corpusFolder.exists() || !corpusFolder.isDirectory())
        {
            System.err.println("没有文本分类语料，请阅读IClassifier.train(java.lang.String)中定义的语料格式与语料下载：" +
                                   "https://github.com/hankcs/HanLP/wiki/%E6%96%87%E6%9C%AC%E5%88%86%E7%B1%BB%E4%B8%8E%E6%83%85%E6%84%9F%E5%88%86%E6%9E%90");
            System.exit(1);
        }

        IClassifier classifier = new NaiveBayesClassifier(); // 创建分类器，更高级的功能请参考IClassifier的接口定义
        classifier.train(CORPUS_FOLDER);                     // 训练后的模型支持持久化，下次就不必训练了
        model = (NaiveBayesModel) classifier.getModel();
        IOUtil.saveObjectTo(model, MODEL_PATH);
        return model;
    }
    public  String queryAbstract(String querySentence) {
		
		// 句子抽象化
		Segment segment = HanLP.newSegment().enableCustomDictionary(true);							
		List<Term> terms = segment.seg(querySentence);
		String abstractQuery = "";
		abstractMap = new HashMap<String, String>();
		int nrCount = 0; //nr 人名词性这个 词语出现的频率
		for (Term term : terms) {
			String word = term.word;
			String termStr = term.toString();
			System.out.println(termStr);
			if (termStr.contains("nm")) {        //nm 电影名
				abstractQuery += "nm ";
				abstractMap.put("nm", word);
			} else if (termStr.contains("nr") && nrCount == 0) { //nr 人名
				abstractQuery += "nnt ";
				abstractMap.put("nnt", word);
				nrCount++;
			}else if (termStr.contains("nr") && nrCount == 1) { //nr 人名 再出现一次，改成nnr
				abstractQuery += "nnr ";
				abstractMap.put("nnr", word);
				nrCount++;
			}else if (termStr.contains("x")) {  //x  评分
				abstractQuery += "x ";
				abstractMap.put("x", word);
			} else if (termStr.contains("ng")) { //ng 类型
				abstractQuery += "ng ";
				abstractMap.put("ng", word);
			} 	
			else {
				abstractQuery += word + " ";
			}
		}
		System.out.println("========HanLP分词结束========");
		return abstractQuery;
	}

	public  String queryExtenstion(String queryPattern) {
		// 句子还原
		Set<String> set = abstractMap.keySet();
		for (String key : set) {
			/**
			 * 如果句子模板中含有抽象的词性
			 */
			if (queryPattern.contains(key)) {
				
				/**
				 * 则替换抽象词性为具体的值 
				 */
				String value = abstractMap.get(key);
				queryPattern = queryPattern.replace(key, value);
			}
		}
		String extendedQuery = queryPattern;
		/**
		 * 当前句子处理完，抽象map清空释放空间并置空，等待下一个句子的处理
		 */
		abstractMap.clear();
		abstractMap = null;
		return extendedQuery;
	}
    
}
