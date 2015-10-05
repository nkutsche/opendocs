package net.sqf.xmlUtils.xslt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.transform.Source;

import net.sqf.stringUtils.TextSource;
import net.sqf.utils.process.exceptions.CancelException;
import net.sqf.utils.process.log.ProcessLoger;
import net.sqf.xmlUtils.exceptions.XSLTErrorListener;

//import net.sf.saxon.TransformerFactoryImpl;

public class XSLTPipe {
	
	private ArrayList<XSLTStep> xsltSteps = new ArrayList<XSLTStep>();
	private ArrayList<ArrayList<TextSource>> tempResults = new ArrayList<ArrayList<TextSource>>();
	private XSLTErrorListener errorListener = new XSLTErrorListener();
	private final String title;
//	private static TransformerFactoryImpl transFac = SaxonUtils.getTransFac();
	private Properties outputProps;
	
//	private final Transformer[] transformers;
	
	
	
	public XSLTPipe(String title) {
		this.title = title;
	}
	public XSLTPipe(String title, Properties outputProperties) {
		this(title);
		this.outputProps = outputProperties;
	}
	
	public XSLTPipe(String title, XSLTErrorListener el) {
		this(title);
		this.errorListener = el;
	}
	
	public XSLTPipe(String title, Properties outputProperties, XSLTErrorListener el) {
		this(title, el);
		this.outputProps = outputProperties;
	}
	
	public void addStep(Source[] xsls) throws XSLTErrorListener {
		for (int i = 0; i < xsls.length; i++) {
			addStep(xsls[i], new ArrayList<Parameter>());
		}
	}
	
	public void addStep(Source xsl) throws XSLTErrorListener{
		addStep(xsl, new ArrayList<Parameter>());
	}
	
	public void addStep(TextSource[] xsls) throws XSLTErrorListener {
		for (int i = 0; i < xsls.length; i++) {
			addStep(xsls[i], new ArrayList<Parameter>());
		}
	}
	
	public void addStep(TextSource xsl) throws XSLTErrorListener{
		addStep(xsl, new ArrayList<Parameter>());
	}
	
	public void addStep(File[] xsls) throws XSLTErrorListener {
		for (int i = 0; i < xsls.length; i++) {
			addStep(xsls[i], new ArrayList<Parameter>());
		}
	}
	
	public void addStep(File xsl) throws XSLTErrorListener{
		addStep(xsl, new ArrayList<Parameter>());
	}
	
	public void addStep(Source xsl, ArrayList<Parameter> params) throws XSLTErrorListener {
		addStep(errorListener != null ? new XSLTStep(xsl, errorListener, params) : new XSLTStep(xsl, params));
	}
	
	public void addStep(TextSource xsl, ArrayList<Parameter> params) throws XSLTErrorListener{
		addStep(errorListener != null ? new XSLTStep(xsl, errorListener, params) : new XSLTStep(xsl, params));
		
	}
	
	public void addStep(File xsl, ArrayList<Parameter> params) throws XSLTErrorListener {
		addStep(errorListener != null ? new XSLTStep(xsl, errorListener, params) : new XSLTStep(xsl, params));
	}
	
	private void addStep(XSLTStep step){
		if(outputProps != null){
			step.setOutputProperty(outputProps);
		}
		this.xsltSteps.add(step);
	}
	
	
	public void reset(){
		this.xsltSteps = new ArrayList<XSLTStep>();
	}
	
	public ArrayList<TextSource> pipe(File source, ArrayList<Parameter> params) throws IOException, XSLTErrorListener{
		return pipe(TextSource.readTextFile(source), params);
	}
	
	public ArrayList<TextSource> pipe(File source) throws IOException, XSLTErrorListener{
		return pipe(TextSource.readTextFile(source));
	}
	public ArrayList<TextSource> pipe(TextSource source) throws XSLTErrorListener {
		return pipe(source, new ArrayList<Parameter>());
	}

	
	public ArrayList<TextSource> pipe(TextSource source, ArrayList<Parameter> params) throws XSLTErrorListener {
		this.tempResults = new ArrayList<ArrayList<TextSource>>();
		tempResults.add(new ArrayList<TextSource>());
		tempResults.get(tempResults.size() - 1).add(source);
		
		HashMap<Integer, ArrayList<Parameter>> paramByStep = createParamStepMap(params);
		
		int stepCounter = 0;
		for (Iterator<XSLTStep> iterator = this.xsltSteps.iterator(); iterator.hasNext(); stepCounter++) {
			XSLTStep step = iterator.next();
			ArrayList<Parameter> stepParam = paramByStep.containsKey(stepCounter) ? paramByStep.get(stepCounter) : new ArrayList<Parameter>();
			
			ArrayList<TextSource> temps = new ArrayList<TextSource>();
			for (TextSource tempResult : tempResults.get(tempResults.size() - 1)) {
				temps.add(step.transform(tempResult, stepParam));
				for (TextSource secResult : step.getSecondaryResults()) {
					temps.add(secResult);
				}
			}
			tempResults.add(temps);
//			ArrayList<TransformerException> errors = step.getErrors(1);
//			for (TransformerException e : errors) {
//				System.err.println(e.getLocalizedMessage());
//			}
		}
		
		if(this.errorListener.hasErrors()){
			throw this.errorListener;
		}
		
		return tempResults.get(tempResults.size() - 1);
	}
	
	public ArrayList<TextSource> pipe(TextSource source, ProcessLoger loger) {
		return pipe(source, new ArrayList<Parameter>(), loger);
	}
	
	public ArrayList<TextSource> pipe(TextSource source, ArrayList<Parameter> params, ProcessLoger loger) {
		try {
			return pipe(source, params);
		} catch (XSLTErrorListener e) {
			try {
				loger.log(e);
			} catch (CancelException e1) {
				return null;
			}
			return null;
		}
	}
	
	
//	pipeMain - just the primary result
	
	public TextSource pipeMain(TextSource source, ArrayList<Parameter> params) throws XSLTErrorListener {
		return this.pipe(source, params).get(0);
	}

	public TextSource pipeMain(TextSource source) throws XSLTErrorListener {
		return this.pipe(source).get(0);
	}
	public TextSource pipeMain(TextSource source, ProcessLoger loger) {
		return this.pipe(source, loger).get(0);
	}
	
	public TextSource pipeMain(TextSource source, ArrayList<Parameter> params, ProcessLoger loger) {
		return this.pipe(source, params, loger).get(0);
	}
	
	
	
	public ArrayList<TextSource> getTempResults() {
		// TODO Auto-generated method stub
		ArrayList<TextSource> tempResults = new ArrayList<TextSource>();
		for (ArrayList<TextSource> result : this.tempResults) {
			tempResults.add(result.get(0));
		}
		return tempResults;
	}
	
	private HashMap<Integer, ArrayList<Parameter>> createParamStepMap(ArrayList<Parameter> paramList){
		HashMap<Integer, ArrayList<Parameter>> paramByStep = new HashMap<Integer, ArrayList<Parameter>>();
		for (Iterator<Parameter> iterator = paramList.iterator(); iterator.hasNext();) {
			Parameter param = iterator.next();
			if(paramByStep.containsKey(param.getStep())){
				paramByStep.get(param.getStep()).add(param);
			} else {
				ArrayList<Parameter> stepList = new ArrayList<Parameter>();
				stepList.add(param);
				paramByStep.put(param.getStep(), stepList);
			}
		}
		return paramByStep;
	}
	public void setOutputProperties(Properties outputProperties) {
		this.outputProps = outputProperties;
	}
	
	@Override
	public String toString() {
		return this.title;
	}
//	
//	public XSLTPipe(StreamSource[] stylesheets) throws TransformerConfigurationException {
//		transformers = new Transformer[stylesheets.length];
//		for (int i = 0; i < stylesheets.length; i++) {
//			transformers[i] = transFac.newTransformer(stylesheets[i]);
//		}
//	}
	
//	public StreamResult transform(File input, String outputSystemId) throws TransformerException {
//		StreamResult result = new StreamResult();
//		result.setSystemId(outputSystemId);
//		transform(new StreamSource(input), result);
//		return result;
//	}
//	
//	public StreamResult transform(StreamSource input) throws TransformerException{
//		StreamResult output = new StreamResult();
//		for (int i = 0; i < this.transformers.length; i++) {
//			transformStep(i, input, output);
//			input = SaxonUtils.castResultToSource(output);
//		}escali-ext4.sch
//		return output;
//	}
//	
//	public void transform(File input, File output) throws TransformerException {
//		transform(new StreamSource(input), new StreamResult(output));
//	}

	
//	public void transform(StreamSource input, StreamResult output) throws TransformerException{
//		StreamSource tempInput = new StreamSource();
//		for (int i = 0; i < this.transformers.length; i++) {
//			output.setWriter(new StringWriter());
//			transformStep(i, i == 0 ? input : tempInput, output);
//			if(i + 1 < this.transformers.length){
//				tempInput = SaxonUtils.castResultToSource(output);
//			}
//		}
//	}
//	
//	private void transformStep(int step, StreamSource input, StreamResult output) throws TransformerException{
//		Transformer tr = transformers[step];
//		tr.clearParameters();
//		tr.transform(input, output);
//		
//	}
}
