/******************************************************************************
 * Copyright(c) 2003-2007 CREDIT SUISSE Financial Services. All Rights Reserved.
 *
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.comp.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.bean.Aorowner;
import com.csg.cs.micos.bean.Control;
import com.csg.cs.micos.bean.ControlCollectionAssignment;
import com.csg.cs.micos.bean.ControlRemWarEsc;
import com.csg.cs.micos.bean.Employee;
import com.csg.cs.micos.bean.EmployeeLeaving;
import com.csg.cs.micos.bean.Employeeroleelement;
import com.csg.cs.micos.bean.Executiontask;
import com.csg.cs.micos.bean.Executor;
import com.csg.cs.micos.bean.Executorpid;
import com.csg.cs.micos.bean.Goal;
import com.csg.cs.micos.bean.Hierarchy;
import com.csg.cs.micos.bean.Hierarchyelement;
import com.csg.cs.micos.bean.RemWarEscOtherRecipient;
import com.csg.cs.micos.bean.Step;
import com.csg.cs.micos.comp.InactiveEmployeeManager;
import com.csg.cs.micos.dao.InactiveEmployeeDao;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.Constants;
import com.csg.cs.micos.util.DateUtil;
import com.csg.cs.micos.util.MicosConstantValues;
import com.csg.cs.micos.util.SystemUtil;
import com.csg.cs.micos.util.MicosConstantValues.EmployeeReassignment;
import com.csg.cs.micos.util.MicosConstantValues.Status;
import com.csg.cs.micos.view.ControlPerformanceTaskView;
import com.csg.cs.micos.view.ControlView;
import com.csg.cs.micos.view.EmployeeAssignmentsView;
import com.csg.cs.micos.view.GoalView;
import com.csg.cs.micos.view.HierarchyElementView;
import com.csg.cs.micos.view.HierarchyRoleView;
import com.csg.cs.micos.view.HierarchyView;
import com.csg.cs.micos.view.InactiveEmployeeView;
import com.csg.cs.micos.view.LanguageCodeView;
import com.csg.cs.micos.view.controlcollections.ControlCollectionView;

/**
 * <code>InactiveEmployeeManagerImpl</code>.
 * 
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 153146 $
 * @version $Id: InactiveEmployeeManagerImpl.java,v 1.2 2009/04/01 07:10:07
 *          172265 Exp $
 */
public class InactiveEmployeeManagerImpl extends BaseManagerImpl implements
		InactiveEmployeeManager {

	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
			.getLogger(InactiveEmployeeManagerImpl.class);

	/**
	 * Holds InactiveEmployeeDao.
	 */
	private InactiveEmployeeDao inactiveEmployeeDao;

	/**
	 * Fetches the list of leaving employees of an administrator.
	 * 
	 * @param employeeId
	 *            Integer
	 * @param showAll
	 *            boolean            
	 * @return List of InactiveEmployeeView
	 * @throws MicosException
	 *             MicosException
	 */
	public List<InactiveEmployeeView> fetchInactiveEmployee(Integer employeeId, boolean showAll)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchInactiveEmp");
		List<InactiveEmployeeView> listOfInactiveEmployee = new ArrayList<InactiveEmployeeView>();
		List<EmployeeLeaving> employeeLeavingList = null;
		InactiveEmployeeView inactiveEmployeeView = null;
		try {
			employeeLeavingList = inactiveEmployeeDao
					.fetchInactiveEmployee(employeeId, showAll);
			if (SystemUtil.isNotNull(employeeLeavingList)) {
				for (EmployeeLeaving employeeLeaving : employeeLeavingList) {
					inactiveEmployeeView = new InactiveEmployeeView();
					inactiveEmployeeView.setId(employeeLeaving
							.getEmployeeLeavingId());
					inactiveEmployeeView.getEmployeeView().setEmpId(
							employeeLeaving.getEmployeeId().getEmployeeid()
									.toString());
					inactiveEmployeeView.getEmployeeView().setSurName(
							employeeLeaving.getEmployeeId().getLastname());
					inactiveEmployeeView.getEmployeeView().setFirstName(
							employeeLeaving.getEmployeeId().getFirstname());
					inactiveEmployeeView.getEmployeeView().setPid(
							employeeLeaving.getEmployeeId().getPid());
					if(employeeLeaving.getLeavingDate() != null) {
						inactiveEmployeeView.setLeavingDate(DateUtil.getFormattedDate(employeeLeaving
								.getLeavingDate(), Constants.OUTPUT_DATE_FORMAT));
					}
					
					inactiveEmployeeView.getEmployeeView().setEmpDescription(
							employeeLeaving.getEmployeeId().getFirstname()
									+ " "
									+ employeeLeaving.getEmployeeId()
											.getLastname()
									+ " "
									+ "("
									+ employeeLeaving.getEmployeeId()
											.getInstradierung() + ")");
					inactiveEmployeeView.getEmployeeView().setTelephone(
							employeeLeaving.getEmployeeId().getTelephone());
					inactiveEmployeeView.getEmployeeView().setDepartment(employeeLeaving.getEmployeeId().getInstradierung() +" - " + employeeLeaving.getEmployeeId().getDepartment() );
					if (employeeLeaving.getEmployeeId().getActive()
							.equalsIgnoreCase("Y")) {
						inactiveEmployeeView.setStatus("Active");
					} else {
						inactiveEmployeeView.setStatus("Inactive");
					}
					listOfInactiveEmployee.add(inactiveEmployeeView);
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("fetchInactiveEmployee", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(), "fetchInactiveEmployee");
		}
		return listOfInactiveEmployee;
	}	
	/**
	 * Fetches the assignments of a particular employee.
	 * 
	 * @param employeeId
	 *            Integer
	 * @param language
	 *            String
	 * @return employeeAssignmentsView EmployeeAssignmentsView
	 * @throws MicosException
	 *             MicosException
	 */
	public EmployeeAssignmentsView fetchEmployeeAssignments(Integer employeeId,
			String language) throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchEmployeeAssignments");
		EmployeeAssignmentsView employeeAssignmentsView = new EmployeeAssignmentsView();

		try {
			employeeAssignmentsView
					.setControlExecutionTasksList(fetchControlExecutionTasks(employeeId));
			employeeAssignmentsView.setGoalOwnerList(fetchGoals(employeeId,
					language));
			employeeAssignmentsView
					.setHierarchyOwnerList(fetchHierarchies(employeeId));
			employeeAssignmentsView
					.setHierarchyAORList(fetchHierarchyAor(employeeId));
			employeeAssignmentsView
					.setControlCollectionAssignmentsList(fetchControlCollectionAssignments(employeeId));
			employeeAssignmentsView
					.setControlStepAssignmentList(fetchControlStepAssgnmnts(
							employeeId, language));
			employeeAssignmentsView.setNotificationAssignmentList(fetchNotificationAssgnmnts(
					employeeId, language));
			employeeAssignmentsView.setHierarchyroleList(fetchHierarchyRoles(employeeId));
		} catch (MicosException micosException) {
			LOGGER.error("fetchEmployeeAssignments", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(), "fetchEmployeeAssignments");
		}
		return employeeAssignmentsView;

	}
	
	/**
	 * Fetches Notification Assignments of an employee.
	 * @param employeeId Integer
	 * @param languageCode String
	 * @return list of ControlView
	 * @throws MicosException Micos Exception
	 */
	@SuppressWarnings("unchecked")
	private List<ControlView> fetchNotificationAssgnmnts(Integer employeeId,
			String languageCode) throws MicosException {
		List<ControlView> controlViewList = new ArrayList<ControlView>();
		List<RemWarEscOtherRecipient> recipientList = null;
		recipientList = inactiveEmployeeDao.fetchNotificationAssignments(employeeId);
		if(SystemUtil.isNotNull(recipientList)){
			for(RemWarEscOtherRecipient recipient : recipientList){
				ControlRemWarEsc controlRemWarEsc = recipient.getControlRemWarEsc();
				ControlView controlView = new ControlView();
				controlView.setMasterControlId(recipient.getRemWarEscOtherRecipientid());
				if(null != controlRemWarEsc.getStepId()){
					controlView = populateControlView(controlRemWarEsc.getStepId(),
							languageCode, controlView);
				}else{
					Control control = controlRemWarEsc.getPowerOption().getControl();
					if (control != null) {
						controlView.setId(control.getControlid());			
						controlView.setControlTitleObj(populateControlTitle(controlView
								.getId()));
						controlView.setStepTitle(controlRemWarEsc.getPowerOption().getTitle());

					}
				}
				controlView.setDescription(controlRemWarEsc.getNotType().getShortdescription());
				controlViewList.add(controlView);
			}
		}
		return controlViewList;
	}

	/**
	 * Fetches control step assignments of an employee.
	 * 
	 * @param employeeId
	 *            Integer
	 * @param languageCode
	 *            String
	 * @return List of ControlView
	 * @throws MicosException
	 *             MicosException
	 */
	@SuppressWarnings("unchecked")
	private List<ControlView> fetchControlStepAssgnmnts(Integer employeeId,
			String languageCode) throws MicosException {
		List<Executorpid> executorPidList = null;
		executorPidList = inactiveEmployeeDao
				.fetchControlStepAssignments(employeeId);
		Set<Step> steps = null;
		List<ControlView> controlViews = new ArrayList<ControlView>();
		for (Executorpid executorPid : executorPidList) {
			Executor executor = executorPid.getExecutor();
			Integer supportSchemaId = null;
			Integer executorExeSchemaId = null;
			Integer executorQASchemaId = null;
			Integer executorSignOffSchemaId = null;
			ControlView controlView = new ControlView();
			controlView.setExecutorPidId(executorPid.getExecutorpidid());
			if (executor.getSupportschema() != null) {
				steps = executor.getSupportschema().getSteps();
				supportSchemaId = executor.getSupportschema()
						.getSupportschemaid();
			} else if (executor.getExecutionschema() != null) {
				steps = executor.getExecutionschema().getSteps();
				executorExeSchemaId = executor.getExecutionschema()
						.getExecutionschemaid();
			} else if (executor.getQaschema() != null) {
				steps = executor.getQaschema().getSteps();
				executorQASchemaId = executor.getQaschema().getQaschema();
			} else if (executor.getSignoffschema() != null) {
				steps = executor.getSignoffschema().getSteps();
				executorSignOffSchemaId = executor.getSignoffschema()
						.getSignoffschemaid();
			}
			if (steps != null) {
				Iterator<Step> iterator = steps.iterator();
				while (iterator.hasNext()) {
					Step step = iterator.next();
					if(!step.getSetup().getControl().getStatus().getStatuscode().equalsIgnoreCase(MicosConstantValues.Status.INACTIVE)) {
						if (step.getSupportschema() != null) {
							if (step.getSupportschema().getSupportschemaid()
									.equals(supportSchemaId)) {
								controlView = populateControlView(step,
										languageCode, controlView);
								controlView
										.setDescriptionType(MicosConstantValues.SchemaType.SUPPORT);
								controlViews.add(controlView);
							}
						} 
						if (step.getExecutionschema() != null && step.getStepType().equalsIgnoreCase("S")) {
							if (step.getExecutionschema().getExecutionschemaid()
									.equals(executorExeSchemaId)) {
								controlView = populateControlView(step,
										languageCode, controlView);
								controlView
										.setDescriptionType(MicosConstantValues.SchemaType.EXECUTION);
								controlViews.add(controlView);
							}
						} 
						if (step.getQaschema() != null) {
							if (step.getQaschema().getQaschema().equals(
									executorQASchemaId)) {
								controlView = populateControlView(step,
										languageCode, controlView);
								controlView
										.setDescriptionType(MicosConstantValues.SchemaType.QA);
								controlViews.add(controlView);
							}
						}
						if (step.getSignoffschema() != null && step.getStepType().equalsIgnoreCase("S")) {
							if (step.getSignoffschema().getSignoffschemaid()
									.equals(executorSignOffSchemaId)) {
								controlView = populateControlView(step,
										languageCode, controlView);
								controlView
										.setDescriptionType(MicosConstantValues.SchemaType.SIGNOFF);
								controlViews.add(controlView);
							}
						}
					}
				}
			}
		}
		return controlViews;
	}
	
	/**
	 * @param employeeid
	 *            employeeid
	 * @return list of hierarchy roles assigned to user
	 * @throws MicosException
	 *             MicosException
	 */
	private List<HierarchyRoleView> fetchHierarchyRoles(Integer employeeid)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchHierarchyRoles");
		List<HierarchyRoleView> hierarchyRoles = new ArrayList<HierarchyRoleView>();
		List<Object[]> hierarchRoleObjArrList = inactiveEmployeeDao
				.fetchHierarchyRoles(employeeid);
		if(hierarchRoleObjArrList != null) {
			for(Object[] hierarchRoleObj : hierarchRoleObjArrList) {
				HierarchyRoleView hierarchyRole = new HierarchyRoleView();
				
				hierarchyRole.setId((Integer) hierarchRoleObj[0]);
				hierarchyRole.setHierarchyId((Integer) hierarchRoleObj[1]);
				hierarchyRole.setHierarchyName((String) hierarchRoleObj[2]);
				hierarchyRole.setHierarchyElementId((Integer) hierarchRoleObj[3]);
				hierarchyRole.setHierarchyElementName((String) hierarchRoleObj[4]);
				hierarchyRole.setHierarchyRoleTitle((String) hierarchRoleObj[5]);
				
				hierarchyRoles.add(hierarchyRole);
			}
		}
 
		return hierarchyRoles;

	}

	/**
	 * Populates control view.
	 * 
	 * @param step
	 *            Step
	 * @param languageCode
	 *            String
	 * @param controlView 
	 *             ControlView
	 * @return controlView 
	 *              ControlView
	 * @throws MicosException
	 *             MicosException
	 */
	private ControlView populateControlView(Step step, String languageCode,
			ControlView controlView) throws MicosException {
		Control control = step.getSetup().getControl();
		if (control != null) {
			controlView.setId(control.getControlid());			
			controlView.setControlTitleObj(populateControlTitle(controlView
					.getId()));
			controlView.setStepTitle(step.getTitle());

		}

		return controlView;
	}

	/**
	 * To populate Control Title.
	 * 
	 * @param controlId
	 *            Integer
	 * @return langCntrlTitle An Object Array of LanguageCodeView
	 * @throws MicosException
	 *             MicosException
	 */
	public LanguageCodeView[] populateControlTitle(Integer controlId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "populateControlTitle");
		List langCodes = SystemUtil.getAllLanguageCode();
		LanguageCodeView[] langCntrlTitle = new LanguageCodeView[langCodes
				.size()];
		Iterator itr = langCodes.iterator();
		String langCode = Constants.EMPTY_STRING;
		int count = 0;
		while (itr.hasNext()) {
			langCode = (String) itr.next();
			langCntrlTitle[count] = new LanguageCodeView();
			langCntrlTitle[count].setLanguageType(langCode);
			langCntrlTitle[count].setValue(SystemUtil
					.getClobValueAsString(inactiveEmployeeDao
							.findControlTitleForControl(langCode, controlId)));
			count++;
		}
		LOGGER.exiting(getClass().getName(), "populateControlTitle");
		return langCntrlTitle;
	}

	/**
	 * Fetches the control execution tasks of an employee.
	 * 
	 * @param employeeId
	 *            Integer
	 * @return List of ExecutionTasks.
	 * @throws MicosException
	 *             MicosException
	 */
	private List<ControlPerformanceTaskView> fetchControlExecutionTasks(
			Integer employeeId) throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchControlExecutionTasks");
		List<ControlPerformanceTaskView> taskViewList = new ArrayList<ControlPerformanceTaskView>();
		List<Executiontask> taskList = null;
		ControlPerformanceTaskView taskView = null;
		try {
			taskList = inactiveEmployeeDao
					.fetchControlExecutionTasks(employeeId);
			if (SystemUtil.isNotNull(taskList)) {
				for (Executiontask task : taskList) {
					taskView = new ControlPerformanceTaskView();
					taskView.setId(task.getExecutiontaskid());
					taskView.setTaskTitle(task.getExecutionschedule().getStep()
							.getTitle());
					taskView.setControlTitleObj(populateControlTitle(task
							.getExecutionschedule().getStep().getSetup()
							.getControl().getControlid()));
					taskView.setDueDate(DateUtil.getFormattedDate(task.getExecutionenddate(), Constants.OUTPUT_DATE_FORMAT));
					if (task.getSupportschema() != null) {
						taskView
								.setTaskType(MicosConstantValues.SchemaType.SUPPORT);
					}
					if (task.getExecutionschema() != null) {
						taskView
								.setTaskType(MicosConstantValues.SchemaType.EXECUTION);
					}
					if (task.getQaschema() != null) {
						taskView.setTaskType(MicosConstantValues.SchemaType.QA);
					}
					if (task.getSignoffschema() != null) {
						taskView
								.setTaskType(MicosConstantValues.SchemaType.SIGNOFF);
					}

					taskViewList.add(taskView);
				}

			}
		} catch (MicosException micosException) {
			LOGGER.error("fetchControlExecutionTasks", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(), "fetchControlExecutionTasks");
		}

		return taskViewList;

	}

	/**
	 * Fetches goals of an employee.
	 * 
	 * @param employeeId
	 *            Integer
	 * @param language
	 *            String
	 * @return List of Goals
	 * @throws MicosException
	 *             MicosException
	 */
	private List<GoalView> fetchGoals(Integer employeeId, String language)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchGoals");
		List<GoalView> goalViewList = new ArrayList<GoalView>();
		List<Goal> goalList = null;
		GoalView goalView = null;
		try {
			goalList = inactiveEmployeeDao.fetchGoals(employeeId);
			if (SystemUtil.isNotNull(goalList)) {
				for (Goal goal : goalList) {
					goalView = new GoalView();
					goalView.setId(goal.getGoalid());
					goalView.setGoalTitle(SystemUtil
							.getClobValueAsString(inactiveEmployeeDao
									.findGoalTitleForGoal(language, goalView
											.getId())));
					goalViewList.add(goalView);
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("fetchGoals", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(), "fetchGoals");
		}
		return goalViewList;
	}

	/**
	 * Fetches hierarchies of an employee.
	 * 
	 * @param employeeId
	 *            Integer
	 * @return List of HierarchyView
	 * @throws MicosException
	 *             MicosException
	 */
	private List<HierarchyView> fetchHierarchies(Integer employeeId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchHierarchies");
		List<HierarchyView> hierarchyViewList = new ArrayList<HierarchyView>();
		List<Hierarchy> hierarchyList = null;
		HierarchyView hierarchyView = null;
		try {
			hierarchyList = inactiveEmployeeDao.fetchHierarchies(employeeId);
			if (SystemUtil.isNotNull(hierarchyList)) {
				for (Hierarchy hierarchy : hierarchyList) {
					hierarchyView = new HierarchyView();
					hierarchyView.setId(hierarchy.getHierarchyid());
					hierarchyView.setHierarchy(hierarchy.getName());
					hierarchyViewList.add(hierarchyView);
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("fetchHierarchies", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(), "fetchHierarchies");
		}
		return hierarchyViewList;
	}

	/**
	 * Fetches the hierarchy Aors of a particular employee.
	 * 
	 * @param employeeId
	 *            Integer
	 * @return List of HierarchyElementViews.
	 * @throws MicosException
	 *             MicosException
	 */
	private List<HierarchyElementView> fetchHierarchyAor(Integer employeeId)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "fetchHierarchyAor");
		List<HierarchyElementView> hierarchyElementViewList = new ArrayList<HierarchyElementView>();
		List<Hierarchyelement> hierarchyElementList = null;
		HierarchyElementView hierarchyElementView = null;
		try {
			hierarchyElementList = inactiveEmployeeDao
					.fetchHierarchiesAOR(employeeId);
			if (SystemUtil.isNotNull(hierarchyElementList)) {
				for (Hierarchyelement hierarchyElement : hierarchyElementList) {
					hierarchyElementView = new HierarchyElementView();
					hierarchyElementView.setId(hierarchyElement
							.getHierarchyelementid());
					hierarchyElementView.setAorOwnerId(hierarchyElement
							.getAorOwnerId());
					hierarchyElementView.setTitle(hierarchyElement.getHierarchy().getName() +" - "+ hierarchyElement.getName());
					hierarchyElementView.setHierarchyId(hierarchyElement.getHierarchy().getHierarchyid());
					hierarchyElementViewList.add(hierarchyElementView);
				}
			}
		} catch (MicosException micosException) {
			LOGGER.error("fetchHierarchyAor", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(), "fetchHierarchyAor");
		}
		return hierarchyElementViewList;
	}
	
	/**
	 * Fetches control collection assignments of an employee.
	 * 
	 * @param employeeId
	 *            Integer
	 * @return List of ControlCollectionView.
	 * @throws MicosException
	 *             MicosException
	 */
	private List<ControlCollectionView> fetchControlCollectionAssignments(
			Integer employeeId) throws MicosException {
		LOGGER.entering(getClass().getName(),
				"fetchControlCollectionAssignments");
		List<ControlCollectionView> controlCollectionAssignmentsList = new ArrayList<ControlCollectionView>();
		List<ControlCollectionAssignment> controlCollectionAssignmentList = null;
		ControlCollectionView controlCollectionView = null;
		try {
			controlCollectionAssignmentList = inactiveEmployeeDao
					.fetchControlCollectionAssignments(employeeId);
			if (SystemUtil.isNotNull(controlCollectionAssignmentList)) {
				for (ControlCollectionAssignment collectionAssignment : controlCollectionAssignmentList) {
					controlCollectionView = new ControlCollectionView();
					controlCollectionView
							.setControlCollectionAssignmentId(collectionAssignment
									.getControlCollectionAssignmentId());
					controlCollectionView.setId(collectionAssignment
							.getControlCollection().getControlCollectionId());							
					controlCollectionView.setTitle(collectionAssignment.getControlCollection()
							.getCollectionTitle());
					controlCollectionAssignmentsList.add(controlCollectionView);
				}

			}
		} catch (MicosException micosException) {
			LOGGER.error("fetchControlCollectionAssignments", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(),
					"fetchControlCollectionAssignments");
		}
		return controlCollectionAssignmentsList;
	}

	/**
	 * Updates the status of employeeLeaving table.
	 * 
	 * @param employeeId
	 *            Integer
	 * @param language
	 *            selected language
	 * @return  assignTaskStatus  String        
	 * @throws MicosException
	 *             MicosException
	 */
	public String updateStatus(Integer employeeId, String language)
			throws MicosException {
		LOGGER.entering(getClass().getName(), "updateStatus");
		EmployeeAssignmentsView employeeAssignmentsView = null;
		EmployeeLeaving employeeLeaving = new EmployeeLeaving();
		int[] sizeArray = new int[7];
		String assignTaskStatus = null;
		try {
			Employee employee = inactiveEmployeeDao.loadEmployee(employeeId);
			employeeAssignmentsView = fetchEmployeeAssignments(
					employeeId, language);
			sizeArray[0] = employeeAssignmentsView
					.getControlExecutionTasksList().size();
			sizeArray[1] = employeeAssignmentsView
					.getControlStepAssignmentList().size();
			sizeArray[2] = employeeAssignmentsView.getGoalOwnerList().size();
			sizeArray[3] = employeeAssignmentsView.getHierarchyOwnerList()
					.size();
			sizeArray[4] = employeeAssignmentsView.getHierarchyAORList().size();
			sizeArray[5] = employeeAssignmentsView
					.getHierarchyroleList().size();
			sizeArray[6] = employeeAssignmentsView
			.getControlCollectionAssignmentsList().size();
			if (sizeArray[0] == 0 && sizeArray[1] == 0 && sizeArray[2] == 0
					&& sizeArray[3] == 0 && sizeArray[4] == 0
					&& sizeArray[5] == 0 && sizeArray[5] == 0 && employee.getActive().equalsIgnoreCase("N")) {
				employeeLeaving = inactiveEmployeeDao
						.fetchInactiveEmployeeDetails(employeeId);
				employeeLeaving.setStatusId(inactiveEmployeeDao
						.getStatusByCode(MicosConstantValues.Status.COMPLETED));
				employeeLeaving.setLastupdate(employeeLeaving.getLastupdate());
				employeeLeaving.setEmployeeByLastupdatedby(inactiveEmployeeDao
						.loadEmployee(getLoggedinUser()));
				inactiveEmployeeDao.updateStatus(employeeLeaving);
				assignTaskStatus=MicosConstantValues.Status.COMPLETED;
			}

		} catch (MicosException micosException) {
			LOGGER.error("updateStatus", micosException);
			throw new MicosException(micosException);
		} finally {
			LOGGER.exiting(getClass().getName(), "updateStatus");
		}
		return assignTaskStatus;
	}


	/**
	 * To assign tasks to selected Employee.
	 * 
	 * @param assignmentsView
	 *            EmployeeAssignmentsView
	 * @param newEmpId
	 *            Integer
	 * @param oldEmpId
	 *            Integer
	 * @param selLang
	 *            String
	 * @return returns the result code             
	 * @throws MicosException
	 *             MicosException
	 */
	public int assignTasks(EmployeeAssignmentsView assignmentsView,
			Integer newEmpId, Integer oldEmpId, String selLang)
			throws MicosException {
		Employee employee = inactiveEmployeeDao.loadEmployee(newEmpId);
		Employee loggedInUser = inactiveEmployeeDao
				.loadEmployee(getLoggedinUser());
		List<ControlPerformanceTaskView> executionTaskList = assignmentsView
				.getControlExecutionTasksList();
		List<Executiontask> listExecutiontask = new ArrayList<Executiontask>();
		Object[] taskArray = new Object[8];
		for (ControlPerformanceTaskView taskView : executionTaskList) {
			if (taskView.isSelected()) {
				Executiontask executiontask = populateExecutionTaskfromView(
						taskView, employee);
				executiontask.setEmployee(loggedInUser);
				listExecutiontask.add(executiontask);
			}
		}
		taskArray[0] = listExecutiontask;
		List<ControlView> listControlView = assignmentsView.getControlStepAssignmentList();
		List<Executorpid> executorPidList = new ArrayList<Executorpid>();
		for (ControlView controlView : listControlView) {
			if (controlView.isSelected()) {
				Executorpid executorpid = populateExecutorPidfromView(
						controlView, employee);
				executorPidList.add(executorpid);
			}
		}
		taskArray[1] = executorPidList;
		List<GoalView> listGoalView = assignmentsView.getGoalOwnerList();
		List<Goal> listGoal = new ArrayList<Goal>();
		for (GoalView goalView : listGoalView) {
			if (goalView.isSelected()) {
				Goal goal = populateGoalfromView(goalView, employee);
				goal.setEmployeeByLastupdatedby(loggedInUser);
				listGoal.add(goal);
			}
		}
		taskArray[2] = listGoal;
		List<HierarchyView> listHierarchyView = assignmentsView.getHierarchyOwnerList();
		List<Hierarchy> listHierarchy = new ArrayList<Hierarchy>();
		for (HierarchyView hierarchyView : listHierarchyView) {
			if (hierarchyView.isSelected()) {
				Hierarchy hierarchy = populateHierarchyfromView(hierarchyView,employee);
				hierarchy.setEmployeeByLastupdatedby(loggedInUser);
				listHierarchy.add(hierarchy);
			}
		}
		taskArray[3] = listHierarchy;
		List<HierarchyElementView> listHrchyElmntView = assignmentsView.getHierarchyAORList();
		List<Hierarchyelement> listHierarchyElmnt = new ArrayList<Hierarchyelement>();
		for (HierarchyElementView hierarchyElementView : listHrchyElmntView) {
			if (hierarchyElementView.isSelected()) {
				Hierarchyelement hierarchyelement = populateHrchyElmntfromView(
						hierarchyElementView, employee);
				hierarchyelement.setEmployee(loggedInUser);
				listHierarchyElmnt.add(hierarchyelement);
			}
		}
		taskArray[4] = listHierarchyElmnt;
		
		List<HierarchyRoleView> listHierarchyrolesView = assignmentsView.getHierarchyroleList();
		List<Employeeroleelement> listHierarchyroles = new ArrayList<Employeeroleelement>();
		for (HierarchyRoleView hierarchyRoleView : listHierarchyrolesView) {
			if (hierarchyRoleView.isSelected()) {
				Employeeroleelement employeeroleelement = populateEmpRoleElementfromView(hierarchyRoleView, employee);
				employeeroleelement.setEmployeeByLastupdatedby(loggedInUser);
				listHierarchyroles.add(employeeroleelement);
			}
		}
		taskArray[5] = listHierarchyroles;
		
		List<ControlCollectionView> listControlCollectionView = assignmentsView
				.getControlCollectionAssignmentsList();
		List<ControlCollectionAssignment> listCntrlClectnAsgnmnt = new ArrayList<ControlCollectionAssignment>();
		for (ControlCollectionView controlCollectionView : listControlCollectionView) {
			if (controlCollectionView.isSelected()) {
				ControlCollectionAssignment controlCollectionAssignment = populateCntrlColctnfromView(
						controlCollectionView, employee);
				controlCollectionAssignment.setLastUpdatedBy(loggedInUser);
				listCntrlClectnAsgnmnt.add(controlCollectionAssignment);
			}
		}
		taskArray[6] = listCntrlClectnAsgnmnt;
		
		List<ControlView> notificationList = assignmentsView.getNotificationAssignmentList();
		List<RemWarEscOtherRecipient> recipientList = new ArrayList<RemWarEscOtherRecipient>();
		for(ControlView controlView : notificationList){
			if(controlView.isSelected()){
				RemWarEscOtherRecipient otherRecipient = populateRecipientIdFromView(controlView, employee);
				recipientList.add(otherRecipient);
			}
		}
		taskArray[7] = recipientList;
		
		int result = inactiveEmployeeDao.assignTasks(taskArray, newEmpId, oldEmpId,
				getLoggedinUser());
		if(result == EmployeeReassignment.REASSIGNMENT_SUCCESS_CODE) {
			String status = updateStatus(oldEmpId, selLang);
			if(status != null && status.equalsIgnoreCase(Status.COMPLETED)) {
				result = EmployeeReassignment.NO_ASSIGNMENTS;
			}
		}
	
		return result;
	}

	/**
	 * To populate Control Collection from View.
	 * 
	 * @param controlCollectionView ControlCollectionView
	 * @param employee Employee
	 * @return controlCollection ControlCollection
	 */
	private ControlCollectionAssignment populateCntrlColctnfromView(
			ControlCollectionView controlCollectionView, Employee employee) {
		ControlCollectionAssignment collectionAssignment = new ControlCollectionAssignment();
		collectionAssignment.setControlCollectionAssignmentId(controlCollectionView.getControlCollectionAssignmentId());
		collectionAssignment.setEmployee(employee);
		return collectionAssignment;
	}
	
	/**
	 * To populate employee role element from View.
	 * 
	 * @param hierarchyRoleView HierarchyRoleView
	 * @param employee Employee
	 * @return controlCollection ControlCollection
	 */
	private Employeeroleelement populateEmpRoleElementfromView(
			HierarchyRoleView hierarchyRoleView, Employee employee) {
		Employeeroleelement emperoleelement = new Employeeroleelement();
		emperoleelement.setEmployeeroleelementid(hierarchyRoleView.getId());
		emperoleelement.setEmployeeByEmployeeid(employee);
		return emperoleelement;
	}

	/**
	 * To populate Hierachy Element from View.
	 * 
	 * @param hierarchyElementView HierarchyElementView
	 * @param employee Employee
	 * @return hierarchyelement Hierarchyelement
	 */
	private Hierarchyelement populateHrchyElmntfromView(
			HierarchyElementView hierarchyElementView, Employee employee) {
		Hierarchyelement hierarchyelement = new Hierarchyelement();
		Aorowner aorowner = new Aorowner();

		hierarchyelement.setHierarchyelementid(hierarchyElementView.getId());
		aorowner.setEmployeeByAorowner(employee);
		hierarchyelement.setAorowner(aorowner);
		return hierarchyelement;
	}

	/**
	 * To populate Hierarchy from View.
	 * 
	 * @param hierarchyView HierarchyView
	 * @param employee  Employee
	 * @return hierarchy Hierarchy
	 */
	private Hierarchy populateHierarchyfromView(HierarchyView hierarchyView,
			Employee employee) {
		Hierarchy hierarchy = new Hierarchy();
		hierarchy.setHierarchyid(hierarchyView.getId());
		hierarchy.setEmployeeByHierarchyowner(employee);
		return hierarchy;
	}

	/**
	 * To populate Goal from View.
	 * 
	 * @param goalView GoalView
	 * @param employee Employee
	 * @return goal Goal
	 */
	private Goal populateGoalfromView(GoalView goalView, Employee employee) {
		Goal goal = new Goal();
		goal.setGoalid(goalView.getId());
		goal.setEmployeeByGoalowner(employee);
		return goal;
	}

	/**
	 * To populate Executorpid from View.
	 * 
	 * @param controlView ControlView
	 * @param employee Employee
	 * @return executorpid Executorpid
	 */
	private Executorpid populateExecutorPidfromView(ControlView controlView,
			Employee employee) {
		Executorpid executorpid = new Executorpid();
		executorpid.setExecutorpidid(controlView.getExecutorPidId());
		executorpid.setEmployeeByEmployeeid(employee);
		return executorpid;
	}
	
	/**
	 * To populate RemWarEscOtherRecipient id from view.
	 * @param controlView ControlView
	 * @param employee Employee
	 * @return otherRecipientId RemWarEscOtherRecipient
	 */
	private RemWarEscOtherRecipient populateRecipientIdFromView(ControlView controlView,
			Employee employee){
		RemWarEscOtherRecipient otherRecipientId = new RemWarEscOtherRecipient();
		otherRecipientId.setRemWarEscOtherRecipientid(controlView.getMasterControlId());
		otherRecipientId.setEmployee(employee);
		return otherRecipientId;
	}

	/**
	 * To populate Execution Task from View.
	 * 
	 * @param taskView ExecutionTaskView
	 * @param employee Employee
	 * @return executiontask Executiontask
	 */
	private Executiontask populateExecutionTaskfromView(
			ControlPerformanceTaskView taskView, Employee employee) {
		Executiontask executiontask = new Executiontask();
		executiontask.setExecutiontaskid(taskView.getId());
		executiontask.setExecutor(employee);
		return executiontask;
	}

	/**
	 * @return Returns the inactiveEmployeeDao.
	 */
	public InactiveEmployeeDao getInactiveEmployeeDao() {
		return inactiveEmployeeDao;
	}

	/**
	 * @param inactiveEmployeeDao
	 *            The inactiveEmployeeDao to set.
	 */
	public void setInactiveEmployeeDao(InactiveEmployeeDao inactiveEmployeeDao) {
		this.inactiveEmployeeDao = inactiveEmployeeDao;
	}	
}
