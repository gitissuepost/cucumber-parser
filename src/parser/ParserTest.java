package parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import io.cucumber.gherkin.GherkinDocumentBuilder;
import io.cucumber.gherkin.Parser;
import io.cucumber.gherkin.TokenMatcher;
import io.cucumber.messages.IdGenerator;
import io.cucumber.messages.MessageToNdjsonWriter;
import io.cucumber.messages.Messages.GherkinDocument;

public class ParserTest {

	public static void main(String[] args) throws IOException {
		File file = new File(System.getProperty("user.dir") + "/test.feature");
		String data = FileUtils.readFileToString(file, "UTF-8");
		TokenMatcher matcher = new TokenMatcher("en");
		IdGenerator idGenerator = new IdGenerator.Incrementing();
		Parser<GherkinDocument.Builder> parser = new Parser<>(new GherkinDocumentBuilder(idGenerator));

		GherkinDocument gherkinDocument = parser.parse(data, matcher).build();
		System.out.println(gherkinDocument.getFeature().getName());
		System.out.println(gherkinDocument.getFeature().getChildrenCount());
		System.out.println(gherkinDocument.getFeature().getChildren(0).getScenario().getTagsList());
		System.out.println(gherkinDocument.getFeature().getChildren(0).getScenario().getName());
		System.out.println(gherkinDocument.getFeature().getChildren(0).getScenario().getSteps(0).getText());

		FileOutputStream fos = new FileOutputStream("Output.json");
		MessageToNdjsonWriter writer = new MessageToNdjsonWriter(fos);
		writer.write(gherkinDocument);
		

//        PickleCompiler pickleCompiler = new PickleCompiler(idGenerator);
//        List<Pickle> pickles = pickleCompiler.compile(gherkinDocument, "hello.feature");
//        
//        pickles.get(0).writeTo(fos);
		fos.close();
		
//		gherkinDocument.get
		
	}
}