package org.perfcake.idea.editor.ui;

import com.intellij.openapi.diagnostic.Logger;
import org.perfcake.idea.editor.components.JTitledRoundedRectangle;
import org.perfcake.idea.editor.model.PropertyModel;
import org.perfcake.idea.editor.model.SenderModel;
import org.perfcake.model.Property;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by miron on 30.4.2014.
 */
public class SenderRectangle extends JTitledRoundedRectangle implements PropertyChangeListener {
    private static final Logger LOG = Logger.getInstance(SenderRectangle.class);

    private SenderModel model;

    public SenderRectangle(SenderModel model) {
        super(model.getSender().getClazz());
        this.model = model;
        model.addPropertyChangeListener(this);

        for (Property p : model.getSender().getProperty()) {
            PropertyModel propertyModel = new PropertyModel(p);
            PropertyRectangle propertyRectangle = new PropertyRectangle(propertyModel);
            panel.add(propertyRectangle);
        }
    }

    //TODO: generalize with PropertiesRectangle
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == SenderModel.CLAZZ_PROPERTY) {
            label.setText((String) evt.getNewValue());
        }
        if (evt.getPropertyName() == SenderModel.PROPERTY_PROPERTY) {
            Property oldValue = (Property) evt.getOldValue();
            Property newValue = (Property) evt.getNewValue();

            if (oldValue == null && newValue != null) {
                PropertyRectangle propertyRectangle = new PropertyRectangle(new PropertyModel(newValue));
                panel.add(propertyRectangle);
            }

            if (oldValue != null && newValue == null) {
                synchronized (getTreeLock()) {
                    Component[] components = panel.getComponents();
                    for (Component c : components) {
                        if (c instanceof PropertyRectangle) {
                            if (((PropertyRectangle) c).getModel().getProperty() == oldValue) {
                                remove(c);
                                return;
                            }
                        }
                    }
                    LOG.error("PropertyRectangle with property " + oldValue.toString() + " was not found in PropertiesRectangle");
                }
            }
        }


    }
}
