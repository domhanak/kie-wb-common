/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts.asCData;

public class CustomInput<T> {

    public static final CustomInputDefinition<String> taskName = new StringInput("TaskName", "Task");
    public static final CustomInputDefinition<String> priority = new StringInput("Priority", "");
    public static final CustomInputDefinition<String> subject = new StringInput("Comment", "");
    public static final CustomInputDefinition<String> description = new StringInput("Description", "");
    public static final CustomInputDefinition<String> createdBy = new StringInput("CreatedBy", "");
    public static final CustomInputDefinition<String> groupId = new StringInput("GroupId", "");
    public static final CustomInputDefinition<Boolean> skippable = new BooleanInput("Skippable", false);

    private final CustomInputDefinition<T> inputDefinition;
    private final Task element;
    private final ItemDefinition typeDef;

    public CustomInput(CustomInputDefinition<T> inputDefinition, Task element) {
        this.inputDefinition = inputDefinition;
        this.element = element;
        this.typeDef = typedefInput(inputDefinition.name(), inputDefinition.type());
    }

    public ItemDefinition typeDef() {
        return typeDef;
    }

    public T get() {
        return inputDefinition.getValue(element);
    }

    public void set(T value) {
        setStringValue(String.valueOf(value));
    }

    private void setStringValue(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        DataInputAssociation input = input(value);
        getIoSpecification(element).getDataInputs().add((DataInput) input.getTargetRef());
        element.getDataInputAssociations().add(input);
    }

    private InputOutputSpecification getIoSpecification(Task element) {
        InputOutputSpecification ioSpecification = element.getIoSpecification();
        if (ioSpecification == null) {
            ioSpecification = bpmn2.createInputOutputSpecification();
            element.setIoSpecification(ioSpecification);
        }
        return ioSpecification;
    }

    private DataInputAssociation input(Object value) {
        // first we declare the type of this assignment

//        // then we declare the input that will provide
//        // the value that we assign to `source`
//        // e.g. myInput
        DataInput target = readInputFrom(inputDefinition.name(), typeDef);

        Assignment assignment = assignment(value.toString(), target.getId());

        // then we create the actual association between the two
        // e.g. foo := myInput (or, to put it differently, myInput -> foo)
        DataInputAssociation association =
                associationOf(assignment, target);

        return association;
    }

    private DataInput readInputFrom(String targetName, ItemDefinition typeDef) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setName(targetName);
        // the id is an encoding of the node id + the name of the input
        dataInput.setId(Ids.dataInput(element.getId(), targetName));
        dataInput.setItemSubjectRef(typeDef);
        CustomAttribute.dtype.of(dataInput).set(typeDef.getStructureRef());
        return dataInput;
    }

    private Assignment assignment(String from, String to) {
        Assignment assignment = bpmn2.createAssignment();
        FormalExpression fromExpr = bpmn2.createFormalExpression();
        fromExpr.setBody(asCData(from));
        assignment.setFrom(fromExpr);
        FormalExpression toExpr = bpmn2.createFormalExpression();
        toExpr.setBody(to);
        assignment.setTo(toExpr);
        return assignment;
    }

    private DataInputAssociation associationOf(Assignment assignment, DataInput dataInput) {
        DataInputAssociation dataInputAssociation =
                bpmn2.createDataInputAssociation();

        dataInputAssociation.getAssignment()
                .add(assignment);

        dataInputAssociation
                .setTargetRef(dataInput);
        return dataInputAssociation;
    }

    private ItemDefinition typedefInput(String name, String type) {
        ItemDefinition typeDef = bpmn2.createItemDefinition();
        typeDef.setId(Ids.dataInputItem(element.getId(), name));
        typeDef.setStructureRef(type);
        return typeDef;
    }
}
