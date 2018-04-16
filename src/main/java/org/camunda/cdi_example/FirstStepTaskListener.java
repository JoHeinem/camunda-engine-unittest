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


import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;

public class FirstStepTaskListener implements TaskListener{


  public void notify(DelegateTask delegateTask) {
    TestVariableExtended testVariable = new TestVariableExtended();
    testVariable.setTestField(42);
    testVariable.setAdditionalUnknownField(42);

    ObjectValue typedCustomerValue =
    Variables.objectValue(testVariable).serializationDataFormat("application/json").create();

    delegateTask.setVariable("testVariable", typedCustomerValue);
  }
}
