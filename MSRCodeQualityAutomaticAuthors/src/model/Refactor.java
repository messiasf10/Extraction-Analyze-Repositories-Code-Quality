package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Messias Filho on 19/06/2019.
 */

public class Refactor {

	private static final int MINIMUM_INTERVAL_TIME = 10000;
	private static final String COMMITTED = "committed";
	private static final String OPENED_BY = "Opened by";
	private static int COUNT_PROJECT = 1;

	private File fileInput, fileOutputOnlyOne, fileOutputMostOne,
			fileOutputAll;	
	private InputStreamReader inputStreamReader;
	private ArrayList<Repository> listRepositoriesOnlyOne,
			listRepositoriesMostOne, listRepositoriesAll;

	public Refactor() {
		this.fileInput = new File("input/all-data-eliaquim.txt");
		this.fileOutputOnlyOne = new File("output/fileOutputOnlyOne.txt");
		this.fileOutputMostOne = new File("output/fileOutputMostOne.txt");
		this.fileOutputAll = new File("output/extractionAll.txt");
		this.listRepositoriesOnlyOne = new ArrayList<Repository>();
		this.listRepositoriesMostOne = new ArrayList<Repository>();
		this.listRepositoriesAll = new ArrayList<Repository>();
	}

	public void startExtraction() {

		try {
			// Variables for reading files
			FileReader frInput = new FileReader(fileInput);
			BufferedReader brInput = new BufferedReader(frInput);

			String line = "";
			String[] lineInfoSystemSplit;

			while ((line = brInput.readLine()) != null) {
				// Auxiliary Variables
				boolean committedFound = false, issueFound = false, moreThanOneCommitted = false, moreThanOneIssue = false;

				lineInfoSystemSplit = line.split("\t");

				Repository newRepository = new Repository(
						lineInfoSystemSplit[0], lineInfoSystemSplit[1],
						lineInfoSystemSplit[2], lineInfoSystemSplit[3],
						lineInfoSystemSplit[4], lineInfoSystemSplit[5],
						Double.parseDouble(lineInfoSystemSplit[6]),
						Boolean.parseBoolean(lineInfoSystemSplit[7]));

				int countCommitted = 0;

				// Start reading the site by COMMIT
				String urlSearchCommit = generateUrlSearch(
						newRepository.getCommitId(), TypeSearch.COMMITS);

				Thread.sleep(MINIMUM_INTERVAL_TIME);
				
				inputStreamReader = NetworkConnection.getPage(urlSearchCommit);
				
				BufferedReader in = new BufferedReader(inputStreamReader);
				String inputLine = "";

				while ((inputLine = in.readLine()) != null) {
					if (inputLine.contains(COMMITTED)) {
						committedFound = true;
						countCommitted++;
						if (countCommitted > 1) {
							moreThanOneCommitted = true;
							break;
						}
					}
				}

				if (committedFound) {

					if (!moreThanOneCommitted) {
						
						Thread.sleep(MINIMUM_INTERVAL_TIME);
						
						inputStreamReader = NetworkConnection.getPage(urlSearchCommit);
						in = new BufferedReader(inputStreamReader);
						inputLine = "";

						while ((inputLine = in.readLine()) != null) {
							if (inputLine.contains(COMMITTED)) {
								inputLine = in.readLine();
								break;
							}
						}
						
						inputLine = replaceLineCommitted(inputLine);
						String author = replaceLineCommittedAuthor(inputLine);

						newRepository.setAuthor(author);

						listRepositoriesOnlyOne.add(newRepository);
					} else {
						listRepositoriesMostOne.add(newRepository);
					}

				} else {

					int countIssue = 0;

					// Start reading the site by ISSUE
					String urlSearchIssue = generateUrlSearch(
							newRepository.getCommitId(), TypeSearch.ISSUES);

					Thread.sleep(MINIMUM_INTERVAL_TIME);

					inputStreamReader = NetworkConnection
							.getPage(urlSearchIssue);

					if (inputStreamReader == null)
						return;

					BufferedReader in2 = new BufferedReader(inputStreamReader);
					String inputLine2 = "";

					while ((inputLine2 = in2.readLine()) != null) {
						if (inputLine2.contains(newRepository.getSystem()
								+ "/issues")) {
							issueFound = true;
							countIssue++;
							if (countIssue > 1) {
								moreThanOneIssue = true;
								break;
							}
						}
					}

					if (issueFound) {

						if (!moreThanOneIssue) {
							
							Thread.sleep(MINIMUM_INTERVAL_TIME);
							
							inputStreamReader = NetworkConnection.getPage(urlSearchIssue);
							in2 = new BufferedReader(inputStreamReader);
							inputLine2 = "";

							while ((inputLine2 = in2.readLine()) != null) {
								if (inputLine2.contains(newRepository
										.getSystem() + "/issues")) {
									break;
								}
							}
							
							inputLine2 = replaceLineIssue(inputLine2);
							String author = replaceLineCommittedAuthor(inputLine2);

							newRepository.setAuthor(author);

							listRepositoriesOnlyOne.add(newRepository);
						} else {

							listRepositoriesMostOne.add(newRepository);

						}

					} else {

					}

				}

				listRepositoriesAll.add(newRepository);

				System.out.println("Project " + COUNT_PROJECT);
				System.out.println(newRepository.toString());

				COUNT_PROJECT++;

			}

			brInput.close();
			frInput.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void recordExtraction() {
		try {
			// Variables for writing files
			FileWriter fwOutputTrue = new FileWriter(fileOutputOnlyOne);
			BufferedWriter bwOutputTrue = new BufferedWriter(fwOutputTrue);

			FileWriter fwOutputFalse = new FileWriter(fileOutputMostOne);
			BufferedWriter bwOutputFalse = new BufferedWriter(fwOutputFalse);

			FileWriter fwOutputAll = new FileWriter(fileOutputAll);
			BufferedWriter bwOutputAll = new BufferedWriter(fwOutputAll);

			for (Repository repoTrue : listRepositoriesOnlyOne) {
				bwOutputTrue.write(repoTrue.toString());
				bwOutputTrue.newLine();
			}
			bwOutputTrue.write("Number of Projects: "
					+ listRepositoriesOnlyOne.size());
			bwOutputTrue.newLine();

			for (Repository repoFalse : listRepositoriesMostOne) {
				bwOutputFalse.write(repoFalse.toString());
				bwOutputFalse.newLine();
			}
			bwOutputFalse.write("Number of Projects: "
					+ listRepositoriesMostOne.size());
			bwOutputFalse.newLine();

			for (Repository repoAll : listRepositoriesAll) {
				bwOutputAll.write(repoAll.toString());
				bwOutputAll.newLine();
			}
			bwOutputAll.write("Number of Projects: "
					+ listRepositoriesAll.size());
			bwOutputAll.newLine();

			bwOutputTrue.close();
			fwOutputTrue.close();
			bwOutputFalse.close();
			fwOutputFalse.close();
			bwOutputAll.close();
			fwOutputAll.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String generateUrlSearch(String commitId, TypeSearch typeSearch) {
		return "https://github.com/search?q=" + commitId + "&type="
				+ typeSearch.name().toLowerCase();
	}

	private String replaceLineCommitted(String line) {
		return line.replaceAll("to ", "").replaceAll("\\<[^>]*>", "")
				.replaceAll("\\s+", "");
	}

	private String replaceLineCommittedAuthor(String line) {
		return line.split("/")[0];
	}

	private String replaceLineIssue(String line) {
		return line.replaceAll("\\<[^>]*>", "").replaceAll("\\s+", "");
	}

}
