package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class WriteFiles {
	
	private File fileInput, fileOutputTrue, fileOutputFalse, fileOutputAll;
	private ArrayList<Repository> listRepositoriesTrue, listRepositoriesFalse, listRepositoriesAll; 
	
	public WriteFiles() {
		this.fileInput = new File("input/repositories.txt");
		this.fileOutputTrue = new File("extractionTrue.txt");
		this.fileOutputFalse = new File("extractionFalse.txt");
		this.fileOutputAll = new File("extractionAll.txt");
		this.listRepositoriesTrue = new ArrayList<Repository>();
		this.listRepositoriesFalse = new ArrayList<Repository>();
		this.listRepositoriesAll = new ArrayList<Repository>();
	}
	
	public void start(){
		readInput();
		recordExtraction();
	}
	
	private void readInput(){
		try {
			int countAux = 1;
			
			FileReader fr1 = new FileReader(fileInput);
			BufferedReader br1 = new BufferedReader(fr1);
			
			String line = "";
			while((line = br1.readLine()) != null){
				if(line.equals("Project " + countAux)){
					countAux++;
					continue;
				}
				
				String dados[] = line.split("\t");
				Repository repository = new Repository(dados[0], dados[1], dados[2], dados[3], dados[4], dados[5], Double.parseDouble(dados[6]));
				repository.setExist(Boolean.parseBoolean(dados[7]));
				
				listRepositoriesAll.add(repository);
				if(repository.isExist())
					listRepositoriesTrue.add(repository);
				else
					listRepositoriesFalse.add(repository);
				
			}
			
			br1.close();
			fr1.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void recordExtraction(){
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
	

}
