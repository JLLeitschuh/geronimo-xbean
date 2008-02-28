/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xbean.recipe;

import junit.framework.TestCase;
import org.apache.xbean.propertyeditor.PropertyEditors;
import static org.apache.xbean.recipe.Person.ConstructionCalled.CONSTRUCTOR;
import static org.apache.xbean.recipe.Person.ConstructionCalled.CONSTRUCTOR_4_ARG;
import static org.apache.xbean.recipe.Person.ConstructionCalled.NEW_INSTANCE;
import static org.apache.xbean.recipe.Person.ConstructionCalled.NEW_INSTANCE_4_ARG;
import static org.apache.xbean.recipe.Person.ConstructionCalled.PERSON_FACTORY;
import static org.apache.xbean.recipe.Person.ConstructionCalled.PERSON_FACTORY_4_ARG;

import java.net.URL;

public class ObjectRecipeTest extends TestCase {

    protected void setUp() throws Exception {
        PropertyEditors.class.getName();
    }

    public void testSetters() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class);
        doTest(objectRecipe, CONSTRUCTOR);
    }

    public void testConstructor() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class, new String[]{"name", "age", "homePage", "car"}, new Class[]{String.class, Integer.TYPE, URL.class, Car.class});
        doTest(objectRecipe, CONSTRUCTOR_4_ARG);
    }

    public void testConstructorWithImpliedTypes() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class, new String[]{"name", "age", "homePage", "car"}, null);
        doTest(objectRecipe, CONSTRUCTOR_4_ARG);
    }

    public void testConstructorWithNamedParameters() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class);
        objectRecipe.allow(Option.NAMED_PARAMETERS);
        doTest(objectRecipe, CONSTRUCTOR_4_ARG);
    }

    public void testStaticFactoryMethodAndSetters() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class, "newInstance");
        doTest(objectRecipe, NEW_INSTANCE);
    }

    public void testStaticFactoryMethodWithParams() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class, "newInstance", new String[]{"name", "age", "homePage", "car"}, new Class[]{String.class, Integer.TYPE, URL.class, Car.class});
        doTest(objectRecipe, NEW_INSTANCE_4_ARG);
    }

    public void testStaticFactoryMethodWithImpliedTypes() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class, "newInstance", new String[]{"name", "age", "homePage", "car"});
        doTest(objectRecipe, NEW_INSTANCE_4_ARG);
    }

    public void testStaticFactoryMethodWithNamedParameters() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(Person.class, "newInstance");
        objectRecipe.allow(Option.NAMED_PARAMETERS);
        doTest(objectRecipe, NEW_INSTANCE_4_ARG);
    }

    public void testInstanceFactorySetters() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(PersonFactory.class, "create");
        doTest(objectRecipe, PERSON_FACTORY);
    }

    public void testInstanceFactoryConstructor() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(PersonFactory.class, "create", new String[]{"name", "age", "homePage", "car"}, new Class[]{String.class, Integer.TYPE, URL.class, Car.class});
        doTest(objectRecipe, PERSON_FACTORY_4_ARG);
    }

    public void testInstanceFactoryConstructorWithImpliedTypes() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(PersonFactory.class, "create", new String[]{"name", "age", "homePage", "car"});
        doTest(objectRecipe, PERSON_FACTORY_4_ARG);
    }

    public void testInstanceFactoryConstructorWithNamedParameters() throws Exception {

        ObjectRecipe objectRecipe = new ObjectRecipe(PersonFactory.class, "create");
        objectRecipe.allow(Option.NAMED_PARAMETERS);
        doTest(objectRecipe, PERSON_FACTORY_4_ARG);
    }

    private void doTest(ObjectRecipe objectRecipe, Person.ConstructionCalled expectedConstruction) throws Exception {
        Person expected = new Person("Joe", 21, new URL("http://www.acme.org"), new Car("Mini", "Cooper", 2008));

        objectRecipe.setProperty("name", "Joe");
        objectRecipe.setProperty("age", "21");
        objectRecipe.setProperty("homePage", "http://www.acme.org");

        ObjectRecipe carRecipe = new ObjectRecipe(Car.class, new String[]{"make", "model", "year"});
        carRecipe.setProperty("make", "Mini");
        carRecipe.setProperty("model", "Cooper");
        carRecipe.setProperty("year", "2008");
        objectRecipe.setProperty("car", carRecipe);

        Person actual = (Person) objectRecipe.create(Person.class.getClassLoader());
        assertEquals("person", expected, actual);

        assertEquals(expectedConstruction, actual.getConstructionCalled());
    }

}
