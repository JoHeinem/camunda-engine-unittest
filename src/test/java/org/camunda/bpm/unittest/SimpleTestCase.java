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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    this.repositoryService = this.rule.getRepositoryService();
    this.historyService = this.rule.getHistoryService();
    this.caseService = this.rule.getCaseService();
    this.taskService = this.rule.getTaskService();
    this.runtimeService = this.rule.getRuntimeService();
  }

  @Test
  @Deployment(resources = { "hostBasedDelegation.cmmn", "compensationAndBenefits.bpmn", "localDelegationContract.bpmn" })
  public void testHistoricCaseActivityCalledProcessInstanceId() {
    String taskId = "PI_ProcessTask_1";

    this.createCaseInstanceByKey("oneProcessTaskCase").getId();

    // as long as the process task is not activated there should be no process instance
    this.assertCount(0, this.runtimeService.createProcessInstanceQuery());

    HistoricCaseActivityInstance historicInstance = this.queryHistoricActivityCaseInstance(taskId);
    assertNull(historicInstance.getCalledProcessInstanceId());

    // start process task manually to create case instance
    CaseExecution processTask = this.queryCaseExecutionByActivityId(taskId);
    this.manualStart(processTask.getId());

    // there should exist a new process instance
    ProcessInstance calledProcessInstance = this.runtimeService.createProcessInstanceQuery().singleResult();
    assertNotNull(calledProcessInstance);

    // check that the called process instance id was correctly set
    historicInstance = this.queryHistoricActivityCaseInstance(taskId);
    assertEquals(calledProcessInstance.getId(), historicInstance.getCalledProcessInstanceId());

    // complete task
    Task task = this.taskService.createTaskQuery().singleResult();
    this.taskService.complete(task.getId());

    // there should exist a new process instance
    calledProcessInstance = this.runtimeService.createProcessInstanceQuery().singleResult();
    assertNotNull(calledProcessInstance);

    // check that the called process instance id of the second task was correctly set
    historicInstance = this.queryHistoricActivityCaseInstance("PI_ProcessTask_2");
    assertNotNull(historicInstance.getCalledProcessInstanceId());
  }

  private void manualStart(final String caseExecutionId) {
    this.caseService.withCaseExecution(caseExecutionId).manualStart();
  }

  private CaseExecution queryCaseExecutionByActivityId(final String activityId) {
    return this.caseService.createCaseExecutionQuery().activityId(activityId).singleResult();
  }

  private HistoricCaseActivityInstance queryHistoricActivityCaseInstance(final String activityId) {
    HistoricCaseActivityInstance historicActivityInstance = this.historicQuery().caseActivityId(activityId)
        .singleResult();
    assertNotNull("No historic activity instance found for activity id: " + activityId, historicActivityInstance);
    return historicActivityInstance;
  }

  private HistoricCaseActivityInstanceQuery historicQuery() {
    return this.historyService.createHistoricCaseActivityInstanceQuery();
  }

  private void assertCount(final long count, final Query<?, ?> historicQuery) {
    assertEquals(count, historicQuery.count());
  }

  private CaseInstance createCaseInstanceByKey(final String caseDefinitionKey) {
    return this.caseService.withCaseDefinitionByKey(caseDefinitionKey).create();
  }
}
