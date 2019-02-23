package com.csg.cs.micos.entitybean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.Set;


/**
 * The persistent class for the ISSUETYPE database table.
 * 
 */
@Entity
@Table(name="ISSUETYPE")
public class Issuetype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="ISSUETYPESEQ", allocationSize=1 )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ISSUETYPESEQ")
	@Column(unique=true, nullable=false, precision=22)
	private Integer issuetypeid;

	@Column(nullable=false, length=1)
	private String available;

	private Timestamp createdate;

	@Column(precision=22)
	private BigDecimal defaultnoofphases;

	@Column(length=4000)
	private String description;

	@Column(precision=22)
	private BigDecimal earlyactionduedatephase;

	@Column(length=1)
	private String enforceearlyactionduedate;

	private Timestamp lastupdate;

	//bi-directional many-to-one association to Employee
	@ManyToOne
	@JoinColumn(name="LASTUPDATEDBY", nullable=false)
	private Employee lastupdatedby;


	@Column(length=1)
	private String limitnextphaseduedate;

	@Column(nullable=false, length=40)
	private String name;

	@Column(precision=22)
	private BigDecimal nextphaseduedateduration;

	@Column(length=1)
	private String phaseable;

	@Column(length=1)
	private String phasecompletionemail;

	@Column(nullable=false, length=1)
	private String standaloneissue;

	//bi-directional many-to-one association to Collectionissuenotification
	@OneToMany(mappedBy="issuetype" , cascade=CascadeType.ALL)
	private Set<Collectionissuenotification> collectionissuenotifications;

	//bi-directional many-to-one association to Collectiontype
	@OneToMany(mappedBy="issuetype")
	private Set<Collectiontype> collectiontypes;

	//bi-directional many-to-one association to Issue
	@OneToMany(mappedBy="issuetype")
	private Set<Issue> issues;

	//bi-directional many-to-one association to Issuecodinggroup
	@OneToMany(mappedBy="issuetype", cascade=CascadeType.ALL)
	private Set<Issuecodinggroup> issuecodinggroups;

	//bi-directional many-to-one association to Issuecreatorgroup
	@OneToMany(mappedBy="issuetype")
	private Set<Issuecreatorgroup> issuecreatorgroups;

	//bi-directional many-to-one association to Issuegroupadministrator
	@OneToMany(mappedBy="issuetype", cascade=CascadeType.ALL)
	private Set<Issuegroupadministrator> issuegroupadministrators;

	//bi-directional many-to-one association to Employee
    @ManyToOne
	@JoinColumn(name="CREATEDBY", nullable=false)
	private Employee employee;

    public Issuetype() {
    }

	public Integer getIssuetypeid() {
		return this.issuetypeid;
	}

	public void setIssuetypeid(Integer issuetypeid) {
		this.issuetypeid = issuetypeid;
	}

	public String getAvailable() {
		return this.available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public Timestamp getCreatedate() {
		return this.createdate;
	}

	public void setCreatedate(Timestamp createdate) {
		this.createdate = createdate;
	}

	public BigDecimal getDefaultnoofphases() {
		return this.defaultnoofphases;
	}

	public void setDefaultnoofphases(BigDecimal defaultnoofphases) {
		this.defaultnoofphases = defaultnoofphases;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getEarlyactionduedatephase() {
		return this.earlyactionduedatephase;
	}

	public void setEarlyactionduedatephase(BigDecimal earlyactionduedatephase) {
		this.earlyactionduedatephase = earlyactionduedatephase;
	}

	public String getEnforceearlyactionduedate() {
		return this.enforceearlyactionduedate;
	}

	public void setEnforceearlyactionduedate(String enforceearlyactionduedate) {
		this.enforceearlyactionduedate = enforceearlyactionduedate;
	}

	public Timestamp getLastupdate() {
		return this.lastupdate;
	}

	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}

	/**
	 * @return Returns the lastupdatedby.
	 */
	public Employee getLastupdatedby() {
		return lastupdatedby;
	}

	/**
	 * @param lastupdatedby The lastupdatedby to set.
	 */
	public void setLastupdatedby(Employee lastupdatedby) {
		this.lastupdatedby = lastupdatedby;
	}


	public String getLimitnextphaseduedate() {
		return this.limitnextphaseduedate;
	}

	public void setLimitnextphaseduedate(String limitnextphaseduedate) {
		this.limitnextphaseduedate = limitnextphaseduedate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getNextphaseduedateduration() {
		return this.nextphaseduedateduration;
	}

	public void setNextphaseduedateduration(BigDecimal nextphaseduedateduration) {
		this.nextphaseduedateduration = nextphaseduedateduration;
	}

	public String getPhaseable() {
		return this.phaseable;
	}

	public void setPhaseable(String phaseable) {
		this.phaseable = phaseable;
	}

	public String getPhasecompletionemail() {
		return this.phasecompletionemail;
	}

	public void setPhasecompletionemail(String phasecompletionemail) {
		this.phasecompletionemail = phasecompletionemail;
	}

	public String getStandaloneissue() {
		return this.standaloneissue;
	}

	public void setStandaloneissue(String standaloneissue) {
		this.standaloneissue = standaloneissue;
	}

	public Set<Collectionissuenotification> getCollectionissuenotifications() {
		return this.collectionissuenotifications;
	}

	public void setCollectionissuenotifications(Set<Collectionissuenotification> collectionissuenotifications) {
		this.collectionissuenotifications = collectionissuenotifications;
	}
	
	public Set<Collectiontype> getCollectiontypes() {
		return this.collectiontypes;
	}

	public void setCollectiontypes(Set<Collectiontype> collectiontypes) {
		this.collectiontypes = collectiontypes;
	}
	
	public Set<Issue> getIssues() {
		return this.issues;
	}

	public void setIssues(Set<Issue> issues) {
		this.issues = issues;
	}
	
	public Set<Issuecodinggroup> getIssuecodinggroups() {
		return this.issuecodinggroups;
	}

	public void setIssuecodinggroups(Set<Issuecodinggroup> issuecodinggroups) {
		this.issuecodinggroups = issuecodinggroups;
	}
	
	public Set<Issuecreatorgroup> getIssuecreatorgroups() {
		return this.issuecreatorgroups;
	}

	public void setIssuecreatorgroups(Set<Issuecreatorgroup> issuecreatorgroups) {
		this.issuecreatorgroups = issuecreatorgroups;
	}
	
	/**
	 * @return the issuegroupadministrators
	 */
	public Set<Issuegroupadministrator> getIssuegroupadministrators() {
		return issuegroupadministrators;
	}

	/**
	 * @param issuegroupadministrators the issuegroupadministrators to set
	 */
	public void setIssuegroupadministrators(
			Set<Issuegroupadministrator> issuegroupadministrators) {
		this.issuegroupadministrators = issuegroupadministrators;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
}