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

public class TestVariableExtended {

  private int testField = 0;
  private int additionalUnknownField;

  public int getTestField() {
    return testField;
  }

  public void setTestField(int testField) {
    this.testField = testField;
  }

  public int getAdditionalUnknownField() {
    return additionalUnknownField;
  }

  public void setAdditionalUnknownField(int additionalUnknownField) {
    this.additionalUnknownField = additionalUnknownField;
  }
}
