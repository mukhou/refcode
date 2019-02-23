/******************************************************************************
 * Copyright(c) 2003-2010 CREDIT SUISSE Financial Services. All Rights Reserved.
 *
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/
package com.csg.cs.micos.controller.governanceandadmin.issues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlForm;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.controller.BaseController;
import com.csg.cs.micos.controller.controlperformance.ControlInformationController;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.helper.issues.IssueTypeSetupHelper;
import com.csg.cs.micos.util.Constants;
import com.csg.cs.micos.util.DateUtil;
import com.csg.cs.micos.util.NavigationConstants;
import com.csg.cs.micos.util.SystemUtil;
import com.csg.cs.micos.util.IssuesConstants.CollectionTabPane;
import com.csg.cs.micos.util.IssuesConstants.IssueTypeConstants;
import com.csg.cs.micos.view.EmployeeView;
import com.csg.cs.micos.view.governanceandadmin.issues.IssueTypeView;
import com.csg.cs.micos.view.governanceandadmin.issues.UpdateReminderView;

/**
 * 
 * <code>IssueTypeSetupController</code> checks functionality of Issue type
 * setup screen.
 * 
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 153146 $
 * @version $Id: IssueCreatorGroupAssignmentController.java,v 1.2 2009/03/03
 *          11:31:29 f542625 Exp $
 */
public class IssueTypeSetupController extends BaseController implements
		Serializable {

	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
			.getLogger(ControlInformationController.class);

	/**
	 * Html form.
	 */
	private HtmlForm init;

	/**
	 * To check whether the collection is new or existing.
	 */
	private String mode;

	/**
	 * Reference to EmployeeView.
	 */
	private EmployeeView selectedEmp;

	/**
	 * Holds the issueId.
	 */
	private Integer issueTypeId;

	/**
	 * helper.
	 */
	private IssueTypeSetupHelper helper;

	/**
	 * @return Returns the helper.
	 */
	public IssueTypeSetupHelper getHelper() {
		if (null == helper) {
			helper = new IssueTypeSetupHelper();
		}
		return helper;
	}

	/**
	 * get the html form.
	 * 
	 * @return init
	 */
	public HtmlForm getInit() {
		LOGGER.entering(getClass().getName(), "getInit");

		init = new HtmlForm();
		setFrompage(getParameterValueFromRequest("fromPage"));

		if (null == getHelper().getIssueTypeView()) {
			getHelper().setIssueTypeView(new IssueTypeView());
		}
		// get issue type id
		if (getParameterValueFromRequest(IssueTypeConstants.READ_ISSUETYPE_ID) != null) {
			issueTypeId = Integer
					.parseInt(getParameterValueFromRequest(IssueTypeConstants.READ_ISSUETYPE_ID));
		}
		if (getFrompage() != null
				&& getFrompage().equals(
						NavigationConstants.COMMON_EMPPLOYEE_SEARCH)) {
			getHelper().fromCommonEmployeeFlagSet();

		} else {
			getHelper().flagSet();
			populateIssueTypesView();
		}

		if (null != getParameterValueFromRequest(IssueTypeConstants.MODE)) {
			mode = getParameterValueFromRequest(IssueTypeConstants.MODE);
			if (IssueTypeConstants.CREATE.equals(getMode())) {
				getHelper().checkCreateMode();
			}
			if (IssueTypeConstants.EDIT.equals(getMode())) {
				getHelper().checkEditeMode();
			}
			if (IssueTypeConstants.READ_ONLY.equals(getMode())) {
				getHelper().setReadOnlyModes();
			}
		}

		if (null == getHelper().getIssueTypeView()) {
			getHelper().setIssueTypeView(new IssueTypeView());
		}
		SystemUtil.setCurrentPageToSession(IssueTypeConstants.ISSUETYPESETUP);
		LOGGER.exiting(getClass().getName(), "getInit");
		return init;
	}

	/**
	 * Populates the Issue Type Setup page.
	 * 
	 */
	private void populateIssueTypesView() {
		LOGGER.entering(getClass().getName(), "populateIssueTypesView");
		try {

			if (issueTypeId != null) {
				getHelper().setIssueTypeView(
						getHelper().getIssueTypeManager()
								.getIssueTypeDetailsById(issueTypeId));
			} else {
				EmployeeView employee = getHelper().getIssueTypeManager()
						.issueCreatorVisible();
				String string = employee.getFirstName() + " ("
						+ employee.getPid() + ") - ";
				getHelper().getIssueTypeView().setCreateDateUser(
						string + DateUtil.getCurrentTimestamp());
				getHelper().getIssueTypeView().setLastupdateDateUser(
						string + DateUtil.getCurrentTimestamp());
			}

			if (null == getHelper().getIssueTypeView().getUpdateRequiredView()) {
				UpdateReminderView updateReminderView = new UpdateReminderView();
				getHelper().getIssueTypeView().setUpdateRequiredView(
						updateReminderView);
			}

			if (null == getHelper().getIssueTypeView().getReminderView()) {
				UpdateReminderView updateReminderView = new UpdateReminderView();
				getHelper().getIssueTypeView().setReminderView(
						updateReminderView);
			}
			if (null == getHelper().getIssueTypeView().getWarningView()) {
				UpdateReminderView updateReminderView = new UpdateReminderView();
				getHelper().getIssueTypeView().setWarningView(
						updateReminderView);
			}

			List<SelectItem> listSelectItem = getHelper().getIssueTypeManager()
					.loadAllPeriodType();
			getHelper().getIssueTypeView().getWarningView().setPeriodType(
					listSelectItem);
			getHelper().getIssueTypeView().getUpdateRequiredView()
					.setPeriodType(listSelectItem);
			getHelper().getIssueTypeView().getReminderView().setPeriodType(
					listSelectItem);

			if (null == getHelper().getIssueTypeView().getUpdateRequiredView()
					.getSelectedperiodType()) {
				getHelper().getIssueTypeView().getUpdateRequiredView()
						.setSelectedperiodType(
								String
										.valueOf(listSelectItem.get(0)
												.getValue()));
			}

			if (null == getHelper().getIssueTypeView().getReminderView()
					.getSelectedperiodType()) {
				getHelper().getIssueTypeView().getReminderView()
						.setSelectedperiodType(
								String
										.valueOf(listSelectItem.get(0)
												.getValue()));
			}

			if (null == getHelper().getIssueTypeView().getWarningView()
					.getSelectedperiodType()) {
				getHelper().getIssueTypeView().getWarningView()
						.setSelectedperiodType(
								String
										.valueOf(listSelectItem.get(0)
												.getValue()));
			}

			List<SelectItem> newlist = new ArrayList<SelectItem>();
			newlist.add(new SelectItem("0", "every"));
			newlist.add(new SelectItem("1", "once"));
			getHelper().getIssueTypeView().getWarningView().setListofDaysIssue(
					newlist);
			if (getHelper().getIssueTypeView().getWarningView()
					.getSelectedDaysIssue() == null) {
				getHelper().getIssueTypeView().getWarningView()
						.setSelectedDaysIssue("0");
			}
		} catch (MicosException micosException) {
			LOGGER.error("issueDetails", micosException);
			addErrorMessage(micosException.getMessage());
		}
	}

	/**
	 * It will go from Issue Type Properties Tab to Report Properties Tab.
	 * 
	 * @return String
	 */
	public String openReportPropertiesTab() {
		LOGGER.entering(getClass().getName(), "openReportPropertiesTab");

		getRequest().setAttribute(IssueTypeConstants.READ_ISSUETYPE_ID,
				issueTypeId);

		if (IssueTypeConstants.EDIT.equals(getMode())) {
			getRequest().setAttribute(IssueTypeConstants.MODE,
					IssueTypeConstants.EDIT);
		}
		if (IssueTypeConstants.READ_ONLY.equals(getMode())) {
			getRequest().setAttribute(IssueTypeConstants.MODE,
					IssueTypeConstants.READ_ONLY);
		}
		removeSessionAttribute("reportPropertiesController");

		LOGGER.exiting(getClass().getName(), "openReportPropertiesTab");
		return "reportProperties";

	}

	/**
	 * The method invoked when the standalone issue checkbox value is changed.
	 * 
	 * @param evnt
	 *            ValueChangeEvent.
	 */
	public void changeReportTab(ValueChangeEvent evnt) {
		Boolean stdAlone = (Boolean) evnt.getNewValue();
		if (stdAlone || "create".equals(getMode())) {
			getHelper().getFlagHelper().setReportTab(false);
		} else {
			getHelper().getFlagHelper().setReportTab(true);
		}

	}

	/**
	 * Cancel the issue type.
	 * 
	 * @return string
	 */
	public String cancelIssueType() {
		removeSessionAttribute("issueTypeSetupController");
		return "issueTypeSetup";
	}

	/**
	 * Save the issue type.
	 * 
	 * @return string
	 */
	public String saveIssueType() {
		String navigation = null;
		try {
			if (validate()) {
				if (SystemUtil.isNotNull(getHelper().getIssueTypeView()
						.getCodingGroup())) {
					List<EmployeeView> listEemployeeView = new ArrayList<EmployeeView>();
					for (EmployeeView employeeView : getHelper()
							.getIssueTypeView().getCodingGroup()) {
						if (null == employeeView.getId()) {
							listEemployeeView.add(employeeView);
						}
					}
					getHelper().getIssueTypeView().setCodingGroup(
							listEemployeeView);
				}
				if (SystemUtil.isNotNull(getHelper().getIssueTypeView()
						.getGroupAdmins())) {
					List<EmployeeView> listEemployeeView = new ArrayList<EmployeeView>();
					for (EmployeeView employeeView : getHelper()
							.getIssueTypeView().getGroupAdmins()) {
						if (null == employeeView.getId()) {
							listEemployeeView.add(employeeView);
						}
					}
					getHelper().getIssueTypeView().setGroupAdmins(
							listEemployeeView);
				}

				getHelper().getIssueTypeView().setId(
						getHelper().getIssueTypeManager()
								.createIssueTypeProperties(
										getHelper().getIssueTypeView()));
				getHelper()
						.setIssueTypeView(
								getHelper().getIssueTypeManager()
										.getIssueTypeDetailsById(
												getHelper().getIssueTypeView()
														.getId()));
				addInfoMessageKey("MICOS.info.DataSaved");
				navigation = "issueTypeSetup";
				removeSessionAttribute("issueTypeSetupController");
			} else {
				navigation = "";
			}
		} catch (MicosException micosException) {
			LOGGER.error("createObservation", micosException);
			addErrorMessage(micosException.getMessage());
			return "";
		}

		return navigation;
	}

	/**
	 * Validates the entry given in the front end.
	 * 
	 * @return Boolean checks whether it is validated or not
	 */
	private boolean validate() {
		LOGGER.entering(getClass().getName(), "validate");

		boolean flag = true;

		if (SystemUtil
				.isNull(getHelper().getIssueTypeView().getIssueTypeName())) {
			addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.name");
			flag = false;
			return flag;
		}

		if (getHelper().getIssueTypeView().isDefaultNoOfPhases()) {

			if (getHelper().getIssueTypeView().getDfltPhases().trim().equals(Constants.EMPTY_STRING)) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.defaultphase");
				flag = false;
				return flag;
			}
			if (!SystemUtil.isInteger(getHelper().getIssueTypeView().getDfltPhases().trim())) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.numbererror.defaultphases");
				flag = false;
				return flag;
			}
			if (SystemUtil.isInteger(getHelper().getIssueTypeView().getDfltPhases().trim())
					&& (Integer.parseInt(getHelper().getIssueTypeView()
							.getDfltPhases().trim()) > IssueTypeConstants.DEFAULT_PHASES_MAX_LENGTH)) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.defaultPhases.maxValue");
				flag = false;
				return flag;
			}
			if (SystemUtil.isInteger(getHelper().getIssueTypeView().getDfltPhases().trim())
					&& (Integer.parseInt(getHelper().getIssueTypeView()
							.getDfltPhases().trim()) < IssueTypeConstants.DEFAULT_PHASES_MIN_LENGTH)) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.defaultPhases.minValue");
				flag = false;
				return flag;
			}
		}

		if (getHelper().getIssueTypeView().isEnforceEarlyActionDueDate()) {
			flag = validateEnforceEarlyActionDueDate(flag);
		}
		if (getHelper().getIssueTypeView().isLimitNextPhaseDueDate()) {
			if (getHelper().getIssueTypeView().getNextPhaseDueDateDuration().trim().equals(Constants.EMPTY_STRING)) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.nextphase");
				flag = false;
				return flag;
			}
			if (!SystemUtil.isInteger(getHelper().getIssueTypeView().getNextPhaseDueDateDuration().trim())) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.numbererror.limitnextphaseduedate");
				flag = false;
				return flag;
			}
		}

		if (getHelper().getIssueTypeView().getUpdateRequiredView().isComplete()) {
			if (getHelper().getIssueTypeView().getUpdateRequiredView().getReminderText() == null
					|| getHelper().getIssueTypeView().getUpdateRequiredView().getReminderText().length() == 0) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.updateRequired");
				flag = false;
				return flag;
			} else {
				if (!SystemUtil.isInteger(getHelper().getIssueTypeView().getUpdateRequiredView().getReminderText())) {
					addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.numbererror.update");
					flag = false;
					return flag;
				}
			}

		}

		if (getHelper().getIssueTypeView().getReminderView().isComplete()) {
			if (getHelper().getIssueTypeView().getReminderView()
					.getReminderText() == null
					|| getHelper().getIssueTypeView().getReminderView().getReminderText().length() == 0) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.reminder");
				flag = false;
				return flag;
			} else {
				if (!SystemUtil.isInteger(getHelper().getIssueTypeView().getReminderView().getReminderText())) {
					addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.numbererror.reminder");

					flag = false;
					return flag;

				}
			}

		}

		if (getHelper().getIssueTypeView().getWarningView().isComplete()) {
			if (getHelper().getIssueTypeView().getWarningView().getReminderText() == null
					|| getHelper().getIssueTypeView().getWarningView()
							.getReminderText().length() == 0) {
				addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.warning");
				flag = false;
				return flag;
			} else {
				if (!SystemUtil.isInteger(getHelper().getIssueTypeView().getWarningView().getReminderText())) {
					addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.numbererror.warning");
					flag = false;
					return flag;

				}
			}

		}

		return flag;
	}

	/**
	 * EnforceEarlyActionDueDate.
	 * 
	 * @param flag
	 *            boolean
	 * @return boolean
	 */
	private boolean validateEnforceEarlyActionDueDate(boolean flag) {
		if (getHelper().getIssueTypeView().getEarlyActionDueDatePhase().trim()
				.equals(Constants.EMPTY_STRING)) {
			addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.earlyaction");
			flag = false;
			return flag;
		}
		if (!SystemUtil.isInteger(getHelper().getIssueTypeView()
				.getEarlyActionDueDatePhase().trim())) {
			addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.numbererror.enforceEarlyActionDueDate");
			flag = false;
			return flag;
		}
		if (SystemUtil.isInteger(getHelper().getIssueTypeView()
				.getEarlyActionDueDatePhase().trim())) {
			if (!getHelper().getIssueTypeView().getDfltPhases().trim().equals(
					Constants.EMPTY_STRING)) {
				if (SystemUtil.isInteger(getHelper().getIssueTypeView()
						.getDfltPhases().trim())
						&& (Integer.parseInt(getHelper().getIssueTypeView()
								.getEarlyActionDueDatePhase().trim()) >= Integer
								.parseInt(getHelper().getIssueTypeView()
										.getDfltPhases().trim()))) {
					addErrorMessageKey("MICOS.Administration.IssueTypes.issueTypeSetup.error.chkEarlyActionValue");
					flag = false;
					return flag;
				}
			}
		}
		return flag;
	}

	/**
	 * The method responsible to navigate to employee_search page.
	 * 
	 * @return String
	 */
	public String addGroupAdmin() {

		getRequest().setAttribute(NavigationConstants.PARAM_FROM_PAGE,
				IssueTypeConstants.ISSUETYPESETUP);
		getHelper().getIssueTypeView().setCheckButton("addGroupAdmin");
		return NavigationConstants.COMMON_EMPPLOYEE_SEARCH;
	}

	/**
	 * The method responsible to navigate to employee_search page.
	 * 
	 * @return String
	 */

	public String addCodingGroup() {

		getRequest().setAttribute(NavigationConstants.PARAM_FROM_PAGE,
				IssueTypeConstants.ISSUETYPESETUP);
		getHelper().getIssueTypeView().setCheckButton("addCodingGroup");
		return NavigationConstants.COMMON_EMPPLOYEE_SEARCH;
	}

	/**
	 * Delete record from row.
	 * 
	 * @return String
	 */
	public String deleteGrpAdmin() {
		LOGGER.entering(getClass().getName(), "deleteGrpAdmin");
		try {
			int selectedEmpId = Integer
					.parseInt(getParameterValueFromRequest("selectedEmp"));
			List<EmployeeView> tempList = new ArrayList<EmployeeView>();
			tempList.addAll(getHelper().getIssueTypeView().getGroupAdmins());
			for (EmployeeView employeeView : tempList) {
				if (employeeView.getTempId() == selectedEmpId) {
					if (employeeView.getId() != null) {
						getHelper().getIssueTypeManager().deleteGroupAdmins(
								(employeeView.getId()), issueTypeId);
					}
					getHelper().getIssueTypeView().getGroupAdmins().remove(
							employeeView);
					getHelper().setDeleteGrpAdmin(true);
					break;
				}
			}
			getRequest().setAttribute(CollectionTabPane.FROM_PAGE,
					NavigationConstants.PAGE_FROM_ISSUETYPE_SETUP);

		} catch (MicosException micosException) {
			LOGGER.error("issueDetails", micosException);
			addErrorMessage(micosException.getMessage());
		}
		LOGGER.exiting(getClass().getName(), "deleteGrpAdmin");
		return "callCreateIssueGroup";
	}

	/**
	 * Delete record from row.
	 * 
	 * @return String
	 */

	public String deleteCodingGroup() {
		LOGGER.entering(getClass().getName(), "deleteCodingGroup");
		try {
			int selectedEmpId = Integer
					.parseInt(getParameterValueFromRequest("selectedEmp"));
			List<EmployeeView> selectedEmpList = new ArrayList<EmployeeView>();
			selectedEmpList.addAll(getHelper().getIssueTypeView()
					.getCodingGroup());
			for (EmployeeView employeeView : selectedEmpList) {
				if (employeeView.getTempId() == selectedEmpId) {
					if (employeeView.getId() != null) {
						getHelper().getIssueTypeManager().deleteCodingGroup(
								(employeeView.getId()), issueTypeId);
					}
					getHelper().getIssueTypeView().getCodingGroup().remove(
							employeeView);
					getHelper().setDeleteCodingGrp(true);
					break;
				}

			}
			getRequest().setAttribute(CollectionTabPane.FROM_PAGE,
					NavigationConstants.PAGE_FROM_ISSUETYPE_SETUP);

		} catch (MicosException micosException) {
			LOGGER.error("deleteCodingGroup", micosException);
			addErrorMessage(micosException.getMessage());
		}
		LOGGER.exiting(getClass().getName(), "deleteCodingGroup");
		return "callCreateIssueGroup";
	}

	/**
	 * The method invoked when the Default number of phases per phaseable issue
	 * to checkbox value is changed.
	 * 
	 * @param evnt
	 *            ValueChangeEvent.
	 */
	public void changeDefaultPhases(ValueChangeEvent evnt) {
		Boolean dfltPhases = (Boolean) evnt.getNewValue();
		if (dfltPhases) {
			getHelper().getFlagHelper().setDefaultPhasesTextboxDisabled(false);
			getHelper().getFlagHelper().setEnforceEarlyActionDisabled(false);
			getHelper().getFlagHelper().setLimitNextPhaseDisabled(false);
		} else {
			getHelper().getFlagHelper().setDefaultPhasesTextboxDisabled(true);
			getHelper().getFlagHelper().setEnforceEarlyActionDisabled(true);
			getHelper().getFlagHelper().setEnforceEarlyTextboxDisabled(true);
			getHelper().getFlagHelper().setLimitNextPhaseDisabled(true);
			getHelper().getFlagHelper().setLimitNextPhaseTextboxDisabled(true);
		}
	}

	/**
	 * The method invoked when the Enforce early action Due date checkbox value
	 * is changed.
	 * 
	 * @param evnt
	 *            ValueChangeEvent.
	 */
	public void changeEnforceEarlyAction(ValueChangeEvent evnt) {

		Boolean enforceEarly = (Boolean) evnt.getNewValue();
		if (enforceEarly) {
			getHelper().getFlagHelper().setEnforceEarlyTextboxDisabled(false);
		} else {
			getHelper().getFlagHelper().setEnforceEarlyTextboxDisabled(true);

		}
	}

	/**
	 * set the Html form.
	 * 
	 * @param init
	 *            the HtmlForm.
	 */
	public void setInit(HtmlForm init) {
		this.init = init;
	}

	/**
	 * @return Returns the mode.
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @return Returns the selectedEmp.
	 */
	public EmployeeView getSelectedEmp() {
		return selectedEmp;
	}

	/**
	 * @param selectedEmp
	 *            The selectedEmp to set.
	 */
	public void setSelectedEmp(EmployeeView selectedEmp) {
		this.selectedEmp = selectedEmp;
	}

	/**
	 * @return Returns the frompage.
	 */
	public String getFrompage() {
		return frompage;
	}

	/**
	 * @param frompage
	 *            The frompage to set.
	 */
	public void setFrompage(String frompage) {
		this.frompage = frompage;
	}

	/**
	 * @return the issueTypeId
	 */
	public Integer getIssueTypeId() {
		return issueTypeId;
	}

}
