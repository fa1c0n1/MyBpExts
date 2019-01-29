package burp;

import me.falcon.ui.MainUI;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.plaf.PopupMenuUI;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class BurpExtender extends MainUI implements IBurpExtender, ITab, ITextEditor, IContextMenuFactory {
    private static final String EXTNAME = "AesCrypto v0.1 by fa1c0n1";

    private IHttpRequestResponse mHttpMsgInfo;
    private IExtensionHelpers mHelpers;
    private IBurpExtenderCallbacks mCallbacks;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        mCallbacks = callbacks;
        mHelpers = callbacks.getHelpers();
        callbacks.setExtensionName(EXTNAME);
        callbacks.registerContextMenuFactory(this);
        callbacks.addSuiteTab(BurpExtender.this);
    }

    @Override
    public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
        IHttpRequestResponse[] httpMsgs = invocation.getSelectedMessages();
        List<JMenuItem> list = new ArrayList<JMenuItem>();

        if (httpMsgs != null && httpMsgs.length == 1) {
            this.mHttpMsgInfo = httpMsgs[0];
            IRequestInfo analyzeRequest = mHelpers.analyzeRequest(httpMsgs[0].getRequest());
            JMenuItem menuItem = new JMenuItem("Send to AesCrypto");

            menuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textAreaRawCipherRd.setText(new String(BurpExtender.this.getSelectedText()));
                }
            });

            list.add(menuItem);
        }

        return list;
    }


    @Override
    public String getTabCaption() {
        return "AesCrypto";
    }

    @Override
    public Component getUiComponent() {
        return this.getContentPane();
    }


    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public void setEditable(boolean editable) {

    }

    @Override
    public void setText(byte[] text) {

    }

    @Override
    public byte[] getText() {
        return new byte[0];
    }

    @Override
    public boolean isTextModified() {
        return false;
    }

    @Override
    public byte[] getSelectedText() {
        return new byte[0];
    }

    @Override
    public int[] getSelectionBounds() {
        return new int[0];
    }

    @Override
    public void setSearchExpression(String expression) {

    }
}
