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

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Johannes Heinemann
 */
public class SimpleTestCase {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private RuntimeService runtimeService;
  private RepositoryService repositoryService;
  private HistoryService historyService;
  private ManagementService managementService;

  @Before
  public void init() {
    repositoryService = rule.getRepositoryService();
    runtimeService = rule.getRuntimeService();
    historyService = rule.getHistoryService();
    managementService = rule.getManagementService();
  }

  @Test
  @Deployment(resources = {"CallActivityProcess.bpmn", "Phase1CalledIncidentProcess.bpmn", "Phase2CalledProcess.bpmn"})
  public void shouldExecuteProcess() {

    // given I start the process, which causes an incident in the called process
    ProcessInstance callActivityProcess = runtimeService.startProcessInstanceByKey("callActivityProcess");

    Job job = managementService.createJobQuery().singleResult();
    try {
      managementService.executeJob(job.getId());
    } catch (Exception e) {
      System.out.println("Error was thrown!");
    }
    Incident incident = runtimeService.createIncidentQuery().activityId("incidentTask").singleResult();
    assertNotNull(incident);

    // when I do process instance modification
    runtimeService.createProcessInstanceModification(
                incident.getProcessInstanceId())
      .cancelAllForActivity(incident.getActivityId())
      .startAfterActivity(incident.getActivityId())
      .execute();

     // then the process should be finished
    assertThat(runtimeService.createProcessInstanceQuery().count(), is(0L));


  }

}
