/******************************************************************************
 * Copyright(c) 2003-2007 CREDIT SUISSE Financial Services. All Rights Reserved.
 *
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The bean class for the Aorowner table EMPLOYEE.
 * 
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 153146 $
 * @version $Id: Employee.java,v 1.1 2010/10/25 15:27:25 153146 Exp $
 */
public class Employee implements Serializable {

	/**
	 * Holds Serial UID.
	 */
	private static final long serialVersionUID = -2356448568966420010L;

	/**
	 * Holds identifier field.
	 */
	private Integer employeeid;

	/**
	 * Holds pid.
	 */
	private String pid;

	/**
	 * Holds personal title.
	 */
	private String personaltitle;

	/**
	 * Holds First name.
	 */
	private String firstname;

	/**
	 * Holds Last name.
	 */
	private String lastname;

	/**
	 * Holds Instradierung .
	 */
	private String instradierung;

	/**
	 * Holds department.
	 */
	private String department;

  /**
   * Holds Business Unit.
   */
  private String bu;

  /**
	 * Holds Division.
	 */
	private String division;

	/**
	 * Holds Mail .
	 */
	private String mail;

	/**
	 * Holds Country .
	 */
	private String country;

	/**
	 * Holds Address.
	 */
	private String address;

	/**
	 * Holds Lang.
	 */
	private String lang;

	/**
	 * Holds Telephone.
	 */
	private String telephone;

	/**
	 * Holds Active.
	 */
	private String active;

	/**
	 * Holds Supervisor pid.
	 */
	private String supervisorpid;

	/**
	 * Holds Batcho Date.
	 */
	private Date batchodate;
	
	/**
	 * Holds lastworkingday.
	 */
	private Date lastworkingday;

	/**
	 * Holds Create Date.
	 */
	private Timestamp createdate;

	/**
	 * Holds Last update.
	 */
	private Timestamp lastupdate;

	/**
	 * Holds Last update employee ID.
	 */
	private BigDecimal lastupdateemployeeid;

	/**
	 * Holds Poweroption.
	 */
	private Set<Poweroption> poweroption;
	
	/**
	 * Holds Poschedule.
	 */
	private Set<Poschedule> poschedule;
	
	/**
	 * Holds Poescalation.
	 */
	private Set<Poescalation> poescalation;	
	
	/**
	 * Holds Notificationbatch.
	 */
	private Set<Notificationbatch> notificationbatchs;
	
	/**
	 * Holds Notificationtype.
	 */
	private Set<Notificationtype> notificationtypes;
	
	/**
	 * Holds Poweroption.
	 */
	private Set<Poweroption> poweroptions;
	
	/**
	 * Holds Poschedule.
	 */
	private Set<Poschedule> poschedules;
	
	/**
	 * Holds Poescalation.
	 */
	private Set<Poescalation> poescalations;
	
	/**
	 * Holds Notification.
	 */
	private Set<Notification> notifications;
	
	/**
	 * Holds notifyuser.
	 */
	private Set<Notification> notifyuser;
	
	/**
	 * Holds Signoff ratings.
	 */
	private Set<Rating> ratings;
	
	/**
	 * Holds ratinggroups.
	 */
	private Set<Rating> ratinggroups;
	
	/**
	 * Holds ratinggroupratings.
	 */
	private Set<Rating> ratinggroupratings;

	/**
	 * Holds Execution tasks.
	 */
	private Set<Executiontask> executiontasks;

	/**
	 * Holds Accessaors.
	 */
	private Set<Accessaor> accessaors;

	/**
	 * Holds Signoff schemas.
	 */
	private Set<Signoffschema> signoffschemas;

	/**
	 * Holds Schedules.
	 */
	private Set<Schedule> schedules;

	/**
	 * Holds Setups.
	 */
	private Set<Setup> setups;

	/**
	 * Hold Attachments.
	 */
	private Set<Attachment> attachments;

	/**
	 * Holds Steps.
	 */
	private Set<Step> steps;

	/** persistent field */
    private Set statuses;
	
	/**
	 * Holds Execution schedules.
	 */
	private Set<Executionschedule> executionschedules;

	/**
	 * Holds Hierarchy admins By Employee id.
	 */
	private Set<Hierarchyadmin> hierarchyadminsByEmployeeid;

	/**
	 * Holds Hierarchy admins By Hierarchy admin.
	 */
	private Set<Hierarchyadmin> hierarchyadminsByHierarchyadmin;

	/**
	 * Holds Links.
	 */
	private Set<Link> links;

	/**
	 * Holds Hierarchy elements.
	 */
	private Set<Hierarchyelement> hierarchyelements;

	/**
	 * Holds Translations.
	 */
	private Set<Translation> translations;

	/**
	 * Hold Control types.
	 */
	private Set<Controltype> controltypes;

	/**
	 * Hold Application role Employees By Employe.
	 */
	private Set<Applicationroleemployee> applicationroleemployeesByEmployee;

	/**
	 * Holds Application role employees By Last updated.
	 */
	private Set<Applicationroleemployee> applicationroleemployeesByLastupdatedby;

	/**
	 * Holds Goals control objectives.
	 */
	private Set<Goalscontrolobjective> goalscontrolobjectives;

	/**
	 * Holds AoR owners By Aorowne.
	 */
	private Set<Aorowner> aorownersByAorowner;

	/**
	 * Holds AoR owners By Last updated .
	 */
	private Set<Aorowner> aorownersByLastupdatedby;

	/**
	 * Holds Hierarchy types.
	 */
	private Set<Hierarchytype> hierarchytypes;

	/**
	 * Holds controls.
	 */
	private Set<Control> controls;
	
	/**
	 * Holds Employees Leaving.
	 */
	private Set<EmployeeLeaving> employeLeavingUpdatedby;
	
	/**
	 * Holds Employee Reassignment.
	 */
	private Set<EmployeeReassignment>employeeReassignmentUpdatedby;
	
	
	/**
	 * Holds Employees Leaving.
	 */
	private Set<EmployeeLeaving> employeeLeaving;
	
	/**
	 * Holds Employee Reassignments.
	 */
	private Set<EmployeeReassignment> employeeReassignment;

	/**
	 * Hold Controlrfcs.
	 */
	private Set<Controlrfc> controlrfcs;

	/**
	 * Holds Policies.
	 */
	private Set<Policy> policies;

	/**
	 * Holds Rolling signoffs.
	 */
	private Set<Rollingsignoff> rollingsignoffs;

	/**
	 * Holds Persistent field .
	 */
	private Set<Controlobjective> controlobjectives;

	/**
	 * Holds Executor pids By Employeeid.
	 */
	private Set<Executorpid> executorpidsByEmployeeid;

	/**
	 * Holds Executor pids By Last updated.
	 */
	private Set<Executorpid> executorpidsByLastupdatedby;

	/**
	 * Holds Escalations.
	 */
	private Set<Escalation> escalations;

	/**
	 * Holds Support schemas.
	 */
	private Set<Supportschema> supportschemas;

	/**
	 * Holds Attachment types.
	 */
	private Set<Attachmenttype> attachmenttypes;

	/** persistent field */
    private Set controlreferences;
    
	/**
	 * Holds Multi links.
	 */
	private Set<Multilink> multilinks;

	/**
	 * Holds Designees By Designee.
	 */
	private Set<Designee> designeesByDesignee;

	/**
	 * Holds Designees By Last updated.
	 */
	private Set<Designee> designeesByLastupdatedby;

	/**
	 * Hold Executoraors.
	 */
	private Set<Executoraor> executoraors;

	/**
	 * Holds Schedule adhoc dates .
	 */
	private Set<Scheduleadhocdate> scheduleadhocdates;

	/**
	 * Holds User profiles By Last updated.
	 */
	private Set<Userprofile> userprofilesByLastupdatedby;

	/**
	 * Holds Hierarchy roles.
	 */
	private Set<Hierarchyrole> hierarchyroles;

	/**
	 * Holds Controllers.
	 */
	private Set<Controller> controllers;

	/**
	 * Holds Execution schemas.
	 */
	private Set<Executionschema> executionschemas;

	/**
	 * Holds Qa schemas.
	 */
	private Set<Qaschema> qaschemas;

	/**
	 * Holds Goals By Lastupdated.
	 */
	private Set<Goal> goalsByLastupdatedby;

	/**
	 * Holds Goals By Goalowner.
	 */
	private Set<Goal> goalsByGoalowner;

	/**
	 * Holds Goals policies.
	 */
	private Set<Goalspolicy> goalspolicies;

	/**
	 * Holds rfcs.
	 */
	private Set<Rfc> rfcs;

	/**
	 * Holds Schedule whens.
	 */
	private Set<Schedulewhen> schedulewhens;

	/**
	 * Holds Executors.
	 */
	private Set<Executor> executors;

	/**
	 * Holds Shared controls.
	 */
	private Set<Sharedcontrol> sharedcontrols;

	/**
	 * Holds Hierarchies By Last updated.
	 */
	private Set<Hierarchy> hierarchiesByLastupdatedby;

	/**
	 * Holds Hierarchies By Hierarchy owner.
	 */
	private Set<Hierarchy> hierarchiesByHierarchyowner;
	
	/**
	 * Holds user profile for the employee
	 */
	private Userprofile userProfile;
	
	/**
	 *Holds Lastupdatedby for the employeeroleelements. 
	 */
	private Set<Employeeroleelement> employeeroleelementsByLastupdatedby;

	/**
	 *Holds Employeeid for the employeeroleelements. 
	 */
	private Set<Employeeroleelement> employeeroleelementsByEmployeeid;

	/**
	 *Holds hierarchyroleelements. 
	 */
	private Set<Hierarchyroleelement> hierarchyroleelements;
	
	/**
	 * Holds executiontasksByExecutor
	 */
	private Set<Executiontask> executiontasksByExecutor;
	
	/** persistent field */
    private Set periodtypes;
    
    /** persistent field */
    private Set periodtypeconversions;
    
    /** persistent field */
    private Set reminders;
    
    /**
	 * Holds signoff executiontasks
	 */
	private Set<Executiontask> signoffexecutiontasks;
	
	/**
	 * Holds CCElementType.
	 */
	private Set<CCElementType> elementTypesByLastUpdatedBy;
	
	/**
	 * Holds CCFieldValue.
	 */
	private Set<CCFieldValue> fieldValuesByLastUpdatedBy;
	
	/**
	 * Holds CCGeoLocation.
	 */
	private Set<CCGeoLocation> geoLocationsByLastUpdatedBy;
	
	/**
	 * Holds CCGeoLocSection.
	 */
	private Set<CCGeoLocSection> geoLocSectionsByLastUpdatedBy;
	
	/**
	 * Holds ControlColElementControl.
	 */
	private Set<ControlColElementControl> controlColElementControlsByLastUpdatedBy;
	
	/**
	 * Holds ControlCollection.
	 */
	private Set<ControlCollection> controlCollectionsByLastUpdatedBy;
	
	/**
	 * Holds ControlCollectionAssignment.
	 */
	private Set<ControlCollectionAssignment> controlCollectionAssignmentsByLastUpdatedBy;
	
	/**
	 * Holds ControlCollectionSection.
	 */
	private Set<ControlCollectionSection> controlCollectionSectionsByLastUpdatedBy;
	
	/**
	 * Holds ControlCollectionReport.
	 */
	private Set<ControlCollectionReport> controlCollectionReportsByLastUpdatedBy;
	
	/**
	 * Holds ControlCollection.
	 */
	private Set<ControlCollection> controlCollectionCreators;
	
	/**
	 * Holds ControlCollectionAssignment. 
	 */
	private Set<ControlCollectionAssignment> controlCollectionAssignmentEmployee;
	
	/**
	 * Holds ControlCollectionField.
	 */
	private Set<ControlCollectionField> controlCollectionFieldByLastUpdatedBy;
	
	/**
	 * Holds ControlCollectionElement.
	 */
	private Set<ControlCollectionElement> controlCollectionElementByLastUpdatedBy;
	
	/**
	 * Holds ControlCollectionReport.
	 */
	private Set<ControlCollectionReport> controlCollectionReportEmployee;
	
	/**
	 * Holds RemWarEscOtherRecipient.
	 */
	private Set<RemWarEscOtherRecipient> remWarEscOtherRecipient;
	
	/**
	 * Holds RemWarEscOtherRecipient.
	 */
	private Set<RemWarEscOtherRecipient> remWarEscOtherRecipientId;
	
	/**
	 * Holds RemWarEscOtherRecipient.
	 */
	private Set<ControlRemWarEsc> controlRemWarEsc;
	/**
	 * Holds referencecodes.
	 */
	private Set<Referencecode> referenceCode;
	
	/**
	 * Holds referencecodegroups.
	 */
	private Set<Referencecodegroup> referenceCodeGroups;
	
	/**
	 * Holds default constructor.
	 */
	public Employee() {
	}

	/**
	 * get employee id.
	 * 
	 * @return employeeid Employee id
	 */
	public Integer getEmployeeid() {
		return this.employeeid;
	}

	/**
	 * set employee id.
	 * 
	 * @param employeeid
	 *            Employee id
	 */
	public void setEmployeeid(Integer employeeid) {
		this.employeeid = employeeid;
	}

	/**
	 * Get Pid .
	 * 
	 * @return pid PID
	 */
	public String getPid() {
		return this.pid;
	}

	/**
	 * set Pid.
	 * 
	 * @param pid
	 *            pid
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * get Personal title.
	 * 
	 * @return personaltitle Personal title
	 */
	public String getPersonaltitle() {
		return this.personaltitle;
	}

	/**
	 * set Personal title.
	 * 
	 * @param personaltitle
	 *            personal title
	 */
	public void setPersonaltitle(String personaltitle) {
		this.personaltitle = personaltitle;
	}

	/**
	 * get Firstname.
	 * 
	 * @return firstname Firstname
	 */
	public String getFirstname() {
		return this.firstname;
	}

	/**
	 * set Firstname.
	 * 
	 * @param firstname
	 *            first name
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * get Lastname.
	 * 
	 * @return lastname Lastname
	 */
	public String getLastname() {
		return this.lastname;
	}

	/**
	 * set Lastname.
	 * 
	 * @param lastname
	 *            last name
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * get Instradierung.
	 * 
	 * @return instradierung Instradierung
	 */
	public String getInstradierung() {
		return this.instradierung;
	}

	/**
	 * set Instradierung.
	 * 
	 * @param instradierung
	 *            Instradierung
	 */
	public void setInstradierung(String instradierung) {
		this.instradierung = instradierung;
	}

	/**
	 * get Department.
	 * 
	 * @return department getDepartment
	 */
	public String getDepartment() {
		return this.department;
	}

	/**
	 * set Department.
	 * 
	 * @param department
	 *            department
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

  /**
   * get Business Unit.
   * 
   * @return department Business Unit
   */
  public String getBu() {
    return this.bu;
  }

  /**
   * set Business Unit.
   * 
   * @param bu
   *            Business Unit
   */
  public void setBu(String bu) {
    this.bu = bu;
  }

  /**
	 * get Division.
	 * 
	 * @return division Division
	 */
	public String getDivision() {
		return this.division;
	}

	/**
	 * set Division.
	 * 
	 * @param division
	 *            division
	 */
	public void setDivision(String division) {
		this.division = division;
	}

	/**
	 * get Mail.
	 * 
	 * @return mail Mail
	 */
	public String getMail() {
		return this.mail;
	}

	/**
	 * set Mail.
	 * 
	 * @param mail
	 *            mail
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * get Country.
	 * 
	 * @return country Country
	 */
	public String getCountry() {
		return this.country;
	}

	/**
	 * set Country.
	 * 
	 * @param country
	 *            country
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * get Address.
	 * 
	 * @return address Address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * se tAddress.
	 * 
	 * @param address
	 *            address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * get Lang.
	 * 
	 * @return lang Lang
	 */
	public String getLang() {
		return this.lang;
	}

	/**
	 * set Lang.
	 * 
	 * @param lang
	 *            lang
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * get Telephone.
	 * 
	 * @return telephone Telephone
	 */
	public String getTelephone() {
		return this.telephone;
	}

	/**
	 * set Telephone.
	 * 
	 * @param telephone
	 *            telephone
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * get Active.
	 * 
	 * @return active Active
	 */
	public String getActive() {
		return this.active;
	}

	/**
	 * set Active.
	 * 
	 * @param active
	 *            active
	 */
	public void setActive(String active) {
		this.active = active;
	}

	/**
	 * get Supervisor pid.
	 * 
	 * @return supervisorpid Supervisor pid
	 */
	public String getSupervisorpid() {
		return this.supervisorpid;
	}

	/**
	 * set Supervisor pid.
	 * 
	 * @param supervisorpid
	 *            supervisor pid
	 */
	public void setSupervisorpid(String supervisorpid) {
		this.supervisorpid = supervisorpid;
	}

	/**
	 * get Batcho date.
	 * 
	 * @return batchodate Batcho date
	 */
	public Date getBatchodate() {
		return this.batchodate;
	}

	/**
	 * set Batcho date.
	 * 
	 * @param batchodate
	 *            batcho date
	 */
	public void setBatchodate(Date batchodate) {
		this.batchodate = batchodate;
	}
	
	/**
	 * @return Returns the lastworkingday.
	 */
	public Date getLastworkingday() {
	
		return lastworkingday;
	}

	
	/**
	 * @param lastworkingday The lastworkingday to set.
	 */
	public void setLastworkingday(Date lastworkingday) {
	
		this.lastworkingday = lastworkingday;
	}

	/**
	 * get Create date.
	 * 
	 * @return createdate Create date
	 */
	public Timestamp getCreatedate() {
		return this.createdate;
	}

	/**
	 * set Create date.
	 * 
	 * @param createdate
	 *            create date
	 */
	public void setCreatedate(Timestamp createdate) {
		this.createdate = createdate;
	}

	/**
	 * get Last update.
	 * 
	 * @return lastupdate Last update
	 */
	public Timestamp getLastupdate() {
		return this.lastupdate;
	}

	/**
	 * set Last update.
	 * 
	 * @param lastupdate
	 *            last update
	 */
	public void setLastupdate(Timestamp lastupdate) {
		this.lastupdate = lastupdate;
	}

	/**
	 * get Last update employee id.
	 * 
	 * @return lastupdateemployeeid
	 */
	public BigDecimal getLastupdateemployeeid() {
		return this.lastupdateemployeeid;
	}

	/**
	 * set Last update employee id.
	 * 
	 * @param lastupdateemployeeid
	 *            last update employee id
	 */
	public void setLastupdateemployeeid(BigDecimal lastupdateemployeeid) {
		this.lastupdateemployeeid = lastupdateemployeeid;
	}

	/**
	 * get Signoff ratings.
	 * 
	 * @return signoffratings
	 */
	public Set<Rating> getRatings() {
		return this.ratings;
	}

	/**
	 * set Signoff ratings.
	 * 
	 * @param signoffratings
	 *            signoff ratings
	 */
	public void setRatings(Set<Rating> signoffratings) {
		this.ratings = signoffratings;
	}

	/**
	 * Returns the ratinggroupratings.
	 *
	 * @return the ratinggroupratings
	 */
	public Set<Rating> getRatinggroupratings() {
		return ratinggroupratings;
	}

	/**
	 * Sets the ratinggroupratings.
	 *
	 * @param ratinggroupratings the ratinggroupratings to set
	 */
	public void setRatinggroupratings(Set<Rating> ratinggroupratings) {
		this.ratinggroupratings = ratinggroupratings;
	}

	/**
	 * Returns the ratinggroups.
	 *
	 * @return the ratinggroups
	 */
	public Set<Rating> getRatinggroups() {
		return ratinggroups;
	}

	/**
	 * Sets the ratinggroups.
	 *
	 * @param ratinggroups the ratinggroups to set
	 */
	public void setRatinggroups(Set<Rating> ratinggroups) {
		this.ratinggroups = ratinggroups;
	}

	/**
	 * get Execution tasks.
	 * 
	 * @return executiontasks Execution tasks
	 */
	public Set<Executiontask> getExecutiontasks() {
		return this.executiontasks;
	}

	/**
	 * set Execution tasks.
	 * 
	 * @param executiontasks
	 *            execution tasks
	 */
	public void setExecutiontasks(Set<Executiontask> executiontasks) {
		this.executiontasks = executiontasks;
	}

	/**
	 * get Accessaors.
	 * 
	 * @return accessaors
	 */
	public Set<Accessaor> getAccessaors() {
		return this.accessaors;
	}

	/**
	 * set Accessaors.
	 * 
	 * @param accessaors
	 *            accessaors
	 */
	public void setAccessaors(Set<Accessaor> accessaors) {
		this.accessaors = accessaors;
	}

	/**
	 * get Signoff schemas.
	 * 
	 * @return signoff schemas
	 */
	public Set<Signoffschema> getSignoffschemas() {
		return this.signoffschemas;
	}

	/**
	 * set Signoff schemas.
	 * 
	 * @param signoffschemas
	 *            signoff schemas
	 */
	public void setSignoffschemas(Set<Signoffschema> signoffschemas) {
		this.signoffschemas = signoffschemas;
	}

	/**
	 * get Schedules.
	 * 
	 * @return schedules Schedules
	 */
	public Set<Schedule> getSchedules() {
		return this.schedules;
	}

	/**
	 * set Schedules.
	 * 
	 * @param schedules
	 *            schedules
	 */
	public void setSchedules(Set<Schedule> schedules) {
		this.schedules = schedules;
	}

	/**
	 * get Setups.
	 * 
	 * @return setups
	 */
	public Set<Setup> getSetups() {
		return this.setups;
	}

	/**
	 * set Setups.
	 * 
	 * @param setups
	 *            setups
	 */
	public void setSetups(Set<Setup> setups) {
		this.setups = setups;
	}

	/**
	 * get Attachments.
	 * 
	 * @return attachments Attachments
	 */
	public Set<Attachment> getAttachments() {
		return this.attachments;
	}

	/**
	 * set Attachments.
	 * 
	 * @param attachments
	 *            Attachments
	 */
	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * get Steps.
	 * 
	 * @return steps
	 */
	public Set<Step> getSteps() {
		return this.steps;
	}

	/**
	 * set Steps.
	 * 
	 * @param steps
	 *            steps
	 */
	public void setSteps(Set<Step> steps) {
		this.steps = steps;
	}

	 /**
	  * Provides the status
	 * @return the status
	 */
	public Set getStatuses() {
		return this.statuses;
	}

	/**
	 * Sets the status
	 * @param statuses all the status
	 */
	public void setStatuses(Set statuses) {
		this.statuses = statuses;
	}

	/**
	 * get Execution schedules.
	 * 
	 * @return execution schedules
	 */
	public Set<Executionschedule> getExecutionschedules() {
		return this.executionschedules;
	}

	/**
	 * set Execution schedules.
	 * 
	 * @param executionschedules
	 *            execution schedules
	 */
	public void setExecutionschedules(Set<Executionschedule> executionschedules) {
		this.executionschedules = executionschedules;
	}

	/**
	 * get Hierarchy admins By Employeeid.
	 * 
	 * @return hierarchyadminsByEmployeeid Hierarchy admins By Employeeid.
	 */
	public Set<Hierarchyadmin> getHierarchyadminsByEmployeeid() {
		return this.hierarchyadminsByEmployeeid;
	}

	/**
	 * set Hierarchy admins By Employeeid.
	 * 
	 * @param hierarchyadminsByEmployeeid
	 *            hierarchy admins By Employee id
	 */
	public void setHierarchyadminsByEmployeeid(Set<Hierarchyadmin> hierarchyadminsByEmployeeid) {
		this.hierarchyadminsByEmployeeid = hierarchyadminsByEmployeeid;
	}

	/**
	 * get Hierarchy admins By Hierarchy admin.
	 * 
	 * @return hierarchyadminsByHierarchyadmin Hierarchy admins By Hierarchy
	 *         admin.
	 */
	public Set<Hierarchyadmin> getHierarchyadminsByHierarchyadmin() {
		return this.hierarchyadminsByHierarchyadmin;
	}

	/**
	 * set Hierarchy admins By Hierarchy admin.
	 * 
	 * @param hierarchyadminsByHierarchyadmin
	 *            Hierarchy admins By Hierarchy admin.
	 */
	public void setHierarchyadminsByHierarchyadmin(
			Set<Hierarchyadmin> hierarchyadminsByHierarchyadmin) {
		this.hierarchyadminsByHierarchyadmin = hierarchyadminsByHierarchyadmin;
	}

	/**
	 * get Links.
	 * 
	 * @return links Links
	 */
	public Set<Link> getLinks() {
		return this.links;
	}

	/**
	 * set Links.
	 * 
	 * @param links
	 *            links
	 */
	public void setLinks(Set<Link> links) {
		this.links = links;
	}

	/**
	 * get Hierarchy elements.
	 * 
	 * @return hierarchy elements
	 */
	public Set<Hierarchyelement> getHierarchyelements() {
		return this.hierarchyelements;
	}

	/**
	 * set Hierarchy elements.
	 * 
	 * @param hierarchyelements
	 *            hierarchy elements
	 */
	public void setHierarchyelements(Set<Hierarchyelement> hierarchyelements) {
		this.hierarchyelements = hierarchyelements;
	}

	/**
	 * get Translations.
	 * 
	 * @return translations
	 */
	public Set<Translation> getTranslations() {
		return this.translations;
	}

	/**
	 * set Translations.
	 * 
	 * @param translations
	 *            translations
	 */
	public void setTranslations(Set<Translation> translations) {
		this.translations = translations;
	}

	/**
	 * get Controltypes.
	 * 
	 * @return controltypes
	 */
	public Set<Controltype> getControltypes() {
		return this.controltypes;
	}

	/**
	 * set Controltypes.
	 * 
	 * @param controltypes
	 *            controltypes
	 */
	public void setControltypes(Set<Controltype> controltypes) {
		this.controltypes = controltypes;
	}

	/**
	 * get Application role employees By Employee.
	 * 
	 * @return applicationroleemployeesByEmployee pplication role employees By
	 *         Employee.
	 */
	public Set<Applicationroleemployee> getApplicationroleemployeesByEmployee() {
		return this.applicationroleemployeesByEmployee;
	}

	/**
	 * set Application role employees By Employee.
	 * 
	 * @param applicationroleemployeesByEmployee
	 *            Application role employees By Employee
	 */
	public void setApplicationroleemployeesByEmployee(
			Set<Applicationroleemployee> applicationroleemployeesByEmployee) {
		this.applicationroleemployeesByEmployee = applicationroleemployeesByEmployee;
	}

	/**
	 * get Application role employees By Las tupdated .
	 * 
	 * @return applicationroleemployeesByLastupdatedby Application role
	 *         employees By Las tupdated
	 */
	public Set<Applicationroleemployee> getApplicationroleemployeesByLastupdatedby() {
		return this.applicationroleemployeesByLastupdatedby;
	}

	/**
	 * set Application role employees By Last updated .
	 * 
	 * @param applicationroleemployeesByLastupdatedby
	 *            Application role employees By Last updated
	 */
	public void setApplicationroleemployeesByLastupdatedby(
			Set<Applicationroleemployee> applicationroleemployeesByLastupdatedby) {
		this.applicationroleemployeesByLastupdatedby = applicationroleemployeesByLastupdatedby;
	}

	/**
	 * get Goals control objectives.
	 * 
	 * @return goalscontrolobjectives Goals control objectives.
	 */
	public Set<Goalscontrolobjective> getGoalscontrolobjectives() {
		return this.goalscontrolobjectives;
	}

	/**
	 * set Goals control objectives.
	 * 
	 * @param goalscontrolobjectives
	 *            Goals control objectives.
	 */
	public void setGoalscontrolobjectives(Set<Goalscontrolobjective> goalscontrolobjectives) {
		this.goalscontrolobjectives = goalscontrolobjectives;
	}

	/**
	 * get AoR owners By AoR owner.
	 * 
	 * @return aorownersByAorowner
	 */
	public Set<Aorowner> getAorownersByAorowner() {
		return this.aorownersByAorowner;
	}

	/**
	 * set AoR owners By AoRowner.
	 * 
	 * @param aorownersByAorowner
	 *            AoR owners By AoRowner
	 */
	public void setAorownersByAorowner(Set<Aorowner> aorownersByAorowner) {
		this.aorownersByAorowner = aorownersByAorowner;
	}

	/**
	 * get AoR owners By Last updated .
	 * 
	 * @return aorownersByLastupdatedby AoR owners By Last updated
	 */
	public Set<Aorowner> getAorownersByLastupdatedby() {
		return this.aorownersByLastupdatedby;
	}

	/**
	 * set AoR owners By Last updated.
	 * 
	 * @param aorownersByLastupdatedby
	 *            AoR owners By Last updated
	 */
	public void setAorownersByLastupdatedby(Set<Aorowner> aorownersByLastupdatedby) {
		this.aorownersByLastupdatedby = aorownersByLastupdatedby;
	}

	/**
	 * get Hierarchy types.
	 * 
	 * @return hierarchytypes Hierarchy types.
	 */
	public Set<Hierarchytype> getHierarchytypes() {
		return this.hierarchytypes;
	}

	/**
	 * set Hierarchy types.
	 * 
	 * @param hierarchytypes
	 *            Hierarchy types.
	 */
	public void setHierarchytypes(Set<Hierarchytype> hierarchytypes) {
		this.hierarchytypes = hierarchytypes;
	}

	/**
	 * get Controls.
	 * 
	 * @return controls Controls
	 */
	public Set<Control> getControls() {
		return this.controls;
	}

	/**
	 * set Controls.
	 * 
	 * @param controls
	 *            controls
	 */
	public void setControls(Set<Control> controls) {
		this.controls = controls;
	}

	/**
	 * get Control rfcs.
	 * 
	 * @return controlrfcs Control rfcs.
	 */
	public Set<Controlrfc> getControlrfcs() {
		return this.controlrfcs;
	}

	/**
	 * set Control rfcs.
	 * 
	 * @param controlrfcs
	 *            control rfcs
	 */
	public void setControlrfcs(Set<Controlrfc> controlrfcs) {
		this.controlrfcs = controlrfcs;
	}

	/**
	 * get Policies.
	 * 
	 * @return policies
	 */
	public Set<Policy> getPolicies() {
		return this.policies;
	}

	/**
	 * set Policies.
	 * 
	 * @param policies
	 *            policies
	 */
	public void setPolicies(Set<Policy> policies) {
		this.policies = policies;
	}

	/**
	 * get Rolling signoffs.
	 * 
	 * @return rollingsignoffs Rolling signoffs.
	 */
	public Set<Rollingsignoff> getRollingsignoffs() {
		return this.rollingsignoffs;
	}

	/**
	 * set Rolling signoffs.
	 * 
	 * @param rollingsignoffs
	 *            olling signoffs.
	 */
	public void setRollingsignoffs(Set<Rollingsignoff> rollingsignoffs) {
		this.rollingsignoffs = rollingsignoffs;
	}

	/**
	 * get Control objectives.
	 * 
	 * @return controlobjectives Control objectives
	 */
	public Set<Controlobjective> getControlobjectives() {
		return this.controlobjectives;
	}

	/**
	 * set Control objectives.
	 * 
	 * @param controlobjectives
	 *            Control objectives
	 */
	public void setControlobjectives(Set<Controlobjective> controlobjectives) {
		this.controlobjectives = controlobjectives;
	}

	/**
	 * get Executor pids By Employee id.
	 * 
	 * @return executorpidsByEmployeeid Executor pids By Employee id.
	 */
	public Set<Executorpid> getExecutorpidsByEmployeeid() {
		return this.executorpidsByEmployeeid;
	}

	/**
	 * set Executor pids By Employee id.
	 * 
	 * @param executorpidsByEmployeeid
	 *            Executor pids By Employee id
	 */
	public void setExecutorpidsByEmployeeid(Set<Executorpid> executorpidsByEmployeeid) {
		this.executorpidsByEmployeeid = executorpidsByEmployeeid;
	}

	/**
	 * get Executor pids By Last updated .
	 * 
	 * @return executorpidsByLastupdatedby Executor pids By Last updated
	 */
	public Set<Executorpid> getExecutorpidsByLastupdatedby() {
		return this.executorpidsByLastupdatedby;
	}

	/**
	 * set Executor pids By Last updated .
	 * 
	 * @param executorpidsByLastupdatedby
	 *            Executor pids By Last updated
	 */
	public void setExecutorpidsByLastupdatedby(Set<Executorpid> executorpidsByLastupdatedby) {
		this.executorpidsByLastupdatedby = executorpidsByLastupdatedby;
	}

	/**
	 * get Escalations.
	 * 
	 * @return escalations Escalations.
	 */
	public Set<Escalation> getEscalations() {
		return this.escalations;
	}

	/**
	 * set Escalations.
	 * 
	 * @param escalations
	 *            escalations
	 */
	public void setEscalations(Set<Escalation> escalations) {
		this.escalations = escalations;
	}

	/**
	 * get Support schemas.
	 * 
	 * @return supportschemas Support schemas.
	 */
	public Set<Supportschema> getSupportschemas() {
		return this.supportschemas;
	}

	/**
	 * set Support schemas.
	 * 
	 * @param supportschemas
	 *            Support schemas.
	 */
	public void setSupportschemas(Set<Supportschema> supportschemas) {
		this.supportschemas = supportschemas;
	}

	/**
	 * get Attachment types.
	 * 
	 * @return attachmenttypes Attachment types.
	 */
	public Set<Attachmenttype> getAttachmenttypes() {
		return this.attachmenttypes;
	}

	/**
	 * set Attachment types.
	 * 
	 * @param attachmenttypes
	 *            Attachment types.
	 */
	public void setAttachmenttypes(Set<Attachmenttype> attachmenttypes) {
		this.attachmenttypes = attachmenttypes;
	}
	
	/**
	 * Provides all control references of this user
	 * 
	 * @return all control references of this user
	 */
	public Set getControlreferences() {
		return this.controlreferences;
	}

	/**
	 * Sets all control references of this user
	 * 
	 * @param controlreferences
	 *            all control references of this user
	 */
	public void setControlreferences(Set controlreferences) {
		this.controlreferences = controlreferences;
	}
	
	/**
	 * get Multi links.
	 * 
	 * @return multilinks Multi links
	 */
	public Set<Multilink> getMultilinks() {
		return this.multilinks;
	}

	/**
	 * set Multi links.
	 * 
	 * @param multilinks
	 *            multi links
	 */
	public void setMultilinks(Set<Multilink> multilinks) {
		this.multilinks = multilinks;
	}

	/**
	 * get Designees By Designee.
	 * 
	 * @return designeesByDesignee Designees By Designee
	 */
	public Set<Designee> getDesigneesByDesignee() {
		return this.designeesByDesignee;
	}

	/**
	 * set Designees By Designee.
	 * 
	 * @param designeesByDesignee
	 *            designees By Designee
	 */
	public void setDesigneesByDesignee(Set<Designee> designeesByDesignee) {
		this.designeesByDesignee = designeesByDesignee;
	}

	/**
	 * get Designees By Last updatedby.
	 * 
	 * @return designees By Last updatedby
	 */
	public Set<Designee> getDesigneesByLastupdatedby() {
		return this.designeesByLastupdatedby;
	}

	/**
	 * set Designees By Last updated .
	 * 
	 * @param designeesByLastupdatedby
	 *            designees By Last updatedby
	 */
	public void setDesigneesByLastupdatedby(Set<Designee> designeesByLastupdatedby) {
		this.designeesByLastupdatedby = designeesByLastupdatedby;
	}

	/**
	 * get Executoraors.
	 * 
	 * @return executoraors Executoraors
	 */
	public Set<Executoraor> getExecutoraors() {
		return this.executoraors;
	}

	/**
	 * set Executoraors.
	 * 
	 * @param executoraors
	 *            executoraors
	 */
	public void setExecutoraors(Set<Executoraor> executoraors) {
		this.executoraors = executoraors;
	}

	/**
	 * set Schedule adhoc dates.
	 * 
	 * @return scheduleadhocdates Schedule adhoc dates
	 */
	public Set<Scheduleadhocdate> getScheduleadhocdates() {
		return this.scheduleadhocdates;
	}

	/**
	 * set Schedul eadhoc dates.
	 * 
	 * @param scheduleadhocdates
	 *            schedule adhoc dates
	 */
	public void setScheduleadhocdates(Set<Scheduleadhocdate> scheduleadhocdates) {
		this.scheduleadhocdates = scheduleadhocdates;
	}

	/**
	 * get User profiles By Last updated .
	 * 
	 * @return userprofilesByLastupdatedby User profiles By Last updated
	 */
	public Set<Userprofile> getUserprofilesByLastupdatedby() {
		return this.userprofilesByLastupdatedby;
	}

	/**
	 * set User profiles By Last updated.
	 * 
	 * @param userprofilesByLastupdatedby
	 *            User profiles By Last updated
	 */
	public void setUserprofilesByLastupdatedby(Set<Userprofile> userprofilesByLastupdatedby) {
		this.userprofilesByLastupdatedby = userprofilesByLastupdatedby;
	}

	/**
	 * get Hierarchy roles.
	 * 
	 * @return hierarchyroles Hierarchy roles
	 */
	public Set<Hierarchyrole> getHierarchyroles() {
		return this.hierarchyroles;
	}

	/**
	 * set Hierarchy roles.
	 * 
	 * @param hierarchyroles
	 *            hierarchy roles
	 */
	public void setHierarchyroles(Set<Hierarchyrole> hierarchyroles) {
		this.hierarchyroles = hierarchyroles;
	}

	/**
	 * get Controllers.
	 * 
	 * @return controllers Controllers
	 */
	public Set<Controller> getControllers() {
		return this.controllers;
	}

	/**
	 * set Controllers.
	 * 
	 * @param controllers
	 *            controllers
	 */
	public void setControllers(Set<Controller> controllers) {
		this.controllers = controllers;
	}

	/**
	 * get Execution schemas.
	 * 
	 * @return executionschemas Execution schemas
	 */
	public Set<Executionschema> getExecutionschemas() {
		return this.executionschemas;
	}

	/**
	 * set Executionschemas.
	 * 
	 * @param executionschemas
	 *            executionschemas
	 */
	public void setExecutionschemas(Set<Executionschema> executionschemas) {
		this.executionschemas = executionschemas;
	}

	/**
	 * get Qa schemas.
	 * 
	 * @return qaschemas
	 */
	public Set<Qaschema> getQaschemas() {
		return this.qaschemas;
	}

	/**
	 * set Qaschemas.
	 * 
	 * @param qaschemas
	 *            qaschemas
	 */
	public void setQaschemas(Set<Qaschema> qaschemas) {
		this.qaschemas = qaschemas;
	}

	/**
	 * get Goals By Last updated .
	 * 
	 * @return goalsByLastupdatedby Goals By Last updated
	 */
	public Set<Goal> getGoalsByLastupdatedby() {
		return this.goalsByLastupdatedby;
	}

	/**
	 * set Goals By Last updated.
	 * 
	 * @param goalsByLastupdatedby
	 *            Goals By Last updated
	 */
	public void setGoalsByLastupdatedby(Set<Goal> goalsByLastupdatedby) {
		this.goalsByLastupdatedby = goalsByLastupdatedby;
	}

	/**
	 * get Goals By Goal owner.
	 * 
	 * @return goalsByGoalowner Goals By Goal owner
	 */
	public Set<Goal> getGoalsByGoalowner() {
		return this.goalsByGoalowner;
	}

	/**
	 * set Goals By Goal owner.
	 * 
	 * @param goalsByGoalowner
	 *            goalsByGoalowner
	 */
	public void setGoalsByGoalowner(Set<Goal> goalsByGoalowner) {
		this.goalsByGoalowner = goalsByGoalowner;
	}

	/**
	 * get Goals policies.
	 * 
	 * @return goalspolicies
	 */
	public Set<Goalspolicy> getGoalspolicies() {
		return this.goalspolicies;
	}

	/**
	 * set Goals policies.
	 * 
	 * @param goalspolicies
	 *            goals policies
	 */
	public void setGoalspolicies(Set<Goalspolicy> goalspolicies) {
		this.goalspolicies = goalspolicies;
	}

	/**
	 * get Rfcs.
	 * 
	 * @return rfcs
	 */
	public Set<Rfc> getRfcs() {
		return this.rfcs;
	}

	/**
	 * set Rfcs.
	 * 
	 * @param rfcs
	 *            rfcs
	 */
	public void setRfcs(Set<Rfc> rfcs) {
		this.rfcs = rfcs;
	}

	/**
	 * get Schedule whens.
	 * 
	 * @return schedulewhens Schedule whens
	 */
	public Set<Schedulewhen> getSchedulewhens() {
		return this.schedulewhens;
	}

	/**
	 * set Schedule whens.
	 * 
	 * @param schedulewhens
	 *            schedule whens
	 */
	public void setSchedulewhens(Set<Schedulewhen> schedulewhens) {
		this.schedulewhens = schedulewhens;
	}

	/**
	 * get Executors.
	 * 
	 * @return executors
	 */
	public Set<Executor> getExecutors() {
		return this.executors;
	}

	/**
	 * set Executors.
	 * 
	 * @param executors
	 *            executors
	 */
	public void setExecutors(Set<Executor> executors) {
		this.executors = executors;
	}

	/**
	 * get Shared controls.
	 * 
	 * @return sharedcontrols Shared controls
	 */
	public Set<Sharedcontrol> getSharedcontrols() {
		return this.sharedcontrols;
	}

	/**
	 * set Shared controls.
	 * 
	 * @param sharedcontrols
	 *            Shared controls.
	 */
	public void setSharedcontrols(Set<Sharedcontrol> sharedcontrols) {
		this.sharedcontrols = sharedcontrols;
	}

	/**
	 * get Hierarchies By Last updated.
	 * 
	 * @return hierarchiesByLastupdatedby Hierarchies By Last updated
	 */
	public Set<Hierarchy> getHierarchiesByLastupdatedby() {
		return this.hierarchiesByLastupdatedby;
	}

	/**
	 * set Hierarchies By Last updated.
	 * 
	 * @param hierarchiesByLastupdatedby
	 *            Hierarchies By Last updated
	 */
	public void setHierarchiesByLastupdatedby(Set<Hierarchy> hierarchiesByLastupdatedby) {
		this.hierarchiesByLastupdatedby = hierarchiesByLastupdatedby;
	}

	/**
	 * get Hierarchies By Hierarchy owner.
	 * 
	 * @return hierarchiesByHierarchyowner ierarchies By Hierarchy owner
	 */
	public Set<Hierarchy> getHierarchiesByHierarchyowner() {
		return this.hierarchiesByHierarchyowner;
	}

	/**
	 * set ierarchies By Hierarchy owner.
	 * 
	 * @param hierarchiesByHierarchyowner
	 *            hierarchies By Hierarchy owner
	 */
	public void setHierarchiesByHierarchyowner(Set<Hierarchy> hierarchiesByHierarchyowner) {
		this.hierarchiesByHierarchyowner = hierarchiesByHierarchyowner;
	}
	
	/**
	 * Get user profuile for employee
	 * @return userProfile user profuile
	 */
	public Userprofile getUserProfile() {
		return userProfile;
	}
	
	/**
	 * Set user profile for employee
	 * @param userProfile for employee
	 */
	public void setUserProfile(Userprofile userProfile) {
		this.userProfile = userProfile;
	}
	/**
	 * Get employeeroleelementsByLastupdatedby
	 * @return employeeroleelementsByLastupdatedby Set
	 */
	public Set<Employeeroleelement> getEmployeeroleelementsByLastupdatedby() {
		return this.employeeroleelementsByLastupdatedby;
	}
	/**
	 * Set employeeroleelementsByLastupdatedby
	 * @param employeeroleelementsByLastupdatedby Set
	 */
	public void setEmployeeroleelementsByLastupdatedby(
			Set<Employeeroleelement> employeeroleelementsByLastupdatedby) {
		this.employeeroleelementsByLastupdatedby = employeeroleelementsByLastupdatedby;
	}
	/**
	 * Get employeeroleelementsByEmployeeid
	 * @return employeeroleelementsByEmployeeid Set
	 */
	public Set<Employeeroleelement> getEmployeeroleelementsByEmployeeid() {
		return this.employeeroleelementsByEmployeeid;
	}
	/**
	 * Set employeeroleelementsByEmployeeid
	 * @param employeeroleelementsByEmployeeid Set
	 */
	public void setEmployeeroleelementsByEmployeeid(
			Set<Employeeroleelement> employeeroleelementsByEmployeeid) {
		this.employeeroleelementsByEmployeeid = employeeroleelementsByEmployeeid;
	}
	/**
	 * Get hierarchyroleelements
	 * @return hierarchyroleelements Set
	 */
	public Set<Hierarchyroleelement> getHierarchyroleelements() {
		return this.hierarchyroleelements;
	}
	/**
	 * Set hierarchyroleelements
	 * @param hierarchyroleelements Set
	 */
	public void setHierarchyroleelements(Set<Hierarchyroleelement> hierarchyroleelements) {
		this.hierarchyroleelements = hierarchyroleelements;
	}
		
	/**
	 * gets ExecutiontasksByExecutor
	 * @return executiontasksByExecutor
	 */
	public Set getExecutiontasksByExecutor() {
		return executiontasksByExecutor;
	}

	/**
	 * sets ExecutiontasksByExecutor
	 * @param executiontasksByExecutor Set
	 */
	public void setExecutiontasksByExecutor(Set<Executiontask> executiontasksByExecutor) {
		this.executiontasksByExecutor = executiontasksByExecutor;
	}

	/**
	 * provides String information of the object.
	 * 
	 * @return employeeid value
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this).append("employeeid", getEmployeeid())
				.toString();
	}

	/**
	 * Compares the objects for equality.
	 * 
	 * @param other
	 *            the object to be compared to
	 * @return boolean Boolean
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (!(other instanceof Employee)) {
			return false;
		}
		Employee castOther = (Employee) other;
		return new EqualsBuilder().append(this.getEmployeeid(),
				castOther.getEmployeeid()).isEquals();
	}

	/**
	 * Returns the hashcode of the object.
	 * 
	 * @return int hash code the hashcode
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder().append(getEmployeeid()).toHashCode();
	}

	/**
	 * get Employee Name.
	 * 
	 * @return employeeName employee Full Name
	 */
	public String getEmployeeName() {
		return this.getFirstname() + " " + this.getLastname() + " ("
				+ this.getInstradierung() + ")";
	}

	
	/**
	 * @return Returns the periodtypeconversions.
	 */
	public Set getPeriodtypeconversions() {
	
		return periodtypeconversions;
	}

	
	/**
	 * @param periodtypeconversions The periodtypeconversions to set.
	 */
	public void setPeriodtypeconversions(Set periodtypeconversions) {
	
		this.periodtypeconversions = periodtypeconversions;
	}

	
	/**
	 * @return Returns the periodtypes.
	 */
	public Set getPeriodtypes() {
	
		return periodtypes;
	}

	
	/**
	 * @param periodtypes The periodtypes to set.
	 */
	public void setPeriodtypes(Set periodtypes) {
	
		this.periodtypes = periodtypes;
	}

	
	/**
	 * @return Returns the reminders.
	 */
	public Set getReminders() {
	
		return reminders;
	}

	
	/**
	 * @param reminders The reminders to set.
	 */
	public void setReminders(Set reminders) {
	
		this.reminders = reminders;
	}

	/**
	 * @return Returns the notificationbatchs.
	 */
	public Set<Notificationbatch> getNotificationbatchs() {
		return notificationbatchs;
	}

	/**
	 * @param notificationbatchs The notificationbatchs to set.
	 */
	public void setNotificationbatchs(Set<Notificationbatch> notificationbatchs) {
		this.notificationbatchs = notificationbatchs;
	}

	/**
	 * @return Returns the notificationtypes.
	 */
	public Set<Notificationtype> getNotificationtypes() {
		return notificationtypes;
	}

	/**
	 * @param notificationtypes The notificationtypes to set.
	 */
	public void setNotificationtypes(Set<Notificationtype> notificationtypes) {
		this.notificationtypes = notificationtypes;
	}

	/**
	 * @return Returns the notifications.
	 */
	public Set<Notification> getNotifications() {
		return notifications;
	}

	/**
	 * @param notifications The notifications to set.
	 */
	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	/**
	 * @return Returns the notifyuser.
	 */
	public Set<Notification> getNotifyuser() {
		return notifyuser;
	}

	/**
	 * @param notifyuser The notifyuser to set.
	 */
	public void setNotifyuser(Set<Notification> notifyuser) {
		this.notifyuser = notifyuser;
	}

	/**
	 * @return Returns the poescalation.
	 */
	public Set<Poescalation> getPoescalation() {
		return poescalation;
	}

	/**
	 * @param poescalation The poescalation to set.
	 */
	public void setPoescalation(Set<Poescalation> poescalation) {
		this.poescalation = poescalation;
	}

	/**
	 * @return Returns the poschedule.
	 */
	public Set<Poschedule> getPoschedule() {
		return poschedule;
	}

	/**
	 * @param poschedule The poschedule to set.
	 */
	public void setPoschedule(Set<Poschedule> poschedule) {
		this.poschedule = poschedule;
	}

	/**
	 * @return Returns the poweroption.
	 */
	public Set<Poweroption> getPoweroption() {
		return poweroption;
	}

	/**
	 * @param poweroption The poweroption to set.
	 */
	public void setPoweroption(Set<Poweroption> poweroption) {
		this.poweroption = poweroption;
	}

	/**
	 * @return Returns the poescalations.
	 */
	public Set<Poescalation> getPoescalations() {
		return poescalations;
	}

	/**
	 * @param poescalations The poescalations to set.
	 */
	public void setPoescalations(Set<Poescalation> poescalations) {
		this.poescalations = poescalations;
	}

	/**
	 * @return Returns the poschedules.
	 */
	public Set<Poschedule> getPoschedules() {
		return poschedules;
	}

	/**
	 * @param poschedules The poschedules to set.
	 */
	public void setPoschedules(Set<Poschedule> poschedules) {
		this.poschedules = poschedules;
	}

	/**
	 * @return Returns the poweroptions.
	 */
	public Set<Poweroption> getPoweroptions() {
		return poweroptions;
	}

	/**
	 * @param poweroptions The poweroptions to set.
	 */
	public void setPoweroptions(Set<Poweroption> poweroptions) {
		this.poweroptions = poweroptions;
	}

/**
 * Returns the signoffexecutiontasks.
 *
 * @return the signoffexecutiontasks
 */
public Set<Executiontask> getSignoffexecutiontasks() {
	return signoffexecutiontasks;
}

/**
 * Sets the signoffexecutiontasks.
 *
 * @param signoffexecutiontasks the signoffexecutiontasks to set
 */
public void setSignoffexecutiontasks(Set<Executiontask> signoffexecutiontasks) {
	this.signoffexecutiontasks = signoffexecutiontasks;
}


/**
 * @return Returns the fieldValuesByLastUpdatedBy.
 */
public Set<CCFieldValue> getFieldValuesByLastUpdatedBy() {
	return fieldValuesByLastUpdatedBy;
}

/**
 * @param fieldValuesByLastUpdatedBy The fieldValuesByLastUpdatedBy to set.
 */
public void setFieldValuesByLastUpdatedBy(
		Set<CCFieldValue> fieldValuesByLastUpdatedBy) {
	this.fieldValuesByLastUpdatedBy = fieldValuesByLastUpdatedBy;
}

/**
 * @return the controlColElementControlsByLastUpdatedBy
 */
public Set<ControlColElementControl> getControlColElementControlsByLastUpdatedBy() {
	return controlColElementControlsByLastUpdatedBy;
}

/**
 * @param controlColElementControlsByLastUpdatedBy the controlColElementControlsByLastUpdatedBy to set
 */
public void setControlColElementControlsByLastUpdatedBy(
		Set<ControlColElementControl> controlColElementControlsByLastUpdatedBy) {
	this.controlColElementControlsByLastUpdatedBy = controlColElementControlsByLastUpdatedBy;
}

/**
 * @return the controlCollectionAssignmentEmployee
 */
public Set<ControlCollectionAssignment> getControlCollectionAssignmentEmployee() {
	return controlCollectionAssignmentEmployee;
}

/**
 * @param controlCollectionAssignmentEmployee the controlCollectionAssignmentEmployee to set
 */
public void setControlCollectionAssignmentEmployee(
		Set<ControlCollectionAssignment> controlCollectionAssignmentEmployee) {
	this.controlCollectionAssignmentEmployee = controlCollectionAssignmentEmployee;
}

/**
 * @return the controlCollectionAssignmentsByLastUpdatedBy
 */
public Set<ControlCollectionAssignment> getControlCollectionAssignmentsByLastUpdatedBy() {
	return controlCollectionAssignmentsByLastUpdatedBy;
}

/**
 * @param controlCollectionAssignmentsByLastUpdatedBy the controlCollectionAssignmentsByLastUpdatedBy to set
 */
public void setControlCollectionAssignmentsByLastUpdatedBy(
		Set<ControlCollectionAssignment> controlCollectionAssignmentsByLastUpdatedBy) {
	this.controlCollectionAssignmentsByLastUpdatedBy = controlCollectionAssignmentsByLastUpdatedBy;
}

/**
 * @return the controlCollectionCreators
 */
public Set<ControlCollection> getControlCollectionCreators() {
	return controlCollectionCreators;
}

/**
 * @param controlCollectionCreators the controlCollectionCreators to set
 */
public void setControlCollectionCreators(
		Set<ControlCollection> controlCollectionCreators) {
	this.controlCollectionCreators = controlCollectionCreators;
}

/**
 * @return the controlCollectionReportsByLastUpdatedBy
 */
public Set<ControlCollectionReport> getControlCollectionReportsByLastUpdatedBy() {
	return controlCollectionReportsByLastUpdatedBy;
}

/**
 * @param controlCollectionReportsByLastUpdatedBy the controlCollectionReportsByLastUpdatedBy to set
 */
public void setControlCollectionReportsByLastUpdatedBy(
		Set<ControlCollectionReport> controlCollectionReportsByLastUpdatedBy) {
	this.controlCollectionReportsByLastUpdatedBy = controlCollectionReportsByLastUpdatedBy;
}

/**
 * @return the controlCollectionsByLastUpdatedBy
 */
public Set<ControlCollection> getControlCollectionsByLastUpdatedBy() {
	return controlCollectionsByLastUpdatedBy;
}

/**
 * @param controlCollectionsByLastUpdatedBy the controlCollectionsByLastUpdatedBy to set
 */
public void setControlCollectionsByLastUpdatedBy(
		Set<ControlCollection> controlCollectionsByLastUpdatedBy) {
	this.controlCollectionsByLastUpdatedBy = controlCollectionsByLastUpdatedBy;
}

/**
 * @return the controlCollectionSectionsByLastUpdatedBy
 */
public Set<ControlCollectionSection> getControlCollectionSectionsByLastUpdatedBy() {
	return controlCollectionSectionsByLastUpdatedBy;
}

/**
 * @param controlCollectionSectionsByLastUpdatedBy the controlCollectionSectionsByLastUpdatedBy to set
 */
public void setControlCollectionSectionsByLastUpdatedBy(
		Set<ControlCollectionSection> controlCollectionSectionsByLastUpdatedBy) {
	this.controlCollectionSectionsByLastUpdatedBy = controlCollectionSectionsByLastUpdatedBy;
}

/**
 * @return Returns the elementTypesByLastUpdatedBy.
 */
public Set<CCElementType> getElementTypesByLastUpdatedBy() {
	return elementTypesByLastUpdatedBy;
}

/**
 * @param elementTypesByLastUpdatedBy The elementTypesByLastUpdatedBy to set.
 */
public void setElementTypesByLastUpdatedBy(
		Set<CCElementType> elementTypesByLastUpdatedBy) {
	this.elementTypesByLastUpdatedBy = elementTypesByLastUpdatedBy;
}

/**
 * @return Returns the controlCollectionFieldByLastUpdatedBy.
 */
public Set<ControlCollectionField> getControlCollectionFieldByLastUpdatedBy() {
	return controlCollectionFieldByLastUpdatedBy;
}

/**
 * @param controlCollectionFieldByLastUpdatedBy The controlCollectionFieldByLastUpdatedBy to set.
 */
public void setControlCollectionFieldByLastUpdatedBy(
		Set<ControlCollectionField> controlCollectionFieldByLastUpdatedBy) {
	this.controlCollectionFieldByLastUpdatedBy = controlCollectionFieldByLastUpdatedBy;
}

/**
 * @return Returns the controlCollectionElementByLastUpdatedBy.
 */
public Set<ControlCollectionElement> getControlCollectionElementByLastUpdatedBy() {
	return controlCollectionElementByLastUpdatedBy;
}

/**
 * @param controlCollectionElementByLastUpdatedBy The controlCollectionElementByLastUpdatedBy to set.
 */
public void setControlCollectionElementByLastUpdatedBy(
		Set<ControlCollectionElement> controlCollectionElementByLastUpdatedBy) {
	this.controlCollectionElementByLastUpdatedBy = controlCollectionElementByLastUpdatedBy;
}

/**
 * @return Returns the controlCollectionReportEmployee.
 */
public Set<ControlCollectionReport> getControlCollectionReportEmployee() {
	return controlCollectionReportEmployee;
}

/**
 * @param controlCollectionReportEmployee The controlCollectionReportEmployee to set.
 */
public void setControlCollectionReportEmployee(
		Set<ControlCollectionReport> controlCollectionReportEmployee) {
	this.controlCollectionReportEmployee = controlCollectionReportEmployee;
}

/**
 * @return Returns the geoLocationsByLastUpdatedBy.
 */
public Set<CCGeoLocation> getGeoLocationsByLastUpdatedBy() {
	return geoLocationsByLastUpdatedBy;
}

/**
 * @param geoLocationsByLastUpdatedBy The geoLocationsByLastUpdatedBy to set.
 */
public void setGeoLocationsByLastUpdatedBy(
		Set<CCGeoLocation> geoLocationsByLastUpdatedBy) {
	this.geoLocationsByLastUpdatedBy = geoLocationsByLastUpdatedBy;
}

/**
 * @return Returns the geoLocSectionsByLastUpdatedBy.
 */
public Set<CCGeoLocSection> getGeoLocSectionsByLastUpdatedBy() {
	return geoLocSectionsByLastUpdatedBy;
}

/**
 * @param geoLocSectionsByLastUpdatedBy The geoLocSectionsByLastUpdatedBy to set.
 */
public void setGeoLocSectionsByLastUpdatedBy(
		Set<CCGeoLocSection> geoLocSectionsByLastUpdatedBy) {
	this.geoLocSectionsByLastUpdatedBy = geoLocSectionsByLastUpdatedBy;
}

/**
* @return Returns the getEmployeeLeaving.
*/
public Set<EmployeeLeaving> getEmployeeLeaving() {
	return employeeLeaving;
}

/**
* @param employeeLeaving The employeeLeaving to set.
*/
public void setEmployeeLeaving(Set<EmployeeLeaving> employeeLeaving) {
	this.employeeLeaving = employeeLeaving;
}

/**
* @return Returns the employeLeavingsLastUpdatedBy.
*/

public Set<EmployeeLeaving> getEmployeLeavingUpdatedby() {
	return employeLeavingUpdatedby;
}
/**
* @param employeLeavingUpdatedby The employeLeavingLastUpdatedBy to set.
*/
public void setEmployeLeavingUpdatedby(
		Set<EmployeeLeaving> employeLeavingUpdatedby) {
	this.employeLeavingUpdatedby = employeLeavingUpdatedby;
}

/**
 * @return Returns the employeeReassignmentUpdatedby.
 */
public Set<EmployeeReassignment> getEmployeeReassignmentUpdatedby() {
	return employeeReassignmentUpdatedby;
}

/**
 * @param employeeReassignmentUpdatedby The employeeReassignmentUpdatedby to set.
 */
public void setEmployeeReassignmentUpdatedby(
		Set<EmployeeReassignment> employeeReassignmentUpdatedby) {
	this.employeeReassignmentUpdatedby = employeeReassignmentUpdatedby;
}

/**
 * @return Returns the employeeReassignment.
 */
public Set<EmployeeReassignment> getEmployeeReassignment() {
	return employeeReassignment;
}

/**
 * @param employeeReassignment The employeeReassignment to set.
 */
public void setEmployeeReassignment(
		Set<EmployeeReassignment> employeeReassignment) {
	this.employeeReassignment = employeeReassignment;
}

/**
 * @return Returns the controlRemWarEsc.
 */
public Set<ControlRemWarEsc> getControlRemWarEsc() {
	return controlRemWarEsc;
}

/**
 * @param controlRemWarEsc The controlRemWarEsc to set.
 */
public void setControlRemWarEsc(Set<ControlRemWarEsc> controlRemWarEsc) {
	this.controlRemWarEsc = controlRemWarEsc;
}

/**
 * @return Returns the remWarEscOtherRecipient.
 */
public Set<RemWarEscOtherRecipient> getRemWarEscOtherRecipient() {
	return remWarEscOtherRecipient;
}

/**
 * @param remWarEscOtherRecipient The remWarEscOtherRecipient to set.
 */
public void setRemWarEscOtherRecipient(
		Set<RemWarEscOtherRecipient> remWarEscOtherRecipient) {
	this.remWarEscOtherRecipient = remWarEscOtherRecipient;
}

/**
 * @return Returns the remWarEscOtherRecipientId.
 */
public Set<RemWarEscOtherRecipient> getRemWarEscOtherRecipientId() {
	return remWarEscOtherRecipientId;
}

/**
 * @param remWarEscOtherRecipientId The remWarEscOtherRecipientId to set.
 */
public void setRemWarEscOtherRecipientId(
		Set<RemWarEscOtherRecipient> remWarEscOtherRecipientId) {
	this.remWarEscOtherRecipientId = remWarEscOtherRecipientId;
}

/**
 * @return Returns the referenceCode.
 */
public Set<Referencecode> getReferenceCode() {
	return referenceCode;
}

/**
 * @param referenceCode The referenceCode to set.
 */
public void setReferenceCode(Set<Referencecode> referenceCode) {
	this.referenceCode = referenceCode;
}

/**
 * @return Returns the referenceCodeGroups.
 */
public Set<Referencecodegroup> getReferenceCodeGroups() {
	return referenceCodeGroups;
}

/**
 * @param referenceCodeGroups The referenceCodeGroups to set.
 */
public void setReferenceCodeGroups(Set<Referencecodegroup> referenceCodeGroups) {
	this.referenceCodeGroups = referenceCodeGroups;
}
}

/*
 * Check-In History: $Log: Employee.java,v $
 * Check-In History: Revision 1.1  2010/10/25 15:27:25  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.37  2010/06/09 15:02:33  g304578
 * Check-In History: changed for AR 1.5
 * Check-In History:
 * Check-In History: Revision 1.3  2010/06/02 15:30:50  163376
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.2  2010/06/01 12:27:38  196828
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.1  2010/05/27 07:03:23  153146
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.36  2010/03/10 09:43:08  f542625
 * Check-In History: code merging after 1.4.3 and AR 2 package 1
 * Check-In History:
 * Check-In History: Revision 1.31.4.2  2009/06/03 15:37:09  f542625
 * Check-In History: Removed the files for breakreport.
 * Check-In History:
 * Check-In History: Revision 1.31.4.1  2009/04/20 15:01:34  g703890
 * Check-In History: MICIS 1.4 : Deliverables for 20th April
 * Check-In History:
 * Check-In History: Revision 1.3  2009/04/17 08:04:45  163314
 * Check-In History: Micos1.4 For EmployeeReassignment table
 * Check-In History:
 * Check-In History: Revision 1.2  2009/04/08 11:32:54  120207
 * Check-In History: adde employee leaving
 * Check-In History:
 * Check-In History: Revision 1.1  2009/03/31 12:19:39  163314
 * Check-In History: Initial veersion - Micos1.4
 * Check-In History:
 * Check-In History: Revision 1.31  2009/01/08 09:40:56  g624982
 * Check-In History: Micos 1.3 : Formating & Code Review Incorporated.
 * Check-In History:
 * Check-In History: Revision 1.30  2008/11/24 07:10:05  g624982
 * Check-In History: Micos1.3: Codes for Control collections setup
 * Check-In History:
 * Check-In History: Revision 1.29  2008/11/21 13:04:51  g624982
 * Check-In History: Micos1.3 - Bean files for HibernateMapping
 * Check-In History:
 * Check-In History: Revision 1.28  2008/09/26 14:20:02  f542625
 * Check-In History: Changed for new column (SIGNOFFEMPLOYEEID and SIGNOFFCOMMENTDATE) in EXECUTIONTASK table.
 * Check-In History:
 * Check-In History: Revision 1.27  2008/09/18 08:14:52  f542625
 * Check-In History: Changed/added files for new database table Ratinggroup and Ratinggrouprating.
 * Check-In History:
 * Check-In History: Revision 1.26  2008/02/08 11:04:39  f544490
 * Check-In History: Updated the bean classes for break report module.
 * Check-In History:
 * Check-In History: Revision 1.25  2008/01/21 12:59:47  j014664
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.24  2007/12/24 11:29:11  j014664
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.23  2007/12/19 11:15:01  f599895
 * Check-In History: serial id changed
 * Check-In History:
 * Check-In History: Revision 1.22  2007/11/30 13:20:32  j014664
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.21  2007/11/28 16:03:04  j014656
 * Check-In History: updated hibernate mappings
 * Check-In History:
 * Check-In History: Revision 1.18  2007/11/21 13:19:29  j014656
 * Check-In History: updated hibernate mappings
 * Check-In History:
 * Check-In History: Revision 1.17  2007/11/17 13:07:17  j014664
 * Check-In History: *** empty log message ***
 * Check-In History:
 * Check-In History: Revision 1.8  2007/11/05 10:31:12  f259648
 * Check-In History: added BU and changes in supervisorid to pid
 * Check-In History:
 * Check-In History: Revision 1.7  2007/11/05 08:47:19  j013447
 * Check-In History: *** empty log message ***
 * Check-In History:
 */
