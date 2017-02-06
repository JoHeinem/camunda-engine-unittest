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

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Johannes Heinemann
 */
public class SimpleTestCase {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  RuntimeService runtimeService;
  RepositoryService repositoryService;

  @Before
  public void init() {
    repositoryService = rule.getRepositoryService();
  }

  @Test
  @Deployment(resources = {"testProcess.bpmn", "testProcess.png"})
  public void shouldExecuteProcess() {

    // given
    String deploymentId = repositoryService.createDeploymentQuery().singleResult().getId();
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();

    // when
    InputStream stream = repositoryService.getProcessDiagram(processDefinition.getId());

    // then
    assertEquals("testProcess.png", processDefinition.getDiagramResourceName());
    assertNotNull(processDefinition.getDiagramResourceName());
    assertNotNull(stream);

  }

}
