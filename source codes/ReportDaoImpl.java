/******************************************************************************
 * Copyright(c) 2003-2007 CREDIT SUISSE Financial Services. All Rights Reserved.
 *
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.csg.cs.core.base.exception.ExceptionContext;
import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.dao.ReportDao;
import com.csg.cs.micos.dao.helper.ReportDaoHelper;
import com.csg.cs.micos.entitybean.Businessdivision;
import com.csg.cs.micos.entitybean.Colclassificationbusdiv;
import com.csg.cs.micos.entitybean.Colclassificationsserv;
import com.csg.cs.micos.entitybean.Colclassifictionregion;
import com.csg.cs.micos.entitybean.Colisstask;
import com.csg.cs.micos.entitybean.Collection;
import com.csg.cs.micos.entitybean.Collectionclassification;
import com.csg.cs.micos.entitybean.Collectiontype;
import com.csg.cs.micos.entitybean.Contact;
import com.csg.cs.micos.entitybean.Distribution;
import com.csg.cs.micos.entitybean.Employee;
import com.csg.cs.micos.entitybean.Issue;
import com.csg.cs.micos.entitybean.Issuecodinggroup;
import com.csg.cs.micos.entitybean.Issuecreatorgroup;
import com.csg.cs.micos.entitybean.Issuetype;
import com.csg.cs.micos.entitybean.Multilink;
import com.csg.cs.micos.entitybean.Notification;
import com.csg.cs.micos.entitybean.Observation;
import com.csg.cs.micos.entitybean.Recommendation;
import com.csg.cs.micos.entitybean.Region;
import com.csg.cs.micos.entitybean.Sharedservices;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.DateUtil;
import com.csg.cs.micos.util.IssuesConstants;
import com.csg.cs.micos.util.MicosConstantValues;
import com.csg.cs.micos.util.SystemUtil;
import com.csg.cs.micos.util.MicosConstantValues.Status;

/**
 * 
 * <code>ReportDaoImpl</code> is used to provide the common methods for
 *  reports
 * 
 * @author Cognizant Technology Solutions

 * @author Last change by $Author: 153146 $
 * @version $Id: ReportDaoImpl.java,v 1.3 2010/09/27 07:56:06 153146 Exp $
 */
public class ReportDaoImpl extends BaseDaoImpl implements ReportDao {
	
	/**
	   * A logger instance with the fully qualified class name as logger name.
	   */
	private static final Logger LOGGER = LoggerHelper
	      .getLogger(ReportDaoImpl.class);
	
	/**
	 * fetch a collection details from a collection ID.
	 * @param collectionId  Integer
	 * @param loadalldata  true if lazy loading required
	 * @return Collection
	 * @throws MicosException Exception
	 */
	public Collection loadCollection(Integer collectionId, boolean loadalldata)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "loadCollection");

		Collection collection = null;
		EntityManager em = null;
		try {
			if (null != collectionId) {
				em = getEntityManager();
				collection = (Collection) em.find(Collection.class, collectionId);
				
				if(loadalldata) {
					Set<Contact> contacts = collection.getContacts();
					for(Contact contact: contacts){	
						contact.getEmployee();
					}
					
					Set<Multilink> setLink=collection.getMultilinks();
					for(Multilink multilink: setLink){	
						multilink.getLink();
					}
					
					Set<Distribution> distributionset=collection.getDistributions();
					for(Distribution distribution: distributionset){	
						distribution.getAorowner().getHierarchyelement().getHierarchy();
						distribution.getAorowner().getEmployeeByAorowner();
					}
					Set<Observation> obsset=collection.getObservations();
					for(Observation obs: obsset){	
						obs.getStatus();
						Set<Recommendation> recset=obs.getRecommendations();
						for(Recommendation rec:recset) {
							rec.getStatus();
							Set<Issue> issueset=rec.getIssues();
							for(Issue issue:issueset) {
								issue.getStatus();
								issue.getInitialstate();
							}
						}
					}
					Set<Colisstask> tasks=collection.getColisstasks();
					for(Colisstask task: tasks){	
						task.getStatus();
						task.getCollisstasktype();
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("loadCollection", exception);
			throw new MicosException(ExceptionContext.ERROR, exception, SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "loadCollection");

		}
		
		return collection;
	}

	
	/**
	 * Create A Collection..
	 * 
	 * @param collection
	 *            Collection
	 * @param loggedinEmployee
	 *            Integer
	 * @return Integer
	 * @throws MicosException
	 *             Micos Exception
	 */
	public Integer createCollection(Collection collection, Employee loggedinEmployee)throws MicosException{
		LOGGER.entering(getClass().getName(), "createCollection");
		EntityManager em = null;
		Integer collectionId ;
		
		try{
			em = getEntityManager();
			em.getTransaction().begin();
			
			Set<Colclassificationbusdiv> setColclassificationbusdiv = new HashSet<Colclassificationbusdiv>();
			Set<Colclassificationsserv> setColclassificationsserv = new HashSet<Colclassificationsserv>();
			Set<Colclassifictionregion> setColclassifictionregion = new HashSet<Colclassifictionregion>();
			Collectionclassification collectionclassification = new Collectionclassification();
			collectionclassification.setCollection(collection);
			collectionclassification.setLastupdate(DateUtil.getCurrentTimestamp());
			collectionclassification.setCreatedate(DateUtil.getCurrentTimestamp());
			collectionclassification.setEmployee(loggedinEmployee);
			setColclassificationbusdiv = populateBusinessDivision(collectionclassification, em, loggedinEmployee);
			collectionclassification.setColclassificationbusdivs(setColclassificationbusdiv);
			setColclassifictionregion = populateRegion(collectionclassification,em, loggedinEmployee);
			collectionclassification.setColclassifictionregions(setColclassifictionregion);
			setColclassificationsserv = populateSharedServises(collectionclassification, em, loggedinEmployee);
			collectionclassification.setColclassificationsservs(setColclassificationsserv);
			collectionclassification.setSignrepeatissues("N");
			collectionclassification.setSignreputationalriskissues("N");
			
			collection.setCollectionclassification(collectionclassification);
			
			//Add all the existing contacts for the selected issue type in collection (Part of Package 1 CR)
			Set<Issuecodinggroup> setOfIssuecodinggroup= collection.getCollectiontype().getIssuetype().getIssuecodinggroups();
			Set<Contact> contacts=null;
			if(SystemUtil.isNotNull(setOfIssuecodinggroup)){
				contacts=new HashSet<Contact>();
				for(Issuecodinggroup issueCodingGrp: setOfIssuecodinggroup){
					Employee employee=issueCodingGrp.getEmployee1();
					Contact contact=new Contact();
					contact.setEmployee(employee);
					contact.setLastupdatedby(loggedinEmployee);
					contact.setCreatedate(DateUtil.getCurrentTimestamp());
					contact.setLastupdate(DateUtil.getCurrentTimestamp());
					contact.setNotifybyemail("N");
					contact.setIsfromissuetype("Y");
					contact.setCollection(collection);
					contacts.add(contact);
				}
				collection.setContacts(contacts);
			}
			
			collection.setCreatedate(DateUtil.getCurrentTimestamp());
			collection.setLastupdate(DateUtil.getCurrentTimestamp());
			
			em.persist(collection);
			em.getTransaction().commit();
			collectionId = collection.getCollectionid();
		}
		catch (Exception exception) {
			LOGGER.error("createCollection", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(),"createCollection");
		}
		return collectionId;
	}
	
	/**
	 * Update Collection.
	 * @param collection Collection
	 * @param notificationList List of Notification
	 * @throws MicosException an exception
	 */
	public void updateCollection(Collection collection, List<Notification> notificationList) 
	throws MicosException {
		LOGGER.entering(getClass().getName(), "updateCollection");
		EntityManager em = null;
		
		try{
			em = getEntityManager();
			em.getTransaction().begin();
			em.merge(collection);
			if(SystemUtil.isNotNull(notificationList)){
				for(Notification newNotification : notificationList){
					em.persist(newNotification);
				}
			}
						
			em.getTransaction().commit();
			
		}
		catch (Exception exception) {
			LOGGER.error("updateCollection", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(),"updateCollection");
		}
	}
	
	/**
	 * populate the Shared Shervises.
	 * @param aCollectionclassification Collectionclassification
	 * @param em EntityManager
	 * @param aEmployee Employee
	 * @return Set
	 */
	@SuppressWarnings("unchecked")
	private Set<Colclassificationsserv> populateSharedServises(
			Collectionclassification aCollectionclassification,
			EntityManager em, Employee aEmployee) {

		List<Sharedservices> listSharedservices = new ArrayList<Sharedservices>();
		Set<Colclassificationsserv> setColclassificationsserv = new HashSet<Colclassificationsserv>();
		Query query = em.createNativeQuery("select * from Sharedservices", Sharedservices.class);
		if (null != query) {
			listSharedservices = query.getResultList();
		}

		if (!SystemUtil.isNull(listSharedservices)) {
			for(Sharedservices aSharedservices:listSharedservices) {
				Colclassificationsserv aColclassificationsserv = new Colclassificationsserv();
				aColclassificationsserv.setSharedservices(aSharedservices);
				aColclassificationsserv.setCollectionclassification(aCollectionclassification);
				aColclassificationsserv.setSelected("N");
				aColclassificationsserv.setCreatedate(DateUtil.getCurrentTimestamp());
				aColclassificationsserv.setEmployee(aEmployee);
				aColclassificationsserv.setLastupdate(DateUtil.getCurrentTimestamp());
				setColclassificationsserv.add(aColclassificationsserv);
			}

		}
		return setColclassificationsserv;
	}
	
	/**
	 * populate the Region.
	 * 
	 * @param aCollectionclassification
	 *            Collectionclassification
	 * @param em EntityManager
	 *            
	 * @param aEmployee
	 *            Employee
	 * @return Set
	 */
	@SuppressWarnings("unchecked")
	private Set<Colclassifictionregion> populateRegion(
			Collectionclassification aCollectionclassification,
			EntityManager em, Employee aEmployee) {

		List<Region> listRegion = new ArrayList<Region>();
		Set<Colclassifictionregion> setColclassifictionregion = new HashSet<Colclassifictionregion>();
		Query query = em.createNativeQuery("select * from Region", Region.class);
		if (null != query) {
			listRegion = query.getResultList();
		}

		if (!SystemUtil.isNull(listRegion)) {
			for(Region aRegion: listRegion) {
				Colclassifictionregion aColclassifictionregion = new Colclassifictionregion();
				aColclassifictionregion.setRegion(aRegion);
				aColclassifictionregion.setCollectionclassification(aCollectionclassification);
				aColclassifictionregion.setSelected("N");
				aColclassifictionregion.setEmployee(aEmployee);
				aColclassifictionregion.setLastupdate(DateUtil.getCurrentTimestamp());
				aColclassifictionregion.setCreatedate(DateUtil.getCurrentTimestamp());
				setColclassifictionregion.add(aColclassifictionregion);
			}

		}

		return setColclassifictionregion;
	}

	/**
	 * populate the BusinessDivision.
	 * 
	 * @param aCollectionclassification
	 *            Collectionclassification
	 * @param em EntityManager
	 *            
	 * @param aEmployee
	 *            Employee
	 * @return Set
	 */
	@SuppressWarnings("unchecked")
	private Set<Colclassificationbusdiv> populateBusinessDivision(
			Collectionclassification aCollectionclassification,
			EntityManager em, Employee aEmployee) {

		List<Businessdivision> listBusinessdivision = new ArrayList<Businessdivision>();
		Set<Colclassificationbusdiv> setColclassificationbusdiv = new HashSet<Colclassificationbusdiv>();
		Query query = em.createNativeQuery("select * from Businessdivision", Businessdivision.class);
		if (null != query) {
			listBusinessdivision = query.getResultList();
		}

		if (!SystemUtil.isNull(listBusinessdivision)) {
			for (Businessdivision aBusinessdivision:listBusinessdivision) {
				Colclassificationbusdiv aColclassificationbusdiv = new Colclassificationbusdiv();
				aColclassificationbusdiv.setBusinessdivision(aBusinessdivision);
				aColclassificationbusdiv.setCollectionclassification(aCollectionclassification);
				aColclassificationbusdiv.setSelected("N");
				aColclassificationbusdiv.setCreatedate(DateUtil.getCurrentTimestamp());
				aColclassificationbusdiv.setEmployee(aEmployee);
				aColclassificationbusdiv.setLastupdate(DateUtil.getCurrentTimestamp());
				setColclassificationbusdiv.add(aColclassificationbusdiv);
			}

		}
		return setColclassificationbusdiv;
	}
	
	/**
	 * Create A Collection..
	 * @param workingasEmployeeId workingasEmployeeId
	 * @return long
	 * @throws MicosException Micos Exception
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCollectionTypes(Integer workingasEmployeeId)throws MicosException {
		
		LOGGER.entering(getClass().getName(), "getCollectionTypes");
		EntityManager em = null;
		List<SelectItem> collectionTypes = new ArrayList<SelectItem>();
		try{
			em = getEntityManager();
			
		    String queryString = "SELECT distinct ct.collectiontypeid, ct.NAME "
		    		+ " FROM collectiontype ct, "
		    		+ " issuetype it, "
		    		+ " issuecreatorgroup icg, "
		    		+ " issuecreatorgroupemployee icgemp "
		    		+ " WHERE ct.issuetypeid = it.issuetypeid "
		    		+ "  AND it.issuetypeid = icg.issuetypeid "
		    		+ "  AND icg.issuecreatorgroupid =icgemp.issuecreatorgroupid "
		    		+ "  AND icgemp.employeeid = "+workingasEmployeeId;
		    Query query1 = em.createNativeQuery(queryString);
		    
		    List<Object[]> results = query1.getResultList();
		    
		    for (Object[] result : results) {
		    	collectionTypes.add(new SelectItem(result[0], (String)result[1]));
		    }
		}
		catch (Exception exception) {
			LOGGER.error("getCollectionTypes", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(),"getCollectionTypes");
		}
		
		return collectionTypes;
		
	}
	
	/**
	 * get all the ICG for selected collection type
	 * 
	 * @param collectiontypeid
	 *            collection type id
	 * @param workingasuser
	 *            workingasuser id
	 * @return list of ICG
	 * @throws MicosException
	 *             Micos Exception
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getIssueCreatorGroups(Integer collectiontypeid, Integer workingasuser)throws MicosException {
		LOGGER.entering(getClass().getName(), "getIssueCreatorGroups");
		EntityManager em = null;
		List<SelectItem> icglist = new ArrayList<SelectItem>();
		try{
			em = getEntityManager();
			
		    String queryString = "SELECT icg.NAME, icg.issuecreatorgroupid "
		    		+ " FROM issuecreatorgroup icg, "
		    		+ "  collectiontype ct, "
		    		+ " issuecreatorgroupemployee icgemp "
		    		+ " WHERE icgemp.employeeid = " + workingasuser
		    		+ "  AND ct.collectiontypeid = " + collectiontypeid
		    		+ "   AND icg.issuetypeid = ct.issuetypeid "
		    		+ "  AND icgemp.issuecreatorgroupid = icg.issuecreatorgroupid ";
		    Query query1 = em.createNativeQuery(queryString);
		    
		    List<Object[]> results = query1.getResultList();
		    
		    for (Object[] result : results) {
		    	SelectItem selectItem=new SelectItem();
				selectItem.setLabel((String)result[0]);
				selectItem.setValue(((BigDecimal)result[1]).intValue());
				icglist.add(selectItem);
		    }
		}
		catch (Exception exception) {
			LOGGER.error("getIssueCreatorGroups", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(),"getIssueCreatorGroups");
		}
		
		return icglist;
	}
	
	/**
	 * Get all collections for collections overview  	
	 * @param workingAsUserid working As Userid
	 * @param tabname selected tabname
	 * @return List of collections
	 * @exception MicosException Micos Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getCollections(Integer workingAsUserid, String tabname)throws MicosException {
		LOGGER.entering(getClass().getName(), "getCollections");
		List<Object[]> collectionList = new ArrayList<Object[]>();
		EntityManager em = null;
		try {
			
			
			String mainquery = "";
			if(tabname.equalsIgnoreCase(IssuesConstants.TAB_CLOSED)) {
				 mainquery = ReportDaoHelper.getQueryforCollectionsWithoutTask(workingAsUserid, tabname); 
			} else {
				String qryStr1 = ReportDaoHelper.getQueryforCollectionsWithTask(workingAsUserid); 
				String qryStr2 = ReportDaoHelper.getQueryforCollectionsWithoutTask(workingAsUserid, tabname); 
				mainquery = qryStr1 +" union " + qryStr2;
			}
			em = getEntityManager();
			Query query = em.createNativeQuery(mainquery);
			collectionList = query.getResultList();
			
		} catch (Exception exception) {
			LOGGER.error("getCollections", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			
			LOGGER.exiting(getClass().getName(), "getCollections");
		}
		return collectionList;
	}
	

	
	/**
	 * Fetches the Collection Information for Collection id.
	 * 
	 * @param collectionId
	 *            collection id
	 * @return collection information
	 * @exception MicosException
	 *                Micos Exception
	 * 
	 */
	public Object[] fetchCollectionInformation(Integer collectionId) throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchCollectionInformation");
		Object[] collection = new Object[11];
		EntityManager em = null;
		try {
			StringBuilder queryStr = new StringBuilder();
			queryStr.append("select distinct collection.COLLECTIONID,collection.NAME, collection.TITLE, ");
			queryStr.append("collection.issuesraised , collection.validationrequired, collection.codingrequired, ");
			queryStr.append("collection.scopingrequired , collectiontype.name, ");
			queryStr.append("status.STATUSCODE, status.STATUSDESCRIPTION, collection.issueddate, collectiontype.INTERNALAUDITREGULATORY,collectiontype.COLLECTIONTYPEID ");
			queryStr.append("from collection, status, collectiontype where ");
			queryStr.append("status.statusid = collection.STATUSID and ");
			queryStr.append("collectiontype.collectiontypeid = collection.COLLECTIONTYPEID and ");
			queryStr.append("collection.collectionid = "+collectionId);
			
			em = getEntityManager();
			Query query = em.createNativeQuery(queryStr.toString());
			collection = (Object[]) query.getSingleResult();
			
		} catch (Exception exception) {
			LOGGER.error("fetchCollectionInformation", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "fetchCollectionInformation");
		}
		return collection;
	}
	
	/**
	 * @param workingasuser workingasuser id
	 * @return true, if user is ICG member
	 * @throws MicosException MicosException
	 */
	public boolean displayCreateReportBtn(Integer workingasuser) throws MicosException {
		LOGGER.entering(getClass().getName(), "displayCreateReportBtn");
		EntityManager em = null;
		boolean result = false;
		try {
			em = getEntityManager();
			String qryStr = "SELECT COUNT (icg.issuecreatorgroupid) "
				+ " FROM issuecreatorgroup icg, issuecreatorgroupemployee icgemp, issuetype it "
				+ " WHERE icg.issuecreatorgroupid = icgemp.issuecreatorgroupid "
				+ " AND icgemp.employeeid = " + workingasuser
				+ " AND icg.issuetypeid = it.issuetypeid "
				+ " AND it.available = 'Y'";
			Query qry = em.createNativeQuery(qryStr);
			BigDecimal count = (BigDecimal)qry.getSingleResult();
			if(count != null && count.intValue() > 0) {
				result = true;
			}
			
		} catch (Exception exception) {
			LOGGER.error("displayCreateReportBtn", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "displayCreateReportBtn");
		}
		return result;
	}
		/**
	 * This method provides collection type for 
	 * collection type PK
	 * @param collectionTypeID Integer
	 * @return Collectiontype Collection Type
	 * @throws MicosException MicosException
	 */
	public Collectiontype getCollectiontype(Integer collectionTypeID)throws MicosException{
		LOGGER.entering(getClass().getName(), "getCollectiontype");
		Collectiontype colType=null;
		EntityManager em = null;		
		try{
			em=getEntityManager();
			colType=em.find(Collectiontype.class, collectionTypeID);
			Issuetype issueType=colType.getIssuetype();
			if(null!=issueType){
				Set<Issuecreatorgroup> setOfIssueCreatorGrp=issueType.getIssuecreatorgroups();
				if(SystemUtil.isNotNull(setOfIssueCreatorGrp)){
					for(Issuecreatorgroup issueCreatorgrp: setOfIssueCreatorGrp){
						issueCreatorgrp.getIssuecreatorgroupid();
					}
				}
			}			
		}catch (Exception exception) {
			LOGGER.error("getCollectiontype", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getCollectiontype");
		}
		return colType;
	}

	/**
	 * Retrive the issuegrouop.
	 * @param issueGroupID Integer
	 * @return IssueCreatorGroupAssignmentView
	 * @throws MicosException MicosException
	 */
	public Issuecreatorgroup getIssueCreatorGroupDetailsById(
			Integer issueGroupID) throws MicosException {
		EntityManager em = null;
		Issuecreatorgroup issuecreatorgroup = null;
		try {
			em = getEntityManager();
			issuecreatorgroup = (Issuecreatorgroup) em.find(
					Issuecreatorgroup.class, issueGroupID);
			issuecreatorgroup.getIssuecreatorgroupemployees();
		}catch (Exception exception) {
			LOGGER.error("getIssueCreatorGroupDetailsById", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "getIssueCreatorGroupDetailsById");
		}
		return issuecreatorgroup;
	}
	
	/**
	 * @param reportid reportid
	 * @return task id
	 * @throws MicosException MicosException
	 */
	public Integer getOpenTaskIdForReport(Integer reportid) throws MicosException {
		LOGGER.entering(getClass().getName(), "getTaskIdForReport");
		EntityManager em = null;
		BigDecimal taskid;
		try {
			em = getEntityManager();
			String qryStr = "SELECT t.collisstaskid "
					+ " FROM colisstask t, status s "
					+ " WHERE s.statuscode = '"+Status.OPEN+"' "
					+ " AND s.statusid = t.statusid "
					+ " AND t.collectionid = "+ reportid;
			Query qry = em.createNativeQuery(qryStr);
			taskid = (BigDecimal)qry.getSingleResult();
			
		} catch (Exception exception) {
			LOGGER.error("getTaskIdForReport", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getTaskIdForReport");
		}
		return taskid.intValue();
	}
	
	/**
	 * method for loading Collection Type
	 * @param collectiontypeId collection type id 
	 * @return Collectiontype Collectiontype object
	 * @exception MicosException Micos Exception
	 */
	public Collectiontype loadCollectionType(Integer collectiontypeId) throws MicosException {
		LOGGER.entering(getClass().getName(), "loadCollectionType");
		Collectiontype collectiontype = null;
		EntityManager em = null;
		try {
			if (null != collectiontypeId) {
				em = getEntityManager();
				collectiontype = (Collectiontype) em.find(Collectiontype.class, collectiontypeId);	
				if(SystemUtil.isNotNull(collectiontype.getIssuetype().getIssuecodinggroups())){
					for(Issuecodinggroup issuecodinggroup:collectiontype.getIssuetype().getIssuecodinggroups()){
						issuecodinggroup.getEmployee1().getAddress();
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("loadCollectionType", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "loadCollectionType");

		}
		return collectiontype;
	}
	
	/**
	 * @param collectionid collection id 
	 * @return list of observation
	 * @exception MicosException Micos Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Observation> getObservationsForValidation(Integer collectionid) throws MicosException  {
		
		LOGGER.entering(getClass().getName(), "getObservationsForValidation");
		List<Observation>  observations = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
			String qryStr = "select * from observation where observation.COLLECTIONID =" + collectionid;
			Query qry = em.createNativeQuery(qryStr, Observation.class);
			observations = (List<Observation>)qry.getResultList();
			if(observations != null) {
				Iterator<Observation> itrObs = observations.iterator();
				while(itrObs.hasNext()){
					Observation obs = itrObs.next();
					if(obs.getStatus().getStatuscode().equalsIgnoreCase(Status.DELETED)) {
						itrObs.remove();
					} else {
						Set<Recommendation> recs = obs.getRecommendations();
						if(recs != null) {
							Iterator<Recommendation> itrRec = recs.iterator();
							while(itrRec.hasNext()){
								Recommendation rec = itrRec.next();
								if(rec.getStatus().getStatuscode().equalsIgnoreCase(Status.DELETED)) {
									itrRec.remove();
								} else {
									Set<Issue> issues = rec.getIssues();
									populateIssueforValidation(issues);
								}
							}
						}
					}
				}
			}
		} catch (Exception exception) {
			LOGGER.error("getObservationsForValidation", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
				em.close();
			}
			LOGGER.exiting(getClass().getName(), "getObservationsForValidation");

		}
		return observations;
	}
	
	/**
	 * @param issues issues
	 */
	private void populateIssueforValidation(Set<Issue> issues){
		if(issues!= null) {
			Iterator<Issue> itrIssue = issues.iterator();
			while(itrIssue.hasNext()){
				Issue issue = itrIssue.next();
				if(issue.getStatus().getStatuscode().equalsIgnoreCase(Status.DELETED)) {
					itrIssue.remove();
				} else {
					issue.getInitialstate();
					issue.getAorowner();
					issue.getIssueaccessepids();
					issue.getIssuetype();
				}
			}
		}
	}
	
		/**
	 * Checks for the status of observation,recommendaion and issue of a collection.
	 * @param collectionId Integer
	 * @return boolean
	 * @throws MicosException MicosException
	 */
	public boolean getStatus(Integer collectionId) throws MicosException {
		LOGGER.entering(getClass().getName(), "getStatus");
		EntityManager em = null;
		boolean result = false;
		try {
			em = getEntityManager();			
				String qryStrObs = "Select count(obs.OBSERVATIONID) from OBSERVATION obs, Status st  "
						+ " where obs.collectionid = " 	+ collectionId
						+ " And obs.STATUSID = st.STATUSID "
						+ " And st.STATUSCODE = 'ICMP'";

				Query qry = em.createNativeQuery(qryStrObs);
				BigDecimal count = (BigDecimal) qry.getSingleResult();
				if (count != null && count.intValue() > 0) {
					result = true;
				} else {
					String qryStrReco = "Select count(reco.RECOMMENDATIONID) from RECOMMENDATION reco, Status st, OBSERVATION obs  "
							+ " where reco.OBSERVATIONID = obs.OBSERVATIONID "
							+ " And  obs.collectionid = " + collectionId
							+ " And reco.STATUSID = st.STATUSID "
							+ " And st.STATUSCODE = 'ICMP'";

					qry = em.createNativeQuery(qryStrReco);
					count = (BigDecimal) qry
							.getSingleResult();
					if (count != null && count.intValue() > 0) {
						result = true;
					} else {
						String qryStrIss = "Select count(iss.ISSUEID) from RECOMMENDATION reco, Status st, OBSERVATION obs, ISSUE iss  "
								+ " where iss.RECOMMENDATIONID = reco.RECOMMENDATIONID "
								+ " And reco.OBSERVATIONID = obs.OBSERVATIONID "
								+ " And  obs.collectionid =  " + collectionId
								+ " And iss.STATUSID = st.STATUSID "
								+ " And st.STATUSCODE = 'ICMP'";

						qry = em.createNativeQuery(qryStrIss);
						count = (BigDecimal) qry
								.getSingleResult();
						if (count != null && count.intValue() > 0) {
							result = true;
						}
					}
				}
			

		} catch (Exception exception) {
			LOGGER.error("getStatus", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getStatus");
		}
		return result;
	}
}
