package xc.mst.bo.service;

import java.util.Date;

import xc.mst.bo.provider.Set;

public class ServiceHarvest {

	protected Long id = null;
	protected Set set = null;
	protected Date from = null;
	protected Date until = null;
	protected Long highestId = null;
	protected Long repoId = null;
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
	public Long getRepoId() {
		return repoId;
	}
	public void setRepoId(Long repoId) {
		this.repoId = repoId;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	
}
