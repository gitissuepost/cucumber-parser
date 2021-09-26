package parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.gherkin.GherkinDocumentBuilder;
import io.cucumber.gherkin.Parser;
import io.cucumber.gherkin.TokenMatcher;
import io.cucumber.messages.IdGenerator;
import io.cucumber.messages.Messages.Envelope;
import io.cucumber.messages.Messages.GherkinDocument;
import io.cucumber.messages.Messages.GherkinDocument.Feature;
import io.cucumber.messages.Messages.GherkinDocument.Feature.FeatureChild;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Scenario;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Step.DataTable;
import io.cucumber.messages.Messages.GherkinDocument.Feature.TableRow.TableCell;
import io.cucumber.messages.Messages.GherkinDocument.Feature.Tag;

public class FeatureGenerator {

	private IdGenerator idGenerator = new IdGenerator.Incrementing();

	public void parserGherkin() throws IOException {
		File file = new File(System.getProperty("user.dir") + "/test.feature");
		String data = FileUtils.readFileToString(file, "UTF-8");
		TokenMatcher matcher = new TokenMatcher("en");
		Parser<GherkinDocument.Builder> parser = new Parser<>(new GherkinDocumentBuilder(idGenerator));

		GherkinDocument gherkinDocument = parser.parse(data, matcher).build();
		System.out.println(gherkinDocument.getFeature().getName());
		System.out.println(gherkinDocument.getFeature().getChildrenCount());
		System.out.println(gherkinDocument.getFeature().getChildren(0).getScenario().getTagsList());
		System.out.println(gherkinDocument.getFeature().getChildren(0).getScenario().getName());
		System.out.println(gherkinDocument.getFeature().getChildren(0).getScenario().getSteps(0).getText());
	}

	public void generateFeatues(String featureRootFolderPath, String featureTargetFolder) throws IOException {
		List<String> featurePaths = Files.walk(Paths.get(featureRootFolderPath)).map(Path::toString)
				.filter(f -> f.endsWith(".feature")).collect(Collectors.toList());

		List<Envelope> envelopes = Gherkin.fromPaths(featurePaths, false, true, false, idGenerator)
				.collect(Collectors.toList());

		for (Envelope envelope : envelopes) {
			StringBuilder builder = new StringBuilder();
			StringBuilder featureBuilder = new StringBuilder();
			GherkinDocument gherkinDocument = envelope.getGherkinDocument();
			Feature feature = gherkinDocument.getFeature();
			featureBuilder.append("Feature: ").append(feature.getName()).append("\n\n");
			featureBuilder.append("Description: ").append(feature.getDescription()).append("\n\n");

			String output = gherkinDocument.getUri().replace(featureRootFolderPath, featureTargetFolder);

			for (FeatureChild featureChild : feature.getChildrenList()) {
				Scenario scenario = featureChild.getScenario();

				StringBuilder parsedScenarioBuilder = parseScenario(scenario);
				if (parsedScenarioBuilder.length() > 0)
					builder.append("\n").append(parsedScenarioBuilder);
			}

			if (builder.length() > 0) {
				builder.insert(0, featureBuilder);
				String fileName = output.substring(output.lastIndexOf("\\"));
				File targetFolder = new File(output.replace(fileName, ""));
				targetFolder.mkdirs();

				FileWriter writer = new FileWriter(output);
				writer.write(builder.toString());
				writer.close();
			}
		}
	}

	private StringBuilder parseScenario(Scenario scenario) {
		List<String> tagList = scenario.getTagsList().stream().map(Tag::getName).collect(Collectors.toList());

		StringBuilder scenarioBuilder = new StringBuilder();
		scenarioBuilder.append(tagList.size() > 0 ? String.join(" ", tagList) + "\n" : "");
		scenarioBuilder.append(scenario.getKeyword()).append(": ").append(scenario.getName()).append("\n");
		scenarioBuilder.append("\"\"\"").append(scenario.getDescription()).append("\"\"\"");

		for (Step step : scenario.getStepsList()) {
			String stepDef = step.getText().replaceAll("\\s+", " ");
			scenarioBuilder.append(step.getKeyword()).append(" ").append(stepDef).append("\n");
			DataTable dataTable = step.getDataTable();
			if (dataTable.getRowsCount() > 0) {
				dataTable.getRowsList().stream().forEach(row -> {
					List<String> cells = row.getCellsList().stream().map(TableCell::getValue)
							.collect(Collectors.toList());
					cells.forEach(cell -> scenarioBuilder.append("|").append(cell));
					scenarioBuilder.append("|\n\n");
				});
			}
		}

		return scenarioBuilder;
	}
}
