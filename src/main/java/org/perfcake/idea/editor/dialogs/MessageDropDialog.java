package org.perfcake.idea.editor.dialogs;

import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.Nullable;
import org.perfcake.idea.model.Message;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * Created by miron on 8. 12. 2014.
 */
public class MessageDropDialog extends MyDialogWrapper {
    ButtonGroup group = new ButtonGroup();
    private JTextField uriText;
    private JTextArea contentText;
    private JPanel rootPanel;
    private JRadioButton uriRadioButton;
    private JRadioButton contentRadioButton;
    private Message mockMessage;

    public MessageDropDialog(Message mockMessage) {
        super(false);
        this.mockMessage = mockMessage;

        init();
        setTitle("Add Message");

        group.add(uriRadioButton);
        group.add(contentRadioButton);

        uriRadioButton.setEnabled(false);
        contentRadioButton.setEnabled(false);

        uriText.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                if (contentText.getText().isEmpty()) {
                    if (e.getDocument().getLength() > 0) {
                        uriRadioButton.setSelected(true);
                    } else {
                        group.clearSelection();
                    }
                }
            }
        });

        contentText.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                if (e.getDocument().getLength() > 0) {
                    contentRadioButton.setSelected(true);
                } else {
                    if (!uriText.getText().isEmpty()) {
                        uriRadioButton.setSelected(true);
                    } else {
                        group.clearSelection();
                    }
                }
            }
        });


    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (uriText.getText().isEmpty() && contentText.getText().isEmpty()) {
            return new ValidationInfo("Uri or content of a message must be specified.");
        }
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }


    @Override
    public Message getMockCopy() {
        mockMessage.getUri().setStringValue(uriText.getText());
        mockMessage.getContent().setStringValue(contentText.getText());
        return mockMessage;
    }
}