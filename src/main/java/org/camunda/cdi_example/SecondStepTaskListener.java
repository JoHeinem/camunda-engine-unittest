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

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.cdi.annotation.ProcessEngineName;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import javax.inject.Inject;
import javax.inject.Named;

import static org.camunda.spin.Spin.JSON;

@Named
public class SecondStepTaskListener implements TaskListener {

  @Inject
  @ProcessEngineName("foo")
  protected RuntimeService runtimeService;

  public void notify(DelegateTask delegateTask) {
    ObjectValue testVariable = delegateTask.getVariableLocalTyped("testVariable");
    ModellingEntity value = testVariable.getValue(ModellingEntity.class);
    System.out.println("Serializing value....");
    delegateTask.setVariable("bar", value);
    String testVariableJson = testVariable.getValueSerialized();

    System.out.println(testVariableJson);


    // should work
    ModellingEntity modellingEntityExtended = JSON(testVariableJson).mapTo(ModellingEntity.class);
  }
}
