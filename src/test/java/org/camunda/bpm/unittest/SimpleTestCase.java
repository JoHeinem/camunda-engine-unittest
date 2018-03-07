/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.unittest;

import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricCaseActivityInstance;
import org.camunda.bpm.engine.history.HistoricCaseActivityInstanceQuery;
import org.camunda.bpm.engine.query.Query;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Johannes Heinemann
 */
public class SimpleTestCase {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private RuntimeService runtimeService;
  private RepositoryService repositoryService;
  private HistoryService historyService;
  private CaseService caseService;
  private TaskService taskService;

  @Before
  public void init() {
    repositoryService = rule.getRepositoryService();
    historyService = rule.getHistoryService();
    caseService = rule.getCaseService();
    taskService = rule.getTaskService();
    runtimeService = rule.getRuntimeService();
  }

  @Test
  @Deployment(resources = {"oneProcessTaskCaseWithManualActivation.cmmn", "oneTaskProcess.bpmn20.xml"})
  public void testHistoricCaseActivityCalledProcessInstanceId() {
    String taskId = "PI_ProcessTask_1";

    createCaseInstanceByKey("oneProcessTaskCase").getId();

    // as long as the process task is not activated there should be no process instance
    assertCount(0, runtimeService.createProcessInstanceQuery());

    HistoricCaseActivityInstance historicInstance = queryHistoricActivityCaseInstance(taskId);
    assertNull(historicInstance.getCalledProcessInstanceId());

    // start process task manually to create case instance
    CaseExecution processTask = queryCaseExecutionByActivityId(taskId);
    manualStart(processTask.getId());

    // there should exist a new process instance
    ProcessInstance calledProcessInstance = runtimeService.createProcessInstanceQuery().singleResult();
    assertNotNull(calledProcessInstance);

    // check that the called process instance id was correctly set
    historicInstance = queryHistoricActivityCaseInstance(taskId);
    assertEquals(calledProcessInstance.getId(), historicInstance.getCalledProcessInstanceId());

    // complete task
    Task task = taskService.createTaskQuery().singleResult();
    taskService.complete(task.getId());

    // check that the called process instance id of the second task was correctly set
    historicInstance = queryHistoricActivityCaseInstance("PI_ProcessTask_2");
    assertNotNull(historicInstance.getCalledProcessInstanceId());
  }

  private void manualStart(final String caseExecutionId) {
    caseService
      .withCaseExecution(caseExecutionId)
      .manualStart();
  }

  private CaseExecution queryCaseExecutionByActivityId(String activityId) {
    return caseService
      .createCaseExecutionQuery()
      .activityId(activityId)
      .singleResult();
  }

  private HistoricCaseActivityInstance queryHistoricActivityCaseInstance(String activityId) {
    HistoricCaseActivityInstance historicActivityInstance = historicQuery()
      .caseActivityId(activityId)
      .singleResult();
    assertNotNull("No historic activity instance found for activity id: " + activityId, historicActivityInstance);
    return historicActivityInstance;
  }

  private HistoricCaseActivityInstanceQuery historicQuery() {
    return historyService.createHistoricCaseActivityInstanceQuery();
  }


  private void assertCount(long count, Query<?, ?> historicQuery) {
    assertEquals(count, historicQuery.count());
  }

  private CaseInstance createCaseInstanceByKey(String caseDefinitionKey) {
    return caseService
      .withCaseDefinitionByKey(caseDefinitionKey)
      .create();
  }
}
