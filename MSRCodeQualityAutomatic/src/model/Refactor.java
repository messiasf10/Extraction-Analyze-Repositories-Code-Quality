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
	private static final String WRONG_MESSAGE = "We couldn’t find any";	
	private static int COUNT_PROJECT = 1;
	
	private File fileInput, fileOutputTrue, fileOutputFalse, fileOutputAll;
	private Repository repositoryBefore;
	private InputStreamReader inputStreamReader;
	private ArrayList<Repository> listRepositoriesTrue, listRepositoriesFalse, listRepositoriesAll;
	
	public Refactor() {
		this.fileInput = new File("input/raw-data.csv");
		this.fileOutputTrue = new File("output/extractionTrue.txt");
		this.fileOutputFalse = new File("output/extractionFalse.txt");
		this.fileOutputAll = new File("output/extractionAll.txt");
		this.repositoryBefore = null;
		this.listRepositoriesTrue = new ArrayList<Repository>();
		this.listRepositoriesFalse = new ArrayList<Repository>();
		this.listRepositoriesAll = new ArrayList<Repository>();
	}

	public void startExtraction() {		

		try {
			// Variables for reading files
			FileReader frInput = new FileReader(fileInput);
			BufferedReader brInput = new BufferedReader(frInput);
			
			String line = brInput.readLine();
			String[] lineInfoSystemSplit;

			while (brInput.ready()) {
				// Auxiliary Variables
				boolean commitFound = true, issueFound = true;

				lineInfoSystemSplit = line.split(",");

				Repository newRepository = new Repository(
						lineInfoSystemSplit[0], lineInfoSystemSplit[1],
						lineInfoSystemSplit[2], lineInfoSystemSplit[3],
						lineInfoSystemSplit[4], lineInfoSystemSplit[5],
						Double.parseDouble(lineInfoSystemSplit[6]));

				// First repository to be parsed
				if (repositoryBefore == null) {

					// Start reading the site by COMMIT
					String urlSearchCommit = generateUrlSearch(
							newRepository.getCommitId(), TypeSearch.COMMITS);

					inputStreamReader = NetworkConnection.getPage(urlSearchCommit);

					BufferedReader in = new BufferedReader(inputStreamReader);
					String inputLine = "";

					while ((inputLine = in.readLine()) != null) {
						if (inputLine.contains(WRONG_MESSAGE)) {
							commitFound = false;
							break;
						}
					}

					if (!commitFound) {

						// Start reading the site by ISSUE
						String urlSearchIssue = generateUrlSearch(
								newRepository.getCommitId(), TypeSearch.ISSUES);

						Thread.sleep(MINIMUM_INTERVAL_TIME);

						inputStreamReader = NetworkConnection.getPage(urlSearchIssue);

						if (inputStreamReader == null)
							return;

						BufferedReader in2 = new BufferedReader(inputStreamReader);
						String inputLine2 = "";

						while ((inputLine2 = in2.readLine()) != null) {
							if (inputLine2.contains(WRONG_MESSAGE)) {
								issueFound = false;
								break;
							}
						}

						if (!issueFound) {
							// Commit not found
							newRepository.setExist(false);
						}

					}	
					
					if (newRepository.isExist()) {
						listRepositoriesTrue.add(newRepository);							
					} else {
						listRepositoriesFalse.add(newRepository);
					}
					
					listRepositoriesAll.add(newRepository);
					
					System.out.println("Project " + COUNT_PROJECT);
					System.out.println(newRepository.toString());
					
					COUNT_PROJECT++;
				}

				if (repositoryBefore != null && !newRepository.getCommitId().equals(repositoryBefore.getCommitId())) {
					// Start reading the site by COMMIT
					String urlSearchCommit = generateUrlSearch(newRepository.getCommitId(), TypeSearch.COMMITS);

					Thread.sleep(MINIMUM_INTERVAL_TIME);

					inputStreamReader = NetworkConnection.getPage(urlSearchCommit);

					BufferedReader in = new BufferedReader(inputStreamReader);
					String inputLine = "";

					while ((inputLine = in.readLine()) != null) {
						if (inputLine.contains(WRONG_MESSAGE)) {
							commitFound = false;
							break;
						}
					}

					if (!commitFound) {

						// Start reading the site by ISSUE
						String urlSearchIssue = generateUrlSearch(newRepository.getCommitId(), TypeSearch.ISSUES);

						Thread.sleep(MINIMUM_INTERVAL_TIME);

						inputStreamReader = NetworkConnection.getPage(urlSearchIssue);
						
						if (inputStreamReader == null) 
		                    return;
						
						BufferedReader in2 = new BufferedReader(inputStreamReader);						
						String inputLine2 = "";

						while ((inputLine2 = in2.readLine()) != null) {
							if (inputLine2.contains(WRONG_MESSAGE)) {
								issueFound = false;
								break;
							}
						}

						if (!issueFound) {
							// Commit not found
							newRepository.setExist(false);
						}

					}
					
					if (newRepository.isExist()) {
						listRepositoriesTrue.add(newRepository);						
					} else {
						listRepositoriesFalse.add(newRepository);						
					}
					
					listRepositoriesAll.add(newRepository);
					
					System.out.println("Project " + COUNT_PROJECT);
					System.out.println(newRepository.toString());
					
					COUNT_PROJECT++;					
				}
				
				repositoryBefore = newRepository;

				line = brInput.readLine();
			}

			brInput.close();
			frInput.close();			

		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	public void recordExtraction(){
		try {
			// Variables for writing files
			FileWriter fwOutputTrue = new FileWriter(fileOutputTrue);
			BufferedWriter bwOutputTrue = new BufferedWriter(fwOutputTrue);

			FileWriter fwOutputFalse = new FileWriter(fileOutputFalse);
			BufferedWriter bwOutputFalse = new BufferedWriter(fwOutputFalse);
			
			FileWriter fwOutputAll = new FileWriter(fileOutputAll);
			BufferedWriter bwOutputAll = new BufferedWriter(fwOutputAll);
			
			for(Repository repoTrue : listRepositoriesTrue){
				bwOutputTrue.write(repoTrue.toString());
				bwOutputTrue.newLine();
			}
			bwOutputTrue.write("Number of Projects: " + listRepositoriesTrue.size());
			bwOutputTrue.newLine();
			
			for(Repository repoFalse : listRepositoriesFalse){
				bwOutputFalse.write(repoFalse.toString());
				bwOutputFalse.newLine();
			}
			bwOutputFalse.write("Number of Projects: " + listRepositoriesFalse.size());
			bwOutputFalse.newLine();
			
			for(Repository repoAll : listRepositoriesAll){
				bwOutputAll.write(repoAll.toString());
				bwOutputAll.newLine();
			}
			bwOutputAll.write("Number of Projects: " + listRepositoriesAll.size());
			bwOutputAll.newLine();
			
			bwOutputTrue.close();
			fwOutputTrue.close();
			bwOutputFalse.close();
			fwOutputFalse.close();
			bwOutputAll.close();
			fwOutputAll.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private String generateUrlSearch(String commitId, TypeSearch typeSearch) {
		return "https://github.com/search?q=" + commitId + "&type="
				+ typeSearch.name().toLowerCase();
	}

}
