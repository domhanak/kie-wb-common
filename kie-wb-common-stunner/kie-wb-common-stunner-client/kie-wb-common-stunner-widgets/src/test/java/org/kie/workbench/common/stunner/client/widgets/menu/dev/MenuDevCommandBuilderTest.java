package org.kie.workbench.common.stunner.client.widgets.menu.dev;

import java.lang.annotation.Annotation;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.html.Text;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.api.builtin.ManagedInstanceProvider;
import org.jboss.errai.ioc.client.container.IOC;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Text.class, AnchorListItem.class, AnchorElement.class, IOC.class})
public class MenuDevCommandBuilderTest {

    @Mock
    ManagedInstance<MenuDevCommand> menuDevCommandManagedInstances;

    @Before
    public void setUp() throws Exception {
        ManagedInstanceProvider provider = new ManagedInstanceProvider();
        menuDevCommandManagedInstances = (ManagedInstance<MenuDevCommand>) provider.provide(new Class[]{MenuDevCommand.class}, new Annotation[]{});
    }

    @Test
    public void testMenuDevCommandBuilderEnabled() {
        MenuDevCommandsBuilder menuDevCommandsBuilder = new MenuDevCommandsBuilder(menuDevCommandManagedInstances);
        Assertions.assertThat(menuDevCommandsBuilder.isEnabled()).isFalse();
        Assertions.assertThat(menuDevCommandsBuilder.build()).isNull();

        menuDevCommandsBuilder.enable();

        Assertions.assertThat(menuDevCommandsBuilder.isEnabled()).isTrue();
        Assertions.assertThat(menuDevCommandsBuilder.build()).isNotNull();
    }
}
