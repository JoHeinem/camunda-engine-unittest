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
package org.camunda.cdi_example;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.cdi.annotation.ProcessEngineName;
import org.camunda.bpm.engine.task.Task;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Named
public class UserTaskForm {

  @Inject
  @ProcessEngineName("foo")
  private transient ProcessEngine processEngine;

  protected RuntimeService getRuntimeService() {
    return processEngine.getRuntimeService();
  }

  protected TaskService getTaskService() {
    return processEngine.getTaskService();
  }

  @PostConstruct
  private void initUserTask() {
    final Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
    String taskId = params.get("taskId");
    if (taskId != null) {
      final Task userTask = getTaskService().createTaskQuery().taskId(taskId).singleResult();
      // should work
      Map<String, Object> processVariables = getRuntimeService().getVariables(userTask.getExecutionId(), getUserTaskFormVariables());
    }
  }

  private List<String> getUserTaskFormVariables() {
    String[] processVariableAusUsertask1 = {"MyFormVariable"};
    return Arrays.asList(processVariableAusUsertask1);
  }

}
