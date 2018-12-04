package com.warmer.kgmaker;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class HandLPTest {
	@Test
	public void TestA(){
		String lineStr = "明天虽然会下雨，但是我还是会看周杰伦的演唱会。";
		String lineStr2 = "明天虽然会好热，但是我还是会看周杰伦的演唱会。";
		String lineStr3 = "明天虽然会非常热，但是我还是会看周杰伦的演唱会。";
		try{
			Segment segment = HanLP.newSegment();
		    segment.enableCustomDictionary(true);
		    /**
		     * 自定义分词+词性
		     */
		    //CustomDictionary.add("好热","ng 0");
		    CustomDictionary.add("非常热","ng 0");
			List<Term> seg = segment.seg(lineStr);
			for (Term term : seg) {
				System.out.println(term.toString());
			}
			List<Term> seg2 = segment.seg(lineStr2);
			for (Term term : seg2) {
				System.out.println(term.toString());
			}
			List<Term> seg3 = segment.seg(lineStr3);
			for (Term term : seg3) {
				System.out.println(term.toString());
			}
		}catch(Exception ex){
			System.out.println(ex.getClass()+","+ex.getMessage());
		}		
	}
}
