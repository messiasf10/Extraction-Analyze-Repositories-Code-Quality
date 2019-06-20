package model;

/**
 * Created by Messias Filho on 19/06/2019.
 */

public class Repository implements Comparable<Repository>{
	
	private String system, commitId, className, measure, metricName, beforeAfter;
	private double value;
	private boolean exist;
	
	public Repository(String system, String commitId, String className,
			String measure, String metricName, String beforeAfter, double value) {		
		this.system = system;
		this.commitId = commitId;
		this.className = className;
		this.measure = measure;
		this.metricName = metricName;
		this.beforeAfter = beforeAfter;
		this.value = value;
		this.exist = true;
	}

	public String getSystem() {
		return system;
	}	
	
	public void setSystem(String system) {
		this.system = system;
	}
	
	public String getCommitId() {
		return commitId;
	}
	
	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getMeasure() {
		return measure;
	}
	
	public void setMeasure(String measure) {
		this.measure = measure;
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	
	public String getBeforeAfter() {
		return beforeAfter;
	}
	
	public void setBeforeAfter(String beforeAfter) {
		this.beforeAfter = beforeAfter;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
		
	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public String toString(){
		return system + "\t" + commitId + "\t" + className+ "\t" + 
				measure + "\t" + metricName + "\t" + beforeAfter + "\t" + 
				value + "\t" + exist;		
	}

	@Override
	public int compareTo(Repository repo) {
		return this.system.compareTo(repo.system);
	}
	
}
