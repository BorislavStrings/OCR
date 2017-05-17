package Semantic;

import it.uniroma1.lcl.babelfy.Babelfy;
import it.uniroma1.lcl.babelfy.Babelfy.Matching;
import it.uniroma1.lcl.babelfy.Babelfy.AccessType;
import it.uniroma1.lcl.babelfy.data.Annotation;
import it.uniroma1.lcl.babelfy.data.BabelSynsetAnchor;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.IOException;
public class Semantic {
	
	public String check(String sentence) throws Exception {
		String result = "";
	    // get an instance of the Babelfy RESTful API manager
	    Babelfy bfy = Babelfy.getInstance(AccessType.ONLINE);
	    // the actual disambiguation call
	    Annotation annotations = bfy.babelfy("", sentence, Matching.EXACT, Language.BG);
	    
	    result += ("inputText: " + sentence + "\nannotations: ");
	    for(BabelSynsetAnchor annotation : annotations.getAnnotations())
	        result += (annotation.getAnchorText() + "\t" + annotation.getBabelSynset().getId() + "\t" + annotation.getBabelSynset());
	    
	    return result; 
	}
}
