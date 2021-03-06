/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.lienzo.toolbox.items.impl;

import java.util.Iterator;
import java.util.function.BiConsumer;

import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeDragStartHandler;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.BoundingPoints;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Direction;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.nativetools.client.event.HandlerRegistrationManager;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.lienzo.toolbox.grid.Point2DGrid;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.AbstractDecoratedItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.DecoratedItem;
import org.kie.workbench.common.stunner.lienzo.toolbox.items.decorator.BoxDecorator;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ButtonGridItemImplTest {

    private final BoundingBox buttonBoundingBox = new BoundingBox(0d,
                                                                  0d,
                                                                  100d,
                                                                  200d);

    private final BoundingBox toolboxBoundingBox = new BoundingBox(0d,
                                                                   0d,
                                                                   300d,
                                                                   123d);

    @Mock
    private BiConsumer<Group, Command> showExecutor;

    @Mock
    private BiConsumer<Group, Command> hideExecutor;

    @Mock
    private ButtonItemImpl button;

    @Mock
    private ToolboxImpl toolbox;

    @Mock
    private HandlerRegistrationManager registrations;

    @Mock
    private AbstractFocusableGroupItem buttonWrap;

    @Mock
    private ItemGridImpl toolboxWrap;

    @Mock
    private BoundingPoints buttonBoundingPoints;

    @Mock
    private BoundingPoints toolboxBoundingPoints;
    @Mock
    private AbstractDecoratedItem button1;

    @Mock
    private IPrimitive button1Prim;

    private ButtonGridItemImpl tested;
    private Group buttonGroup;
    private Group toolboxGroup;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        buttonGroup = spy(new Group().setAlpha(0d));
        toolboxGroup = spy(new Group());
        when(buttonGroup.getComputedBoundingPoints()).thenReturn(buttonBoundingPoints);
        when(buttonGroup.getBoundingBox()).thenReturn(buttonBoundingBox);
        when(buttonBoundingPoints.getBoundingBox()).thenReturn(buttonBoundingBox);
        when(toolboxGroup.getComputedBoundingPoints()).thenReturn(toolboxBoundingPoints);
        when(toolboxGroup.getBoundingBox()).thenReturn(toolboxBoundingBox);
        when(toolboxBoundingPoints.getBoundingBox()).thenReturn(toolboxBoundingBox);
        when(button.getBoundingBox()).thenReturn(() -> buttonBoundingBox);
        when(button.asPrimitive()).thenReturn(buttonGroup);
        when(button.getPrimitive()).thenReturn(mock(IPrimitive.class));
        when(button.getWrapped()).thenReturn(buttonWrap);
        when(buttonWrap.registrations()).thenReturn(registrations);
        when(buttonWrap.asPrimitive()).thenReturn(buttonGroup);
        when(buttonWrap.getBoundingBox()).thenReturn(() -> buttonBoundingBox);
        when(toolbox.getBoundingBox()).thenReturn(() -> toolboxBoundingBox);
        when(toolbox.asPrimitive()).thenReturn(toolboxGroup);
        when(toolbox.getPrimitive()).thenReturn(mock(IPrimitive.class));
        when(toolbox.getAt()).thenReturn(Direction.SOUTH_EAST);
        when(toolbox.getWrapped()).thenReturn(toolboxWrap);
        when(toolboxWrap.asPrimitive()).thenReturn(toolboxGroup);
        when(toolboxWrap.getBoundingBox()).thenReturn(() -> toolboxBoundingBox);
        when(button1.getPrimitive()).thenReturn(button1Prim);
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return button;
        }).when(button).show(any(Command.class),
                             any(Command.class));
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return button;
        }).when(button).hide(any(Command.class),
                             any(Command.class));
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return toolbox;
        }).when(toolbox).show(any(Command.class),
                              any(Command.class));
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            ((Command) invocationOnMock.getArguments()[1]).execute();
            return toolbox;
        }).when(toolbox).hide(any(Command.class),
                              any(Command.class));
        tested = new ButtonGridItemImpl(button,
                                        toolbox)
                .useHideExecutor(hideExecutor)
                .useShowExecutor(showExecutor);
    }

    @Test
    public void testInit() {
        assertEquals(buttonGroup,
                     tested.asPrimitive());
        assertEquals(buttonWrap,
                     tested.getWrapped());
        assertEquals(buttonBoundingBox,
                     tested.getBoundingBox().get());
        assertFalse(tested.isVisible());
        verify(toolboxWrap,
               times(1)).useShowExecutor(eq(showExecutor));
        verify(toolboxWrap,
               times(1)).useHideExecutor(eq(hideExecutor));
    }

    @Test
    public void testAtForDropDown() {
        when(toolbox.getAt()).thenReturn(Direction.NORTH);
        ButtonGridItemImpl cascade = tested.at(Direction.NORTH);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).at(eq(Direction.NORTH));
        assertEquals(0,
                     tested.getArrow().getRotationDegrees(),
                     0);
        assertEquals(30,
                     tested.getArrow().getX(),
                     0);
        assertEquals(280,
                     tested.getArrow().getY(),
                     0);
    }

    @Test
    public void testAtForDropRight() {
        when(toolbox.getAt()).thenReturn(Direction.EAST);
        ButtonGridItemImpl cascade = tested.at(Direction.EAST);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).at(eq(Direction.EAST));
        assertEquals(-90,
                     tested.getArrow().getRotationDegrees(),
                     0);
        assertEquals(140,
                     tested.getArrow().getX(),
                     0);
        assertEquals(140,
                     tested.getArrow().getY(),
                     0);
    }

    @Test
    public void testOffset() {
        Point2D o = new Point2D(50,
                                25);
        ButtonGridItemImpl cascade = tested.offset(o);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).offset(eq(o));
    }

    @Test
    public void testGrid() {
        Point2DGrid grid = mock(Point2DGrid.class);
        ButtonGridItemImpl cascade = tested.grid(grid);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).grid(eq(grid));
    }

    @Test
    public void testDecorateGrid() {
        BoxDecorator decorator = spy(ToolboxFactory.INSTANCE.decorators().box());
        ButtonGridItemImpl cascade = tested.decorateGrid(decorator);
        assertEquals(tested,
                     cascade);
        verify(toolbox,
               times(1)).decorate(eq(decorator));
        verify(registrations,
               times(4)).register(any(HandlerRegistration.class));
    }

    @Test
    public void testShow() {
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.show(before,
                    after);
        verify(button,
               times(1)).show(eq(before),
                              eq(after));
        verify(button,
               never()).hide(any(Command.class),
                             any(Command.class));
        verify(toolbox,
               never()).hide(any(Command.class),
                             any(Command.class));
        verify(toolbox,
               never()).hide(any(Command.class),
                             any(Command.class));
        verify(before,
               times(1)).execute();
        verify(after,
               times(1)).execute();
    }

    @Test
    public void testHide() {
        final Command before = mock(Command.class);
        final Command after = mock(Command.class);
        tested.hide(before,
                    after);
        verify(toolbox,
               times(1)).hide(any(Command.class),
                              any(Command.class));
        verify(toolbox,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(button,
               times(1)).hide();
        verify(button,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(before,
               times(1)).execute();
        verify(after,
               times(1)).execute();
    }

    @Test
    public void testShowGrid() {
        tested.showGrid();
        verify(button,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(button,
               never()).hide(any(Command.class),
                             any(Command.class));
        verify(toolbox,
               times(1)).show();
        verify(toolbox,
               never()).hide(any(Command.class),
                             any(Command.class));
    }

    @Test
    public void testHideGrid() {
        tested.hideGrid();
        verify(button,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(button,
               never()).hide(any(Command.class),
                             any(Command.class));
        verify(toolbox,
               never()).show(any(Command.class),
                             any(Command.class));
        verify(toolbox,
               times(1)).hide(any(Command.class),
                              any(Command.class));
    }

    @Test
    public void testAddItem() {
        tested.add(button1);
        verify(toolbox,
               times(1)).add(eq(button1));
        verify(button1Prim,
               times(1)).addNodeMouseEnterHandler(any(NodeMouseEnterHandler.class));
        verify(button1Prim,
               times(1)).addNodeMouseExitHandler(any(NodeMouseExitHandler.class));
        verify(registrations,
               times(4)).register(any(HandlerRegistration.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIterate() {
        Iterator<DecoratedItem> iterator = mock(Iterator.class);
        when(toolbox.iterator()).thenReturn(iterator);
        assertEquals(iterator,
                     tested.iterator());
    }

    @Test
    public void testClick() {
        NodeMouseClickHandler handler = mock(NodeMouseClickHandler.class);
        tested.onClick(handler);
        verify(button,
               times(1)).onClick(eq(handler));
    }

    @Test
    public void testDrag() {
        NodeDragStartHandler startHandler = mock(NodeDragStartHandler.class);
        NodeDragMoveHandler moveHandler = mock(NodeDragMoveHandler.class);
        NodeDragEndHandler endHandler = mock(NodeDragEndHandler.class);
        tested.onDragStart(startHandler);
        tested.onDragMove(moveHandler);
        tested.onDragEnd(endHandler);
        verify(button,
               times(1)).onDragStart(eq(startHandler));
        verify(button,
               times(1)).onDragMove(eq(moveHandler));
        verify(button,
               times(1)).onDragEnd(eq(endHandler));
    }

    @Test
    public void testFocus() {
        tested.focus();
        verify(buttonWrap,
               times(1)).focus();
        verify(toolbox,
               times(1)).show();
        assertEquals(0,
                     tested.getArrow().getAlpha(),
                     0);
    }

    @Test
    public void testImmediateUnFocus() {
        tested.immediateUnFocus();
        verify(buttonWrap,
               times(1)).setUnFocusDelay(eq(0));
        verify(buttonWrap,
               times(1)).unFocus();
        verify(buttonWrap,
               times(2)).setUnFocusDelay(eq(ButtonGridItemImpl.TIMER_DELAY_MILLIS));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(button,
               times(1)).destroy();
        verify(toolbox,
               times(1)).destroy();
    }
}
