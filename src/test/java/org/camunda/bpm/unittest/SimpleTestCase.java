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

import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johannes Heinemann
 */
public class SimpleTestCase {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  private RuntimeService runtimeService;
  private RepositoryService repositoryService;
  private ManagementService managementService;

  @Before
  public void init() {
    repositoryService = rule.getRepositoryService();
    runtimeService = rule.getRuntimeService();
    managementService = rule.getManagementService();
  }

  @Test
  @Deployment(resources = {"testProcess.bpmn"})
  public void shouldExecuteProcess() throws InterruptedException {

    // given
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("iterations", 3);
    runtimeService.startProcessInstanceByKey("testProcess", variables);

    // when
    List<ProcessInstance> runningInstances = runtimeService.createProcessInstanceQuery().list();
    while(!runningInstances.isEmpty()) {
      executeAllJobs();
      runningInstances = runtimeService.createProcessInstanceQuery().list();
    }

    // then

  }

  private void executeAllJobs() throws InterruptedException {
    List<Job> jobList = managementService.createJobQuery().list();
    for (Job job : jobList) {
      printOutVariableFromDatabase("Database query before task but after async before");
      managementService.executeJob(job.getId());

    }
    Thread.sleep(200L);
  }

  private void printOutVariableFromDatabase(String whenItIsPrintedOut) {
    VariableInstance myVariableInstance = runtimeService.createVariableInstanceQuery().variableName("myVariable").singleResult();
    if(myVariableInstance != null) {
      Map<String, List<Integer>> myVariable = (Map<String, List<Integer>>) myVariableInstance.getValue();
      System.out.println("-------- " + whenItIsPrintedOut + " ----------");
      System.out.println("My variable size: " + myVariable.size());
      System.out.println();
    }
  }

}
