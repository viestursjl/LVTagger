import static org.junit.Assert.*;

import java.util.List;

import lv.semti.morphology.analyzer.Word;
import lv.semti.morphology.analyzer.Wordform;
import lv.semti.morphology.attributes.AttributeNames;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.nlp.ie.ner.CMMClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LVMorphologyAnalysis;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.sequences.LVMorphologyReaderAndWriter;


public class TaggerTest {

	private static CMMClassifier<CoreLabel> cmm;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cmm = CMMClassifier.getClassifier("models/lv-morpho-model.ser.gz");
	}
	
	private static List<CoreLabel> tag (String sentence) {
		return cmm.classify( LVMorphologyReaderAndWriter.analyzeSentence(sentence.trim() ));
	}
		
	private void assertPOS(List<CoreLabel> sentence, int word, String pos) {
		assertValue(sentence, word, AttributeNames.i_PartOfSpeech, pos);
	}
		
	private void assertValue(List<CoreLabel> sentence, int word, String key, String value) {	
		String token = sentence.get(word).getString(TextAnnotation.class);
		assertFalse(token.contains("<s>"));
		Word analysis = sentence.get(word).get(LVMorphologyAnalysis.class);
		Wordform maxwf = analysis.getMatchingWordform(sentence.get(word).getString(AnswerAnnotation.class), true);
		assertEquals(value, maxwf.getValue(key));
	}
	
	private void assertLemma(List<CoreLabel> sentence, int word, String lemma) {	
		String token = sentence.get(word).getString(TextAnnotation.class);
		assertFalse(token.contains("<s>"));
		Word analysis = sentence.get(word).get(LVMorphologyAnalysis.class);
		Wordform maxwf = analysis.getMatchingWordform(sentence.get(word).getString(AnswerAnnotation.class), true);
		assertEquals(lemma, maxwf.getValue(AttributeNames.i_Lemma));
	}


	@Test
	public void sanity() {
		List<CoreLabel> sentence = tag("cirvis");
		assertPOS(sentence, 1, AttributeNames.v_Noun);
	}
	
	@Test
	public void roka() {
		List<CoreLabel> sentence = tag("es roku roku");
		assertPOS(sentence, 2, AttributeNames.v_Verb);
		assertPOS(sentence, 3, AttributeNames.v_Noun);
	}
	
	@Test
	public void gunta19dec_1() {
		// Guntas sūdzības pa skype 2012.12.19 - uz 2013.02.15 strādāja
		List<CoreLabel> word = tag("kontrolētājs");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "1");
		
		word = tag("Čeinijs");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "1");
		
		word = tag("GPS");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "1");
		
		word = tag("Čārlzs");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "1");
	}

	@Test
	public void gunta19dec_2() {
		// Guntas sūdzības pa skype 2012.12.19 - uz 2013.02.15 nestrādāja
		List<CoreLabel> word = tag("ambiciozs");
		assertPOS(word, 1, AttributeNames.v_Adjective);

		word = tag("padzīs");
		assertPOS(word, 1, AttributeNames.v_Verb);

		word = tag("marokāņu");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "2");

		word = tag("Makartnijas");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "4");
			
		word = tag("Bārbijas");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "4");

		word = tag("Ziemeļlatvijas");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "4");
		
		word = tag("nodotu");
		assertPOS(word, 1, AttributeNames.v_Verb);
		assertValue(word, 1, AttributeNames.i_Konjugaacija, "1");				
	}
	
	@Test
	public void gunta19dec_3() {
		// Guntas sūdzības pa skype 2012.12.19 - retās deklinācijas
		List<CoreLabel> word = tag("ragus");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "1");
			
		word = tag("dermatovenerologi");
		assertPOS(word, 1, AttributeNames.v_Noun);
		assertValue(word, 1, AttributeNames.i_Declension, "1");

	}
	
	@Test
	public void lemmas() {
		List<CoreLabel> word = tag("neizpaušana");
		assertLemma(word, 1, "neizpaušana");  // bija gļuks ar 'neizpausšana'
	}
}