package edu.stanford.nlp.ie;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Properties;

//import edu.stanford.nlp.ie.regexp.NumberSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * Subclass of ClassifierCombiner that behaves like a NER, by copying the AnswerAnnotation labels to NERAnnotation
 * Also, it runs an additional classifier (QuantifiableEntityNormalizer) to recognize numeric entities
 * @author Mihai Surdeanu
 *
 */
public class NERClassifierCombiner extends ClassifierCombiner<CoreLabel> {

	private final boolean applyNumericClassifiers = false;
//  public static final boolean APPLY_NUMERIC_CLASSIFIERS_DEFAULT = false;
//  public static final String APPLY_NUMERIC_CLASSIFIERS_PROPERTY = "ner.applyNumericClassifiers";
//
//  private final boolean useSUTime = false;

  //private final AbstractSequenceClassifier<CoreLabel> nsc;

  public NERClassifierCombiner(Properties props)
    throws FileNotFoundException
  {
    super(props);
  }

  public NERClassifierCombiner(String... loadPaths) throws FileNotFoundException {
    super(loadPaths);
  }

  @SafeVarargs
public NERClassifierCombiner(AbstractSequenceClassifier<CoreLabel>... classifiers) throws FileNotFoundException {
    super(classifiers);
  }

  public boolean isApplyNumericClassifiers() {
    return applyNumericClassifiers;
  }

  private static <INN extends CoreMap> void copyAnswerFieldsToNERField(List<INN> l) {
    for (INN m: l) {
      m.set(NamedEntityTagAnnotation.class, m.get(AnswerAnnotation.class));
    }
  }

  @Override
  public List<CoreLabel> classify(List<CoreLabel> tokens) {
    return classifyWithGlobalInformation(tokens, null, null);
  }

  @Override
  public List<CoreLabel> classifyWithGlobalInformation(List<CoreLabel> tokens, final CoreMap document, final CoreMap sentence) {
	List<CoreLabel> output = super.classify(tokens);
	// AnswerAnnotation -> NERAnnotation
	copyAnswerFieldsToNERField(output);
	return output;
  }
//  private void recognizeNumberSequences(List<CoreLabel> words, final CoreMap document, final CoreMap sentence) {
//    // we need to copy here because NumberSequenceClassifier overwrites the AnswerAnnotation
//    List<CoreLabel> newWords = NumberSequenceClassifier.copyTokens(words, sentence);
//
//    nsc.classifyWithGlobalInformation(newWords, document, sentence);
//
//    // copy AnswerAnnotation back. Do not overwrite!
//    // also, copy all the additional annotations generated by SUTime and NumberNormalizer
//    for (int i = 0, sz = words.size(); i < sz; i++){
//      CoreLabel origWord = words.get(i);
//      CoreLabel newWord = newWords.get(i);
//
//      // System.err.println(newWord.word() + " => " + newWord.get(AnswerAnnotation.class) + " " + origWord.ner());
//
//      String before = origWord.get(AnswerAnnotation.class);
//      String newGuess = newWord.get(AnswerAnnotation.class);
//      if ((before == null || before.equals(nsc.flags.backgroundSymbol) || before.equals("MISC")) && !newGuess.equals(nsc.flags.backgroundSymbol)) {
//        origWord.set(AnswerAnnotation.class, newGuess);
//      }
//
//      // transfer other annotations generated by SUTime or NumberNormalizer
//      NumberSequenceClassifier.transferAnnotations(newWord, origWord);
//
//    }
//  }
}

