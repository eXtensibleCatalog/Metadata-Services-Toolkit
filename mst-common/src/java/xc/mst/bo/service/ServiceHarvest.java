package xc.mst.bo.service;

import java.util.Date;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;

public class ServiceHarvest {

	protected Long id = null;
	protected Set set = null;
	protected Format format = null;
	protected Date from = null;
	protected Date until = null;
	protected Long highestId = null;
	protected String repoName = null;
	protected Service service = null;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Set getSet() {
		return set;
	}
	public void setSet(Set set) {
		this.set = set;
	}
	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getUntil() {
		return until;
	}
	public void setUntil(Date until) {
		this.until = until;
	}
	public Long getHighestId() {
		return highestId;
	}
	public void setHighestId(Long highestId) {
		this.highestId = highestId;
	}
	public String getRepoName() {
		return repoName;
	}
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	
}
