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

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceTaskBehaviorDelegate implements JavaDelegate{

  public void execute(DelegateExecution delegateExecution) throws Exception {
    Map<String, List<Integer>> myVariable = (Map<String, List<Integer>>)delegateExecution.getVariable("myVariable");
    Integer iterationRound = (Integer) delegateExecution.getVariable("iterations");
    if (myVariable == null) {
      myVariable = new HashMap<String, List<Integer>>();
      myVariable.put(iterationRound.toString(), Collections.singletonList(iterationRound));
      delegateExecution.setVariableLocal("myVariable", myVariable);
    }
    myVariable.put(iterationRound.toString(), Collections.singletonList(iterationRound));
    delegateExecution.setVariable("iterations", --iterationRound);

    System.out.println("-------- After Service Task ----------");
    System.out.println("My variable size: " + myVariable.size());
    System.out.println();
  }
}
