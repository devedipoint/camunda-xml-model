/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.model.xml.testmodel.instance;

import org.camunda.bpm.model.xml.ModelInstance;
import org.camunda.bpm.model.xml.impl.parser.AbstractModelParser;
import org.camunda.bpm.model.xml.testmodel.Gender;
import org.camunda.bpm.model.xml.testmodel.TestModelParser;
import org.camunda.bpm.model.xml.testmodel.TestModelTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.model.xml.testmodel.TestModelConstants.MODEL_NAMESPACE;
import static org.junit.runners.Parameterized.Parameters;

/**
 * @author Sebastian Menski
 */
public class BirdTest extends TestModelTest {

  private Bird tweety;
  private Bird hedwig;
  private Bird timmy;
  private Egg egg1;
  private Egg egg2;

  public BirdTest(String testName, ModelInstance testModelInstance, AbstractModelParser modelParser) {
    super(testName, testModelInstance, modelParser);
  }

  @Parameters(name="Model {0}")
  public static Collection<Object[]> models() {
    Object[][] models = {createModel(), parseModel(BirdTest.class)};
    return Arrays.asList(models);
  }

  public static Object[] createModel() {
    TestModelParser modelParser = new TestModelParser();
    ModelInstance modelInstance = modelParser.getEmptyModel();

    Animals animals = modelInstance.newInstance(Animals.class);
    modelInstance.setDocumentElement(animals);

    // add a tns namespace prefix for QName testing
    animals.getDomElement().registerNamespace("tns", MODEL_NAMESPACE);

    Bird tweety = createBird(modelInstance, "tweety", Gender.Female);
    Bird hedwig = createBird(modelInstance, "hedwig", Gender.Female);
    createBird(modelInstance, "timmy", Gender.Female);
    Egg egg1 = createEgg(modelInstance, "egg1");
    Egg egg2 = createEgg(modelInstance, "egg2");

    tweety.setSpouse(hedwig);
    tweety.getEggs().add(egg1);
    tweety.getEggs().add(egg2);

    return new Object[]{"created", modelInstance, modelParser};
  }

  @Before
  public void copyModelInstance() {
    modelInstance = cloneModelInstance();
    tweety = modelInstance.getModelElementById("tweety");
    hedwig = modelInstance.getModelElementById("hedwig");
    timmy = modelInstance.getModelElementById("timmy");
    egg1 = modelInstance.getModelElementById("egg1");
    egg2 = modelInstance.getModelElementById("egg2");
  }

  @Test
  public void testAddEggsByHelper() {
    assertThat(tweety.getEggs())
      .isNotEmpty()
      .hasSize(2)
      .containsOnly(egg1, egg2);

    Egg egg3 = createEgg(modelInstance, "egg3");
    tweety.getEggs().add(egg3);
    Egg egg4 = createEgg(modelInstance, "egg4");
    tweety.getEggs().add(egg4);

    assertThat(tweety.getEggs())
      .hasSize(4)
      .containsOnly(egg1, egg2, egg3, egg4);
  }

  @Test
  public void testUpdateEggsByIdByHelper() {
    egg1.setId("new-" + egg1.getId());
    egg2.setId("new-" + egg2.getId());
    assertThat(tweety.getEggs())
      .hasSize(2)
      .containsOnly(egg1, egg2);
  }

  @Test
  public void testUpdateEggsByIdByAttributeName() {
    egg1.setAttributeValue("id", "new-" + egg1.getId(), true);
    egg2.setAttributeValue("id", "new-" + egg2.getId(), true);
    assertThat(tweety.getEggs())
      .hasSize(2)
      .containsOnly(egg1, egg2);
  }

  @Test
  public void testUpdateEggsByReplaceElements() {
    Egg egg3 = createEgg(modelInstance, "egg3");
    Egg egg4 = createEgg(modelInstance, "egg4");
    egg1.replaceWithElement(egg3);
    egg2.replaceWithElement(egg4);
    assertThat(tweety.getEggs())
      .hasSize(2)
      .containsOnly(egg3, egg4);
  }

  @Test
  public void testUpdateEggsByRemoveElement() {
    tweety.getEggs().remove(egg1);
    assertThat(tweety.getEggs())
      .hasSize(1)
      .containsOnly(egg2);
  }

  @Test
  public void testClearEggs() {
    tweety.getEggs().clear();
    assertThat(tweety.getEggs())
      .isEmpty();
  }

  @Test
  public void testSetSpouseRefByHelper() {
    tweety.setSpouse(timmy);
    assertThat(tweety.getSpouse()).isEqualTo(timmy);
  }

  @Test
  public void testUpdateSpouseByIdHelper() {
    hedwig.setId("new-" + hedwig.getId());
    assertThat(tweety.getSpouse()).isEqualTo(hedwig);
  }

  @Test
  public void testUpdateSpouseByIdByAttributeName() {
    hedwig.setAttributeValue("id", "new-" + hedwig.getId(), true);
    assertThat(tweety.getSpouse()).isEqualTo(hedwig);
  }

  @Test
  public void testUpdateSpouseByReplaceElement() {
    hedwig.replaceWithElement(timmy);
    assertThat(tweety.getSpouse()).isEqualTo(timmy);
  }

  @Test
  public void testUpdateSpouseByRemoveElement() {
    Animals animals = (Animals) modelInstance.getDocumentElement();
    animals.getAnimals().remove(hedwig);
    assertThat(tweety.getSpouse()).isNull();
  }

  @Test
  public void testClearSpouse() {
    tweety.removeSpouse();
    assertThat(tweety.getSpouse()).isNull();
  }

  @Test
  public void testSetSpouseRefsByHelper() {
    SpouseRef spouseRef = modelInstance.newInstance(SpouseRef.class);
    spouseRef.setTextContent(timmy.getId());
    tweety.getSpouseRef().replaceWithElement(spouseRef);
    assertThat(tweety.getSpouse()).isEqualTo(timmy);
  }

  @Test
  public void testSpouseRefsByTextContent() {
    SpouseRef spouseRef = tweety.getSpouseRef();
    assertThat(spouseRef.getTextContent()).isEqualTo(hedwig.getId());
  }

  @Test
  public void testUpdateSpouseRefsByTextContent() {
    SpouseRef spouseRef = tweety.getSpouseRef();
    spouseRef.setTextContent(timmy.getId());
    assertThat(tweety.getSpouse()).isEqualTo(timmy);
  }

  @Test
  public void testUpdateSpouseRefsByTextContentWithNamespace() {
    SpouseRef spouseRef = tweety.getSpouseRef();
    spouseRef.setTextContent("tns:" + timmy.getId());
    assertThat(tweety.getSpouse()).isEqualTo(timmy);
  }

}
