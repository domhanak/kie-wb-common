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

package org.kie.workbench.common.stunner.client.widgets.menu.dev;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.mockito.Mock;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class, AnchorListItem.class, AnchorElement.class})
public class ShapeSetsMenuItemBuilderTest {

    @Mock
    ShapeManager shapeManager;
    @Mock
    ShapeSetsMenuItemsBuilder.Callback callback;
    @Mock
    ShapeSet shapeSet;
    @Mock
    ShapeFactory shapeFactory;

    private Collection<ShapeSet<?>> shapeSetCollection;

    @Before
    public void setUp() throws Exception {
        when(shapeSet.getShapeFactory()).thenReturn(shapeFactory);
        when(shapeSet.getId()).thenReturn("Test ID");
        when(shapeSet.getDescription()).thenReturn("Testscription");

        shapeSetCollection = new ArrayList<>();
        shapeSetCollection.add(shapeSet);

        when(shapeManager.getShapeSets()).thenReturn(shapeSetCollection);
    }

    @Test
    public void testBuild() {
        ShapeSetsMenuItemsBuilder builder = new ShapeSetsMenuItemsBuilder(shapeManager);
        BaseMenuCustom menuItem = (BaseMenuCustom) builder.build("Test Name", "Test Prefix", callback);

        verify(shapeSet, times(2)).getDescription();

        Assertions.assertThat(menuItem).isNotNull();
        Assertions.assertThat(menuItem.isEnabled()).isTrue();
        Assertions.assertThat(menuItem.getResourceActions()).isEmpty();
        Assertions.assertThat(menuItem.getCaption()).isNullOrEmpty();
        Assertions.assertThat(menuItem.getContributionPoint()).isNullOrEmpty();
    }
}