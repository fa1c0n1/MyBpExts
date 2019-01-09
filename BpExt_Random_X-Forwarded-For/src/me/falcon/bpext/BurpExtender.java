package me.falcon.bpext;

import burp.IBurpExtender;
import burp.IBurpExtenderCallbacks;
import burp.IExtensionHelpers;
import burp.IHttpListener;
import burp.IHttpRequestResponse;
import burp.IHttpRequestResponsePersisted;
import burp.IHttpService;
import burp.IMessageEditor;
import burp.IMessageEditorController;
import burp.IRequestInfo;
import burp.ITab;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BurpExtender extends AbstractTableModel
        implements IBurpExtender, ITab, IMessageEditorController, IHttpListener {

    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;
    private JSplitPane splitPane;
    private IMessageEditor requestViewer;
    private IMessageEditor responseViewer;
    private IHttpRequestResponse curDisplayedItem;
    private List<LogEntry> logList = new ArrayList<LogEntry>();
    private boolean isOpen = true; //插件是否生效
    private boolean isAuto = true; //IP是否自动随机生成
    private boolean isSpecify;     //IP是否指定值
    private String ipVal;          //指定IP

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        this.helpers = callbacks.getHelpers();
        callbacks.setExtensionName("Random X-Forward-For");

        //开始创建自定义UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //主面板
                splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                JTabbedPane topTabs = new JTabbedPane();

                //HistoryLog视图
                Table logTable = new Table(BurpExtender.this);
                JScrollPane scrollPane = new JScrollPane(logTable);

                //创建[options]显示面板
                JPanel optionsPanel = BurpExtender.this.createOptionsPanel();

                //添加主面板的上半部分中，分两个tab页面
                topTabs.add("Options", optionsPanel);
                topTabs.add("HistoryLog", scrollPane);
                splitPane.setLeftComponent(topTabs);

                //request/response 视图
                JTabbedPane tabs = new JTabbedPane();
                requestViewer = callbacks.createMessageEditor(BurpExtender.this, false);
                responseViewer = callbacks.createMessageEditor(BurpExtender.this, false);

                //添加主面板的下半部分中，分两个tab页面
                tabs.addTab("Request", requestViewer.getComponent());
                tabs.addTab("Response", responseViewer.getComponent());
                splitPane.setRightComponent(tabs);

                //自定义组件
                callbacks.customizeUiComponent(splitPane);
                callbacks.customizeUiComponent(topTabs);
                callbacks.customizeUiComponent(tabs);

                //在Burp添加自定义插件的tab页
                callbacks.addSuiteTab(BurpExtender.this);

                //注册HTTP Listener
                callbacks.registerHttpListener(BurpExtender.this);
            }
        });
    }

    /**
     * 创建options视图对象
     */
    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout());

        //是否启用X-Forwarded-For复选框
        JCheckBox isOpenCheck = new JCheckBox("是否启用X-Forwarded-For", true);

        //是否自动生成X-Forwarded-For值单选按钮
        JRadioButton isAutoRadio = new JRadioButton("自动生成X-Forwarded-For值", true);

        //是否指定X-Forwarded-For值单选按钮
        JRadioButton isSpecifyRadio = new JRadioButton("指定X-Forwarded-For值");

        //指定IP值输入框和label
        JLabel label = new JLabel("<html>&nbsp;&nbsp;&nbsp;&nbsp;IP值：</html>");
        JTextField ipText = new JTextField("", 15);
        ipText.setEditable(false);
        ipText.setBackground(Color.WHITE);

        //为复选框和单选按钮添加监听事件
        isOpenCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                isOpen = isOpenCheck.isSelected();
            }
        });

        isAutoRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                isAuto = isAutoRadio.isSelected();
            }
        });

        isSpecifyRadio.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (isSpecifyRadio.isSelected()) {
                    isSpecify = true;
                    ipText.setEditable(true);
                    ipText.setEnabled(true);
                } else {
                    isSpecify = false;
                    ipText.setEditable(false);
                    ipText.setEnabled(false);
                    ipText.setText("");
                }
            }
        });

        //为IP输入框添加监听事件，失去焦点时校验IP是否符合规范,并传递IP值
        ipText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                ipText.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void focusLost(FocusEvent e) {
                ipText.setBackground(Color.WHITE);
                if (isSpecifyRadio.isSelected()) {
                    ipVal = ipText.getText().toString();
                    if (ipVal == null || "".equals(ipVal)) {
                        JOptionPane.showMessageDialog(optionsPanel, "请指定IP值");
                        return ;
                    } else {
                        //192.168.18.17
                        String regx = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
                        Pattern pattern = Pattern.compile(regx);
                        Matcher matcher = pattern.matcher(ipVal);
                        if (!matcher.find()) {
                            JOptionPane.showMessageDialog(optionsPanel, "IP格式不正确");
                            return ;
                        }
                    }
                }
            }
        });

        //Group the radio buttons.
        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(isAutoRadio);
        btnGroup.add(isSpecifyRadio);

        optionsPanel.add(isOpenCheck);
        optionsPanel.add(isAutoRadio);
        optionsPanel.add(isSpecifyRadio);
        optionsPanel.add(label);
        optionsPanel.add(ipText);

        return optionsPanel;
    }

    @Override
    public String getTabCaption() {
        return "Logger";
    }

    @Override
    public Component getUiComponent() {
        return splitPane;
    }

    @Override
    public IHttpService getHttpService() {
        return curDisplayedItem.getHttpService();
    }

    @Override
    public byte[] getRequest() {
        return curDisplayedItem.getRequest();
    }

    @Override
    public byte[] getResponse() {
        return curDisplayedItem.getResponse();
    }

    @Override
    public void processHttpMessage(int toolFlag, boolean msgIsRequest, IHttpRequestResponse msgInfo) {
        //如果插件未启用，则跳出不执行
        if (!this.isOpen) {
            return ;
        }

        //不同的toolFlag代表了不同的Burp组件，如Intruder,Scanner,Proxy,Spider
        if (toolFlag == IBurpExtenderCallbacks.TOOL_PROXY
                || toolFlag == IBurpExtenderCallbacks.TOOL_INTRUDER
                || toolFlag == IBurpExtenderCallbacks.TOOL_SCANNER
                || toolFlag == IBurpExtenderCallbacks.TOOL_SPIDER) {
            if (msgIsRequest) { //对请求包进行处理
                IRequestInfo requestInfo = this.helpers.analyzeRequest(msgInfo); //对消息体进行解析
                String requestStr = new String(msgInfo.getRequest());
                byte[] bodyBytes = requestStr.substring(requestInfo.getBodyOffset()).getBytes();

                //获取http请求头的信息，返回headers参数的列表
                List<String> headers = requestInfo.getHeaders();
                //根据IP生成方式，获取IP
                String ipStr = this.getIpVal(isAuto);

                String xforwardStr = "X-Forwarded-For: " + ipStr;
                //添加X-Forwarded-For
                headers.add(xforwardStr);
                //重新组装请求消息
                byte[] newRequest = this.helpers.buildHttpMessage(headers, bodyBytes);
                msgInfo.setRequest(newRequest);  //设置最终的请求包
            }

            //添加消息到HistoryLog记录中，供UI显示用
            synchronized (logList) {
                int row = logList.size();
                short httpCode = this.helpers.analyzeResponse(msgInfo.getResponse()).getStatusCode();
                logList.add(new LogEntry(toolFlag,
                        this.callbacks.saveBuffersToTempFiles(msgInfo),
                        this.helpers.analyzeRequest(msgInfo).getUrl(), httpCode));
                this.fireTableRowsInserted(row, row);
            }
        }
    }

    public String getIpVal(boolean isAuto) {
        return isAuto ? RandomIP.getRandomIpStr() : this.ipVal;
    }

    @Override
    public int getRowCount() {
        return logList.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int colIdx) {
        switch (colIdx) {
            case 0:
                return "Tool";
            case 1:
                return "URL";
            case 2:
                return "STATUS";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIdx, int colIdx) {
        LogEntry logEntry = logList.get(rowIdx);

        switch (colIdx) {
            case 0:
                return callbacks.getToolName(logEntry.mTool);
            case 1:
                return logEntry.mUrl.toString();
            case 2:
                return logEntry.mHttpCode;
            default:
                return "";
        }
    }

    /**
     * extend JTable to handle cell selection
     */
    private class Table extends JTable {
        public Table(TableModel tableModel) {
            super(tableModel);
        }

        @Override
        public void changeSelection(int rowIdx, int colIdx, boolean toggle, boolean extend) {
            //show the log entry for the selected row
            LogEntry logEntry = logList.get(rowIdx);
            requestViewer.setMessage(logEntry.mResponse.getRequest(), true);
            responseViewer.setMessage(logEntry.mResponse.getResponse(), false);
            curDisplayedItem = logEntry.mResponse;
            super.changeSelection(rowIdx, colIdx, toggle, extend);
        }
    }

    /**
     * class to hold details of each log entry
     */
    static class LogEntry {
        private int mTool;
        private IHttpRequestResponsePersisted mResponse;
        private URL mUrl;
        private short mHttpCode;

        public LogEntry(int tool, IHttpRequestResponsePersisted reqResponse,
                        URL url, short httpCode) {
            this.mTool = tool;
            this.mResponse = reqResponse;
            this.mUrl = url;
            this.mHttpCode = httpCode;
        }
    }
}
