/*******************************************************************************
 * Copyright(c) 2003-2007 CREDIT SUISSE Financial Services. All Rights Reserved.
 * 
 * This software is the proprietary information of CREDIT SUISSE Financial
 * Services. Use is subject to license and non-disclosure terms.
 ******************************************************************************/

package com.csg.cs.micos.controller.controlsetupandissuance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.html.HtmlForm;
import javax.faces.model.SelectItem;

import com.csg.cs.core.base.logging.Logger;
import com.csg.cs.core.base.logging.LoggerHelper;
import com.csg.cs.micos.comp.ControlSetupManager;
import com.csg.cs.micos.controller.BaseController;
import com.csg.cs.micos.controller.managementoverview.StatementSetupController;
import com.csg.cs.micos.exception.MicosException;
import com.csg.cs.micos.util.IssueStatementConstants;
import com.csg.cs.micos.util.NavigationConstants;
import com.csg.cs.micos.util.SystemUtil;
import com.csg.cs.micos.util.MicosConstantValues.SchemaType;
import com.csg.cs.micos.view.ControlStepView;
import com.csg.cs.micos.view.HierarchyElementView;
import com.csg.jsf.util.JsfHelper;

/**
 * <code>SelectionViaAorController</code> is used to finding Scope of AoR.
 * 
 * @author Cognizant Technology Solutions
 * @author Last change by $Author: 212346 $
 * @version $Id: FindScopeAorController.java,v 1.2 2007/10/18 11:31:29 j013447
 *          Exp $
 */

public class SelectionViaAorController extends BaseController {

	/**
	 * A logger instance with the fully qualified class name as logger name.
	 */
	private static final Logger LOGGER = LoggerHelper
			.getLogger(SelectionViaAorController.class);

	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instance of controlSetupManager.
	 */
	private ControlSetupManager controlSpringManager;

	/**
	 * list Of Hierarchy Element
	 */

	private List<HierarchyElementView> listOfHierarchyElement;

	/**
	 * search on the basis of criteria
	 */
	private HierarchyElementView searchCriteria;

	/**
	 * Nivigation path
	 */
	private String navigateTo;

	/**
	 * To show the initial values.
	 */
	private HtmlForm init;

	/**
	 * flag for power option hierarchy search for performance.
	 */
	private boolean poHierarchyEleSearch;
	
	/**
	 * Getting the initial values.
	 * 
	 * @return HtmlForm
	 */
	public HtmlForm getInit() {
		LOGGER.entering(getClass().getName(), "getInit");
		init = new HtmlForm();
		searchCriteria = new HierarchyElementView();
		if (frompage == null) {
			frompage = getParameterValueFromRequest(NavigationConstants.PARAM_FROM_PAGE);
		}
		PowerOptionController poweroptioncontroller = (PowerOptionController) JsfHelper
		.findManagedBean("poweroptioncontroller");
		if (poweroptioncontroller != null
				&& poweroptioncontroller.isFromPowerOption() == true) {
			if (poweroptioncontroller.getSchemaHelper().getSelectedSchema().equals(
					SchemaType.EXECUTION)) {
				poHierarchyEleSearch = true;
			}
		}
		if (frompage.equalsIgnoreCase(NavigationConstants.STATEMENT_SETUP)) {
			poHierarchyEleSearch = true;
		}
		LOGGER.exiting(getClass().getName(), "getInit");
		return init;
	}

	/**
	 * Search the AoR
	 * 
	 * @return selectionviaaor the String
	 */
	public String searchHierarchyElement() {
		LOGGER.entering(getClass().getName(), "searchHierarchyElement");
		try {
			if (validate()){
				listOfHierarchyElement = controlSpringManager.searchHierarchyElementForExecutorAoR(searchCriteria);
		        if (controlSpringManager.hasMoreResults()) {
		        	addErrorMessage(getResourceBundle(
									getUsersPreferredLanguage()).getString(
										"MICOS.error.TooManyRecords"));
		        } else if (SystemUtil.isNull(listOfHierarchyElement)) {
		        	addErrorMessage(getResourceBundle(
									getUsersPreferredLanguage()).getString(
										"MICOS.AorSearch.error.NoResult"));
		        }
			}
			
		} catch (MicosException micosException) {
			LOGGER.error("searchHierarchyElement", micosException);
			addErrorMessage(micosException.getMessage());
		} 
		LOGGER.exiting(getClass().getName(), "searchHierarchyElement");
		
		return "selectionviaaor";
	}
	
	/**
	 * used to validate the search criteria.
	 * 
	 * @return a boolean value
	 */
	private boolean validate() {
		boolean valid = true;
		/*
		if (SystemUtil.isNull(searchCriteria.getHierarchyName())
				&& SystemUtil.isNull(searchCriteria.getTitle())
				&& SystemUtil.isNull(searchCriteria.getAorOwnerName())) {
			addErrorMessage(getResourceBundle(getUsersPreferredLanguage())
					.getString("MICOS.error.InvalidSearchCriteria"));
			valid = false;
		}*/
		return valid;
	}

	/**
	 * Find the selected AORs and navigates to control overview page.
	 * 
	 * @return String
	 */
	public String select() {
		LOGGER.entering(getClass().getName(), "getSelectedAORs");
		List<HierarchyElementView> selectedHeList = null;
		if(!poHierarchyEleSearch) {
			if (SystemUtil.isNotNull(listOfHierarchyElement)) {
				selectedHeList = new ArrayList<HierarchyElementView>();
				List<SelectItem> roleList = null;
				for (HierarchyElementView heView : listOfHierarchyElement) {
					if (heView.isSelected()) {
						heView.setTempId(SystemUtil.getNextSeqNo());
						if (heView.getLevel() == null) {
							heView.setLevel(0);
						}
						roleList = heView.getHierarchyRoleList();
						if (roleList != null && roleList.size() > 0) {
							Iterator itrControlType = roleList.iterator();
							while (itrControlType.hasNext()) {
								SelectItem selectItem = (SelectItem) itrControlType
										.next();
								if (selectItem.getValue().equals(heView.getSelectedRole().getId())) {
									heView.getSelectedRole().setDescription(selectItem.getLabel());
								}
							}
						}
						selectedHeList.add(heView);
					}
				}

			}
		} else {
			if(SystemUtil.isNotNull(listOfHierarchyElement)) {
				selectedHeList = new ArrayList<HierarchyElementView>();
				selectedHeList.add(listOfHierarchyElement.get(selectedIndex));
			}
		}
		
		if (SystemUtil.isNull(selectedHeList)) {
			addErrorMessageKey("MICOS.Common.hierarchyelementsearch.notselectedmessage");
			LOGGER.exiting(getClass().getName(), "getSelectedAORs",
							"inside if");
			return "";
		} else {
			ControlDetailsController controlDetailsController = (ControlDetailsController) JsfHelper
					.findManagedBean("controldetailscontroller");
			PowerOptionController poweroptioncontroller = (PowerOptionController) JsfHelper
					.findManagedBean("poweroptioncontroller");
			StatementSetupController setupController = (StatementSetupController) JsfHelper
			.findManagedBean("statementSetupController");		
			if (controlDetailsController != null
					&& controlDetailsController.isFromControlDetails() == true) {
				navigateTo = "control_steps";
				if (controlDetailsController.getSchemaHelper().getSelectedSchema().equals(
						SchemaType.SUPPORT)) {
					setStepSupportAor(controlDetailsController,selectedHeList);
				} else if (controlDetailsController.getSchemaHelper().getSelectedSchema().equals(
						SchemaType.EXECUTION)) {
					setStepPerformanceAor(controlDetailsController,selectedHeList);
				} else if (controlDetailsController.getSchemaHelper().getSelectedSchema().equals(
						SchemaType.QA)) {
					setStepQaAor(controlDetailsController,selectedHeList);

				} else if (controlDetailsController.getSchemaHelper().getSelectedSchema().equals(
						SchemaType.SIGNOFF)) {
					setStepSignoffAor(controlDetailsController,selectedHeList);
				}
				controlDetailsController.setFromControlDetails(false);
			} else {
				if (poweroptioncontroller != null
						&& poweroptioncontroller.isFromPowerOption() == true) {
					navigateTo = "powerOption";
					if (poweroptioncontroller.getSchemaHelper().getSelectedSchema().equals(
							SchemaType.SUPPORT)) {
						setPoweroptionSupportAor(poweroptioncontroller,selectedHeList);
					} else if (poweroptioncontroller.getSchemaHelper().getSelectedSchema().equals(
							SchemaType.QA)) {
						setPoweroptionQaAor(poweroptioncontroller,selectedHeList);
					} else if (poweroptioncontroller.getSchemaHelper().getSelectedSchema().equals(
							SchemaType.EXECUTION)) {
						HierarchyElementView heview = selectedHeList.get(0);
						poweroptioncontroller.setPerAORName(heview.getHierarchyName()
								+ " - "+ heview.getTitle());
						poweroptioncontroller.setHierarchyElementId(heview.getId());
						poweroptioncontroller.setTempSignOffRoleList(heview.getHierarchyRoleList());
					}
				}
				poweroptioncontroller.setFromPowerOption(false);
			}
			
			if(null != setupController){
					setupController.getStatementSetupView().setAorOwner(listOfHierarchyElement.get(selectedIndex).getTitle());
					setupController.getStatementSetupView().setAorOwnerId(listOfHierarchyElement.get(selectedIndex).getId().toString());
					setupController.setRenderIcon(false);
					getRequest().setAttribute(IssueStatementConstants.PAGE_NAVIGATE,IssueStatementConstants.PAGE_FROM_AOR_SELECTION);
					navigateTo = NavigationConstants.STATEMENT_SETUP;
				}
		}
		removeSessionAttribute("selectionviaaorcontroller");
		LOGGER.exiting(getClass().getName(), "getSelectedAORs", ":normal exit");
		return navigateTo;
	}
	

	/**
	 * Cancelling the selection of AOR.
	 * 
	 * @return String
	 */
	public String cancel() {
		LOGGER.entering(getClass().getName(), "cancel");
		removeSessionAttribute("selectionviaaorcontroller");
		ControlDetailsController controlDetailsController = (ControlDetailsController) JsfHelper
				.findManagedBean("controldetailscontroller");
		PowerOptionController poweroptioncontroller = (PowerOptionController) JsfHelper
				.findManagedBean("poweroptioncontroller");
		
		StatementSetupController statementSetupController = (StatementSetupController) JsfHelper
				.findManagedBean("statementSetupController");
		if (controlDetailsController != null
				&& controlDetailsController.isFromControlDetails() == true) {
			controlDetailsController.setFromControlDetails(false);
			LOGGER.exiting(getClass().getName(), "cancel",
					":inside if");
			return "control_steps";
		}else if(statementSetupController != null && statementSetupController.isContClicked()){
			getRequest().setAttribute(IssueStatementConstants.PAGE_NAVIGATE,IssueStatementConstants.PAGE_FROM_AOR_SELECTION);
			return NavigationConstants.STATEMENT_SETUP;
		}else {
			poweroptioncontroller.setFromPowerOption(false);
			LOGGER.exiting(getClass().getName(), "cancel",
					":inside else");
			return "powerOption";
		}
	}
	
	/**
	 * set the selected hierarchy elements in schema.
	 * @param controlDetailsController controlDetailsController
	 * @param heList selectedHierarchyElements
	 */
	private void setStepSupportAor(ControlDetailsController controlDetailsController,
			List<HierarchyElementView> heList) {
		LOGGER.entering(getClass().getName(), "setStepSupportAor");
		ControlStepView selectedContStep = controlDetailsController.getSelectedContStep();
		if (selectedContStep != null && selectedContStep.getSupportSchemaView() != null) {
			if (selectedContStep.getSupportSchemaView().getSelectionViaHierarchyView() == null) {
				selectedContStep.getSupportSchemaView().setSelectionViaHierarchyView(
								new ArrayList<HierarchyElementView>());
			}
			selectedContStep.getSupportSchemaView().getSelectionViaHierarchyView().addAll(
					heList);
		}
		
		controlDetailsController.getSchemaHelper().setSupportSchemaView(selectedContStep.getSupportSchemaView());
	}

	/**
	 * set the selected hierarchy elements in schema.
	 * @param controlDetailsController controlDetailsController
	 * @param heList selectedHierarchyElements
	 */
	private void setStepQaAor(ControlDetailsController controlDetailsController,  List<HierarchyElementView> heList) {
		LOGGER.entering(getClass().getName(), "setStepQaAor");
		ControlStepView selectedContStep = controlDetailsController.getSelectedContStep();
		if (selectedContStep != null
				&& selectedContStep.getQaSchemaView() != null) {
			if (selectedContStep.getQaSchemaView()
					.getSelectionViaHierarchyView() == null) {
				selectedContStep.getQaSchemaView()
						.setSelectionViaHierarchyView(
								new ArrayList<HierarchyElementView>());
			}
			selectedContStep.getQaSchemaView().getSelectionViaHierarchyView()
					.addAll(heList);
		}
		controlDetailsController.getSchemaHelper().setQaSchemaView(selectedContStep.getQaSchemaView());
	}

	/**
	 * set the selected hierarchy elements in schema.
	 * @param controlDetailsController controlDetailsController
	 * @param heList selectedHierarchyElements
	 */
	private void setStepPerformanceAor(ControlDetailsController controlDetailsController,  List<HierarchyElementView> heList) {
		LOGGER.entering(getClass().getName(), "setStepPerformanceAor");
		ControlStepView selectedContStep = controlDetailsController.getSelectedContStep();
		if (selectedContStep != null
				&& selectedContStep.getPerformanceSchemaView() != null) {
			if (selectedContStep.getPerformanceSchemaView()
					.getSelectionViaHierarchyView() == null) {
				selectedContStep.getPerformanceSchemaView()
						.setSelectionViaHierarchyView(
								new ArrayList<HierarchyElementView>());
			}
			selectedContStep.getPerformanceSchemaView()
					.getSelectionViaHierarchyView().addAll(heList);
		}
		controlDetailsController.getSchemaHelper().setPerformanceSchemaView(selectedContStep.getPerformanceSchemaView());
	}

	/**
	 * set the selected hierarchy elements in schema.
	 * @param controlDetailsController controlDetailsController
	 * @param heList selectedHierarchyElements
	 */
	private void setStepSignoffAor(ControlDetailsController controlDetailsController,  List<HierarchyElementView> heList) {
		LOGGER.entering(getClass().getName(), "setStepSignoffAor");
		ControlStepView selectedContStep = controlDetailsController.getSelectedContStep();
		if (selectedContStep != null
				&& selectedContStep.getSignOffSchemaView() != null) {
			if (selectedContStep.getSignOffSchemaView()
					.getSelectionViaHierarchyView() == null) {
				selectedContStep.getSignOffSchemaView()
						.setSelectionViaHierarchyView(
								new ArrayList<HierarchyElementView>());
			}
			selectedContStep.getSignOffSchemaView().getSelectionViaHierarchyView()
					.addAll(heList);
		}
		controlDetailsController.getSchemaHelper().setSignOffSchemaView(selectedContStep.getSignOffSchemaView());
	}

	/**
	 * set the selected hierarchy elements in schema.
	 * @param poweroptioncontroller PowerOptionController
	 * @param heList selectedHierarchyElements
	 */
	private void setPoweroptionSupportAor(PowerOptionController poweroptioncontroller,  List<HierarchyElementView> heList) {
		LOGGER.entering(getClass().getName(), "setPoweroptionSupportAor");
		ControlStepView selectedContStep = poweroptioncontroller.getSelectedContStep();
		if (selectedContStep != null
				&& selectedContStep.getSupportSchemaView() != null) {
			if (selectedContStep.getSupportSchemaView()
					.getSelectionViaHierarchyView() == null) {
				selectedContStep.getSupportSchemaView()
						.setSelectionViaHierarchyView(
								new ArrayList<HierarchyElementView>());
			}
			selectedContStep.getSupportSchemaView()
					.getSelectionViaHierarchyView().addAll(heList);
		}
		poweroptioncontroller.getSchemaHelper().setSupportSchemaView(selectedContStep.getSupportSchemaView());
	}

	/**
	 * set the selected hierarchy elements in schema.
	 * @param poweroptioncontroller PowerOptionController
	 * @param heList selectedHierarchyElements
	 */
	private void setPoweroptionQaAor(PowerOptionController poweroptioncontroller,  List<HierarchyElementView> heList) {
		LOGGER.entering(getClass().getName(), "setPoweroptionQaAor");
		ControlStepView selectedContStep = poweroptioncontroller.getSelectedContStep();
		if (selectedContStep != null
				&& selectedContStep.getQaSchemaView() != null) {
			if (selectedContStep.getQaSchemaView()
					.getSelectionViaHierarchyView() == null) {
				selectedContStep.getQaSchemaView()
						.setSelectionViaHierarchyView(
								new ArrayList<HierarchyElementView>());
			}
			selectedContStep.getQaSchemaView().getSelectionViaHierarchyView()
					.addAll(heList);
		}
		poweroptioncontroller.getSchemaHelper().setQaSchemaView(selectedContStep.getQaSchemaView());
	}


	/**
	 * sets the ControlSpringManager
	 * 
	 * @param controlSpringManager
	 *            ControlSetupManager
	 */
	public void setControlSpringManager(ControlSetupManager controlSpringManager) {
		this.controlSpringManager = controlSpringManager;
	}
	
	/**
	 * @return the listOfHierarchyElement
	 */
	public List<HierarchyElementView> getListOfHierarchyElement() {
		return listOfHierarchyElement;
	}

	/**
	 * Setting the initial values.
	 * 
	 * @param init
	 *            the HtmlForm
	 */
	public void setInit(HtmlForm init) {
		this.init = init;
	}

	/**
	 * @return the searchCriteria
	 */
	public HierarchyElementView getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * @return the poHierarchyEleSearch
	 */
	public boolean isPoHierarchyEleSearch() {
		return poHierarchyEleSearch;
	}


}
