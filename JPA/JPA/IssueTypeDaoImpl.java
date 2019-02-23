/******************************************************************************
 * Copyright(c) 2003-2010 CREDIT SUISSE Financial Services. All Rights Reserved.
 *
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.csg.cs.core.base.exception.ExceptionContext;
import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.dao.IssueTypeDao;
import com.csg.cs.micos.entitybean.Aorowner;
import com.csg.cs.micos.entitybean.Collection;
import com.csg.cs.micos.entitybean.Collectionissuenotification;
import com.csg.cs.micos.entitybean.Collectiontype;
import com.csg.cs.micos.entitybean.Contact;
import com.csg.cs.micos.entitybean.Employee;
import com.csg.cs.micos.entitybean.Hierarchy;
import com.csg.cs.micos.entitybean.Hierarchyelement;
import com.csg.cs.micos.entitybean.Hierarchytype;
import com.csg.cs.micos.entitybean.Issuecodinggroup;
import com.csg.cs.micos.entitybean.Issuecreatorgroup;
import com.csg.cs.micos.entitybean.Issuecreatorgroupemployee;
import com.csg.cs.micos.entitybean.Issuegroupadministrator;
import com.csg.cs.micos.entitybean.Issuetype;
import com.csg.cs.micos.entitybean.Periodtype;
import com.csg.cs.micos.entitybean.Status;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.DateUtil;
import com.csg.cs.micos.util.MicosConstantValues;
import com.csg.cs.micos.util.SystemUtil;
/**
 * Used as a DAO layer for accessing the data for issues.
 *
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 153146 $
 * @version $Id: IssueTypeDaoImpl.java,v 1.1 2010/10/25 15:25:36 153146 Exp $
 */
public class IssueTypeDaoImpl extends BaseDaoImpl implements IssueTypeDao {

	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
			.getLogger(IssueTypeDaoImpl.class);

	/**
	 * @param workingasuserid workingasuserid
	 * @return list of issue types
	 * @throws MicosException
	 *             MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuetype> getIssueTypes(Integer workingasuserid)
			throws MicosException {

		List<Issuetype> issuetype = new ArrayList<Issuetype>();
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from issuetype ", Issuetype.class);
			issuetype = qry.getResultList();
			if (SystemUtil.isNull(issuetype)) {
				for (Issuetype issue : issuetype) {
					Employee employee = issue.getEmployee();
					employee.getFirstname();
				}
			}

		} catch (Exception exception) {
			LOGGER.error("getIssueTypes", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getIssueTypes");
		}
		return issuetype;
	}

	/**
	 * change the availability of the issue.
	 * @param issueID Integer
	 * @throws MicosException MicosException
	 */
	public void deleteIssue(Integer issueID) throws MicosException {
		EntityManager em = null;

		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Issuetype issue = (Issuetype) em.find(Issuetype.class, issueID);
			issue.setAvailable("N");
			em.merge(issue);
			em.getTransaction().commit();

		} catch (Exception exception) {
			LOGGER.error("deleteIssue", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "deleteIssue");
		}
	}

	/**
	 * @param workingasuserid workingasuserid
	 * @return list of issue types
	 * @throws MicosException MicosException
	 *             
	 */
	@SuppressWarnings("unchecked")
	public List<Issuecreatorgroup> getIssueCreatorGroup(Integer workingasuserid)
			throws MicosException {

		List<Issuecreatorgroup> issueCreatorGroup = new ArrayList<Issuecreatorgroup>();
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from issuecreatorgroup t where t.groupowner = "
							+ workingasuserid, Issuecreatorgroup.class);
			issueCreatorGroup = qry.getResultList();
			if (SystemUtil.isNotNull(issueCreatorGroup)) {
				for (Issuecreatorgroup issueGroup : issueCreatorGroup) {
					Hierarchyelement hierarchyelement = issueGroup
							.getHierarchyelement();
					hierarchyelement.getName();
					Issuetype issuetype = issueGroup.getIssuetype();
					issuetype.getAvailable();
				}
			}

		} catch (Exception exception) {
			LOGGER.error("getIssueCreatorGroup", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getIssueCreatorGroup");
		}
		return issueCreatorGroup;
	}

	/**
	 * 
	 * @param aorOwnerId Integer
	 * @return Hierarchyelement
	 * @throws MicosException MicosException
	 */
	public Hierarchyelement loadHierarchyElementByAorOwner(Integer aorOwnerId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "loadHierarchyElement");
		Hierarchyelement hierarchyelement = null;
		Aorowner aorowner = null;
		EntityManager em = null;
		try {
			if (null != aorOwnerId) {
				em = getEntityManager();
				aorowner = (Aorowner) em.find(Aorowner.class, aorOwnerId);
				hierarchyelement = aorowner.getHierarchyelement();
				hierarchyelement.getHierarchyelementid();

			}
		} catch (Exception exception) {
			LOGGER.error("loadHierarchyElement", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "loadHierarchyElement");

		}
		return hierarchyelement;
	}

	/**
	 * save issuecreator.
	 * @param issuecreatorgroup Issuecreatorgroup
	 * @return issue creator id
	 * @throws MicosException MicosException
	 */
	public Integer createIssueCreatorGroup(Issuecreatorgroup issuecreatorgroup)
			throws MicosException {
		EntityManager em = null;
		Integer issueCreatoeGroupId = -1;
		
		boolean checkName = false;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			String querystr="";

			if (null == issuecreatorgroup.getIssuecreatorgroupid()) {
			 querystr = "SELECT t.NAME FROM issuecreatorgroup t "
					+ " WHERE t.issuetypeid = " +issuecreatorgroup.getIssuetype().getIssuetypeid();
			} else {
			 querystr = "SELECT t.NAME FROM issuecreatorgroup t "
					+ " WHERE t.issuetypeid = " +issuecreatorgroup.getIssuetype().getIssuetypeid()
					+ " and t.ISSUECREATORGROUPID <> "+ issuecreatorgroup.getIssuecreatorgroupid();
			}
			 
			Query qry = em.createNativeQuery(querystr);
			
			List<String> issueCreatorGroup = qry.getResultList();
			if (SystemUtil.isNotNull(issueCreatorGroup)) {
				for(String icgname:issueCreatorGroup){
					if(icgname.equalsIgnoreCase(issuecreatorgroup.getName().trim())){
						checkName = true;
						break;
					}
				}
			}
			if(!checkName){
				issuecreatorgroup.setLastupdate(DateUtil.getCurrentTimestamp());
				if (null == issuecreatorgroup.getIssuecreatorgroupid()) {
					issuecreatorgroup.setCreatedate(DateUtil.getCurrentTimestamp());
					em.persist(issuecreatorgroup);
				} else {
					em.merge(issuecreatorgroup);
				}
				em.getTransaction().commit();
				for (Issuecreatorgroupemployee issue : issuecreatorgroup
						.getIssuecreatorgroupemployees()) {
					Employee employee = issue.getLastupdatedby();
					employee.getAddress();
				}
				issueCreatoeGroupId = issuecreatorgroup.getIssuecreatorgroupid();
			}

		} catch (Exception exception) {
			LOGGER.error("getIssueCreatorGroup", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getIssueCreatorGroup");
		}
		return issueCreatoeGroupId;
	}

	/**
	 * Retrive all details of issuegrouop.
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
			//Query qry = em.createNativeQuery("select * from issuecreatorgroup t where t.issuecreatorgroupid = " 
			//+ issueGroupID, Issuecreatorgroup.class);

			//issuecreatorgroup=(Issuecreatorgroup)qry.getSingleResult();
			issuecreatorgroup = (Issuecreatorgroup) em.find(
					Issuecreatorgroup.class, issueGroupID);

			if (null != issuecreatorgroup) {
				Hierarchyelement hierarchyelement = issuecreatorgroup
						.getHierarchyelement();
				hierarchyelement.getName();

				Set<Aorowner> setAor = hierarchyelement.getAorowners();
				for (Aorowner aorowner : setAor) {
					if (null != aorowner.getAorownerid()) {
						aorowner.getDefernotifications();
					}
				}

				Employee employee = issuecreatorgroup.getLastupdatedby();
				employee.getFirstname();

				Issuetype issuetype = issuecreatorgroup.getIssuetype();
				issuetype.getName();
				em.refresh(issuecreatorgroup);
				Set<Issuecreatorgroupemployee> set = issuecreatorgroup
						.getIssuecreatorgroupemployees();
				if (SystemUtil.isNotNull(set)) {
					for (Issuecreatorgroupemployee issuecreatorgroupemployee : set) {
						issuecreatorgroupemployee.getEmployee().getFirstname();
					}
				}
			}

		} catch (Exception exception) {
			LOGGER.error("getIssueCreatorGroupDetails", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getIssueCreatorGroupDetails");
		}
		return issuecreatorgroup;
	}

	/**
	 * save Issuetype.
	 * @param issuetype Issuetype
	 * @return Integer
	 * @throws MicosException MicosException
	 */
	public Integer createIssueTypeProperties(Issuetype issuetype)
			throws MicosException {
		EntityManager em = null;
		Integer issueTypeId = 0;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			issuetype.setLastupdate(DateUtil.getCurrentTimestamp());
			
			if (null == issuetype.getIssuetypeid()) {
				issuetype.setCreatedate(DateUtil.getCurrentTimestamp());
				em.persist(issuetype);
			} else {
				if(SystemUtil.isNotNull(issuetype.getIssuecodinggroups())){
					saveContactsForReport(em,issuetype);
				}
				em.merge(issuetype);
			}
			em.getTransaction().commit();

			issueTypeId = issuetype.getIssuetypeid();

		} catch (Exception exception) {
			LOGGER.error("createIssueTypeProperties", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "createIssueTypeProperties");
		}
		return issueTypeId;
	}

	
	/**
	 * save the Contacts of Report for this issue type.
	 * @param em EntityManager
	 * @param issuetype  Issuetype
	 */
	private void saveContactsForReport(EntityManager em, Issuetype issuetype){
		List<Collection> list = null; 
		Set<Issuecodinggroup> setIssuecodinggroup= issuetype.getIssuecodinggroups();
		Set<Collectiontype> setCollectiontype = issuetype.getCollectiontypes();
		Integer collectionId = 0;
		for(Collectiontype collectiontype: setCollectiontype){
			collectionId = collectiontype.getCollectiontypeid();
		}
		
		Query qry = em.createNativeQuery(
				"select * from collection t where t.COLLECTIONTYPEID = "
						+ collectionId, Collection.class);
		list = qry.getResultList();
		
		if(SystemUtil.isNotNull(list)){
			for(Collection collection : list){
				Set<Contact> setContact = collection.getContacts();
				Set<Issuecodinggroup> setofNewIssuecodinggroup =  new HashSet<Issuecodinggroup>();
				if(SystemUtil.isNotNull(setContact)){
					for(Issuecodinggroup issuecodinggroup : setIssuecodinggroup){
						boolean flag = false;
						for(Contact contact : setContact){
							if(issuecodinggroup.getEmployee1().getEmployeeid().equals(contact.getEmployee().getEmployeeid())){
								contact.setIsfromissuetype("Y");
								contact.setCreatedate(DateUtil.getCurrentTimestamp());
								contact.setLastupdatedby(issuetype.getLastupdatedby());
								em.merge(contact);
								if(setofNewIssuecodinggroup.contains(issuecodinggroup)){
									setofNewIssuecodinggroup.remove(issuecodinggroup);
								}
								flag = true;
								break;
							}else{
								if(!setofNewIssuecodinggroup.contains(issuecodinggroup) && !flag){
									setofNewIssuecodinggroup.add(issuecodinggroup);
								}
							}
						}
					}
					if(SystemUtil.isNotNull(setofNewIssuecodinggroup)){
						for(Issuecodinggroup issuecodinggroup : setofNewIssuecodinggroup){
							Contact contact = new Contact();
							contact.setCollection(collection);
							contact.setLastupdate(DateUtil.getCurrentTimestamp());
							contact.setCreatedate(DateUtil.getCurrentTimestamp());
							contact.setLastupdatedby(issuetype.getLastupdatedby());
							contact.setEmployee(issuecodinggroup.getEmployee1());
							contact.setIsfromissuetype("Y");
							contact.setNotifybyemail("N");
							em.merge(contact);
						}
					}
				}else{
					for(Issuecodinggroup issuecodinggroup : setIssuecodinggroup){
						Contact contact = new Contact();
						contact.setCollection(collection);
						contact.setLastupdate(DateUtil.getCurrentTimestamp());
						contact.setCreatedate(DateUtil.getCurrentTimestamp());
						contact.setLastupdatedby(issuetype.getLastupdatedby());
						contact.setEmployee(issuecodinggroup.getEmployee1());
						contact.setIsfromissuetype("Y");
						contact.setNotifybyemail("Y");
						em.merge(contact);
					}
				}
				
				
			}
		}
		
	
	}
	/**
	 * 
	 * @return boolean
	 * @param issuecreatorgroupEmployeeId Integer
	 * @throws MicosException MicosException
	 */
	public boolean deleteIssuecreatorgroupEmployee(
			Integer issuecreatorgroupEmployeeId) throws MicosException {
		EntityManager em = null;
		boolean flag = false;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Issuecreatorgroupemployee issuecreatorgroupemployee = em.find(
					Issuecreatorgroupemployee.class,
					issuecreatorgroupEmployeeId);
			Issuecreatorgroup issuecreatorgroup = issuecreatorgroupemployee
					.getIssuecreatorgroup();
			em.remove(issuecreatorgroupemployee);
			em.refresh(issuecreatorgroup);
			em.getTransaction().commit();
			flag = true;

		} catch (Exception exception) {
			LOGGER.error("deleteIssuecreatorgroupEmployee", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(),
					"deleteIssuecreatorgroupEmployee");
		}
		return flag;
	}

	/**
	 * 
	 * @param issuecreatorgroupid Integer
	 * @return list of Issuecreatorgroupemployee
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuecreatorgroupemployee> loadIssueCreatorgroupEmp(
			Integer issuecreatorgroupid) throws MicosException {
		EntityManager em = null;
		List<Issuecreatorgroupemployee> list = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from issuecreatorgroupemployee t where t.issuecreatorgroupid = "
							+ issuecreatorgroupid,
					Issuecreatorgroupemployee.class);
			list = qry.getResultList();
		} catch (Exception exception) {
			LOGGER.error("loadIssueCreatorgroupEmp", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "loadIssueCreatorgroupEmp");
		}
		return list;
	}

	/**
	 * 
	 * @param issueAdminId Integer
	 * @return list of Issuecodinggroup
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuecodinggroup> loadIssuecodinggroup(Integer issueAdminId)
			throws MicosException {
		EntityManager em = null;
		List<Issuecodinggroup> list = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from issuecodinggroup t where t.ISSUETYPEID = "
							+ issueAdminId, Issuecodinggroup.class);
			list = qry.getResultList();
		} catch (Exception exception) {
			LOGGER.error("deleteCodingGroup", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "deleteCodingGroup");
		}
		return list;
	}

	/**
	 * 
	 * @param issueTypeId Integer
	 * @return list of Issuegroupadministrator
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuegroupadministrator> loadIssueAdmin(Integer issueTypeId)
			throws MicosException {
		EntityManager em = null;
		List<Issuegroupadministrator> list = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from Issuegroupadministrator t where t.issuegroupadministratorid = "
							+ issueTypeId, Issuegroupadministrator.class);
			list = qry.getResultList();
		} catch (Exception exception) {
			LOGGER.error("loadIssueAdmin", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "loadIssueAdmin");
		}
		return list;
	}

	/**
	 * save Issuetype.
	 * @param collectiontype Collectiontype
	 * @return collectionType Id
	 * @throws MicosException MicosException
	 */
	public Integer createReportProperties(Collectiontype collectiontype)
			throws MicosException {
		EntityManager em = null;
		Integer collectionTypeId = 0;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			collectiontype.setLastupdate(DateUtil.getCurrentTimestamp());
			Issuetype issuetype = (Issuetype) em.find(Issuetype.class,
					collectiontype.getIssuetype().getIssuetypeid());
			collectiontype.setIssuetype(issuetype);

			if (null == collectiontype.getCollectiontypeid()) {
				collectiontype.setCreatedate(DateUtil.getCurrentTimestamp());
				em.persist(collectiontype);
			} else {

				em.merge(collectiontype);
			}
			em.getTransaction().commit();
			collectionTypeId = collectiontype.getCollectiontypeid();

		} catch (Exception exception) {
			LOGGER.error("createReportProperties", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "createReportProperties");
		}
		return collectionTypeId;
	}

	/**
	 * 
	 * @param workingasuserid Integer
	 * @return list of issue types
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Issuetype> getAllIssueType(Integer workingasuserid)
			throws MicosException {

		List<Issuetype> listOfIssueType = new ArrayList<Issuetype>();
		EntityManager em = null;
		try {
			String qrystr = "SELECT * "
				+ " FROM issuetype t, issuegroupadministrator iga "
				+ " WHERE t.available = 'Y' "
				+ " AND t.issuetypeid = iga.issuetypeid "
				+ " AND iga.issuegroupadministrator = " + workingasuserid ;
			em = getEntityManager();
			Query qry = em.createNativeQuery(qrystr, Issuetype.class);
			listOfIssueType = qry.getResultList();
			if (SystemUtil.isNotNull(listOfIssueType)) {
				for (Issuetype issuetype : listOfIssueType) {
					issuetype.getName();
				}
			}

		} catch (Exception exception) {
			LOGGER.error("getAllIssueType", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getAllIssueType");
		}
		return listOfIssueType;
	}

	/**
	 * @param issueTypeId Integer
	 * @return list of issue types
	 * @throws MicosException MicosException
	 *             
	 */
	public Issuetype getIssueTypeDetailsById(Integer issueTypeId)
			throws MicosException {

		Issuetype issuetype = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
			issuetype = (Issuetype) em.find(Issuetype.class, issueTypeId);
			em.refresh(issuetype);
			Set<Issuecodinggroup> setOfissuecoding = issuetype
					.getIssuecodinggroups();
			Set<Issuegroupadministrator> setOfIssuegroupadministrator = issuetype
					.getIssuegroupadministrators();

			if (SystemUtil.isNotNull(setOfIssuegroupadministrator)) {
				for (Issuegroupadministrator issuegroupadministrator : setOfIssuegroupadministrator) {
					Employee employee = issuegroupadministrator.getEmployee();
					employee.getFirstname();
				}
			}
			if (SystemUtil.isNotNull(setOfissuecoding)) {
				for (Issuecodinggroup issuecodinggroup : setOfissuecoding) {
					Employee employee = issuecodinggroup.getEmployee1();
					employee.getFirstname();
				}

			}

			if (SystemUtil.isNotNull(issuetype
					.getCollectionissuenotifications())) {
				for (Collectionissuenotification collectionissuenotification : issuetype
						.getCollectionissuenotifications()) {
					collectionissuenotification.getReferencecode().getCode();
					collectionissuenotification.getPeriodtype()
							.getBaseperiodtype();
				}
			}
		} catch (Exception exception) {
			LOGGER.error("getIssueTypeDetailsById", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getIssueTypeDetailsById");
		}
		return issuetype;
	}

	/**
	 * 
	 * @param workingasuserid Integer
	 * @return boolean
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public boolean createButtonVisible(Integer workingasuserid)
			throws MicosException {
		List<Issuegroupadministrator> listIssuegroupadmin = new ArrayList<Issuegroupadministrator>();
		EntityManager em = null;
		boolean flag = false;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery(
					"select * from issuegroupadministrator t where t.ISSUEGROUPADMINISTRATOR = "
							+ workingasuserid, Issuegroupadministrator.class);
			listIssuegroupadmin = qry.getResultList();
			if (SystemUtil.isNotNull(listIssuegroupadmin)) {
				for (Issuegroupadministrator issuegroupadministrator : listIssuegroupadmin) {
					Issuetype issuetype = issuegroupadministrator
							.getIssuetype();
					if (issuetype.getAvailable().equalsIgnoreCase("Y")) {
						flag = true;
						break;
					}
				}
			}

		} catch (Exception exception) {
			LOGGER.error("createButtonVisible", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "createButtonVisible");
		}
		return flag;
	}

	/**
	 * 
	 * @return List of Hierarchy
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Hierarchy> loadRiskhierarchyForCoding() throws MicosException {
		List<Hierarchy> list = new ArrayList<Hierarchy>();
		EntityManager em = null;
		try {
			em = getEntityManager();
			String hierarchyTypeCode = MicosConstantValues.Hierarchytype.RISK;
			Query qry = em.createNativeQuery(
					"select * from hierarchytype t where t.hierarchycode = '"
							+ hierarchyTypeCode + "'", Hierarchytype.class);
			Hierarchytype hierarchytype = (Hierarchytype) qry.getSingleResult();

			qry = em.createNativeQuery(
					"select * from hierarchy t where t.hierarchytypeid = "
							+ hierarchytype.getHierarchytypeid(),
					Hierarchy.class);

			list = qry.getResultList();
			for (Hierarchy hierarchy : list) {
				hierarchy.getName();
			}

		} catch (Exception exception) {
			LOGGER.error("loadRiskhierarchyForCoding", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "loadRiskhierarchyForCoding");
		}
		return list;
	}

	/**
	 * get CollectionType ID by IssueTypeID.
	 * @param issueTypeId Integer
	 * @return collection type id
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public Integer getCollectionTypeIdByIssueType(Integer issueTypeId)
			throws MicosException {
		Collectiontype newCollectiontype = null;
		Integer collectiontyInteger = null;
		EntityManager em = null;
		try {
			em = getEntityManager();

			Query qry = em.createNativeQuery(
					"select * from Collectiontype t where t.ISSUETYPEID = "
							+ issueTypeId, Collectiontype.class);
			if (qry.getResultList().size() != 0) {
				newCollectiontype = (Collectiontype) qry.getSingleResult();
				collectiontyInteger = newCollectiontype.getCollectiontypeid();
			}

		} catch (Exception exception) {
			LOGGER.error("loadRiskhierarchyForCoding", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "loadRiskhierarchyForCoding");
		}
		return collectiontyInteger;
	}

	/**
	 * 
	 * @param  issuetypeid Integer
	 * @return Collectiontype
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public Collectiontype getReportPropertiesByIssueTypeId(Integer issuetypeid)
			throws MicosException {
		Collectiontype collectiontype = null;
		EntityManager em = null;
		try {
			
			em = getEntityManager();
			
			String qrystr = "select * from collectiontype ct where ct.issuetypeid = " + issuetypeid;
			Query qry = em.createNativeQuery(qrystr, Collectiontype.class);
			List<Collectiontype> resultobj = qry.getResultList();
			if(resultobj != null && resultobj.size() > 0) {
				collectiontype = resultobj.get(0);
				if (collectiontype.getHierarchy() != null) {
					collectiontype.getHierarchy().getHierarchyid();
				}

				Set<Collectionissuenotification> setColissnot = collectiontype.getCollectionissuenotifications();
				if (setColissnot != null) {
					for (Collectionissuenotification colissnot : setColissnot) {
						Periodtype periodtype = colissnot.getPeriodtype();
						periodtype.getName();

						Status status = colissnot.getStatus();
						status.getStatusdescription();

						colissnot.getNotifystatusowner();
					}
				}
				
				collectiontype.getIssuetype().getIssuecodinggroups();
			
			} else {
				collectiontype = null;
			}

		} catch (Exception exception) {
			LOGGER.error("getReportPropertiesByIssueTypeId", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "getReportPropertiesByIssueTypeId");
		}
		return collectiontype;
	}

	/**
	 * loads all period type.
	 * 
	 * @return List Periodtype
	 * @throws MicosException MicosException
	 */
	@SuppressWarnings("unchecked")
	public List<Periodtype> loadAllPeriodType() throws MicosException {
		LOGGER.entering(getClass().getName(), "loadHierarchy");
		List<Periodtype> periodtype = null;
		EntityManager em = null;
		try {
			em = getEntityManager();
			Query qry = em.createNativeQuery("select * from Periodtype order by displayorder ",
					Periodtype.class);
			periodtype = qry.getResultList();
			for (Periodtype period : periodtype) {
				period.getBaseperiodtype();
			}
		} catch (Exception exception) {
			LOGGER.error("loadReferenceCode", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "loadReferenceCode");

		}
		return periodtype;
	}
	
	/**
	 * delete collection issue Notification.
	 * @param collectionissuenotification Collectionissuenotification
	 * @return boolean
	 * @throws MicosException MicosException
	 */
	public boolean deleteCollectionIssue(
			Collectionissuenotification collectionissuenotification)
			throws MicosException {
		EntityManager em = null;
		boolean flag = false;

		try {

			em = getEntityManager();
			if (collectionissuenotification.getIssuetype() != null) {
				em.getTransaction().begin();
				Collectionissuenotification coll = em.find(
						Collectionissuenotification.class,
						collectionissuenotification.getColissnotificationid());
				Issuetype issuetype = coll.getIssuetype();
				em.remove(coll);
				em.refresh(issuetype);
				em.getTransaction().commit();
				flag = true;
			} else if (collectionissuenotification.getCollectiontype() != null) {
				em.getTransaction().begin();
				Collectionissuenotification coll = em.find(
						Collectionissuenotification.class,
						collectionissuenotification.getColissnotificationid());
				Collectiontype collectiontype = coll.getCollectiontype();
				em.remove(coll);
				em.refresh(collectiontype);
				em.getTransaction().commit();
				flag = true;
			}
		} catch (Exception exception) {
			LOGGER.error("deletedAdminGroupList", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			em.close();
			LOGGER.exiting(getClass().getName(), "deletedAdminGroupList");
		}

		return flag;
	}

	/**
	 * Delete value from entity.
	 * @param id id
	 * @param cls Class
	 * @return entity bean object
	 * @throws MicosException an exception
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteCodingGroup(Integer id) throws MicosException {
		LOGGER.entering(getClass().getName(), "deleteAttachment");
		EntityManager em = null;
		boolean result = false;
		Integer collectionId = 0;
		List<Collection> list = null;
		try {
			em = getEntityManager();
			em.getTransaction().begin();
			Issuecodinggroup entityObj = em.find(Issuecodinggroup.class, id);
			em.remove(entityObj);
			Set<Collectiontype> setCollectiontype = entityObj.getIssuetype().getCollectiontypes();
			for(Collectiontype collectiontype: setCollectiontype){
				collectionId = collectiontype.getCollectiontypeid();
			}
			
			Query qry = em.createNativeQuery(
					"select * from collection t where t.COLLECTIONTYPEID = "
							+ collectionId, Collection.class);
			list = qry.getResultList();
			
			for(Collection collection : list){
				Set<Contact> setContacts = collection.getContacts();
				for(Contact contact : setContacts){
					if(contact.getEmployee().getEmployeeid().equals(entityObj.getEmployee1().getEmployeeid())){
						em.remove(contact);
					}
				}
			}
			em.getTransaction().commit();
			result = true;
		} catch (Exception exception) {
			LOGGER.error("deleteAttachment", exception);
			throw new MicosException(ExceptionContext.ERROR, exception,
					SystemUtil.getErrorMessage(exception));
		} finally {
			if(null != em){
					em.close();
			}
			LOGGER.exiting(getClass().getName(), "deleteAttachment");
		}
		return result;
	}

}