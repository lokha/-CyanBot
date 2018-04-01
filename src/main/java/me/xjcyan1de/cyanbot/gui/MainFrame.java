package me.xjcyan1de.cyanbot.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import me.xjcyan1de.cyanbot.Bot;
import me.xjcyan1de.cyanbot.BotManager;
import me.xjcyan1de.cyanbot.config.Config;
import me.xjcyan1de.cyanbot.logger.BotLogger;
import me.xjcyan1de.cyanbot.utils.Schedule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    private JPanel contentPane;
    private JTextArea joinCommands;
    private JTextField ip;
    private JButton join;
    private JTextField name;
    private JTextArea chat;
    private JTextArea logs;
    private JTextField messageText;
    private JButton sendMessage;
    private JScrollPane chatScroll;
    private JScrollPane logsScroll;
    private JButton leave;
    private JTextField status;
    private JComboBox historyIp;
    private JCheckBox autoJoin;
    private JTabbedPane tabbedPane1;
    private JList commandList;
    private JButton getKey;
    private JPanel commandPanel;
    private JList botList;
    private JButton slotButton1;
    private JPanel slotPanel;
    private JButton slotButton2;
    private JButton slotButton3;
    private JButton slotButton4;
    private JButton slotButton5;
    private JButton slotButton6;
    private JButton slotButton7;
    private JButton slotButton8;
    private JButton slotButton9;
    private Config config;
    private BotManager manager;
    private Logger logger;

    public MainFrame(Config config, BotManager manager, Logger logger) {
        this.config = config;
        this.manager = manager;
        this.logger = logger;

        this.setTitle("CyanBot");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(join);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final ConfigSavedJFrameData configSaved = new ConfigSavedJFrameData(this, config);
        configSaved.loadConfig();
        configSaved.register();

        logger.addHandler(new FormLoggerHandler(logs));

        this.registerListeners();
        this.scheduleUpdateStatus(this, manager);

        if (autoJoin.isSelected()) {
            this.onJoin();
        }

        commandPanel.removeAll();
    }

    @SuppressWarnings("unchecked")
    private void scheduleUpdateStatus(MainFrame mainFrame, BotManager manager) {
        Schedule.timer(() -> {
            final DefaultListModel model = (DefaultListModel) botList.getModel();

            Set<String> namesInList = new LinkedHashSet<>();
            for (int i = 0; i < model.getSize(); i++) {
                final String name = String.valueOf(model.getElementAt(i));
                if (!manager.isConnected(name)) {
                    model.removeElement(name);
                } else {
                    namesInList.add(name);
                }
            }
            manager.getBots().forEach(player -> {
                final String name = player.getUsername();
                if (!namesInList.contains(name)) {
                    model.addElement(name);
                    if (model.size() == 1) {
                        botList.setSelectedIndex(0);
                    }
                }
            });

            boolean connected = manager.isConnected(name.getText());

            final JTextField status = mainFrame.getStatus();
            if (!connected) {
                status.setText("Не подключен");
                status.setBackground(Color.decode("#ff5050"));
            } else {
                status.setText("Подключен");
                status.setBackground(Color.decode("#33cc33"));
            }

            sendMessage.setEnabled(connected);
            leave.setEnabled(connected);
            join.setEnabled(!connected);
            getKey.setEnabled(connected);
        }, 0, 500);
    }

    public JCheckBox getAutoJoin() {
        return autoJoin;
    }

    public JTextField getStatus() {
        return status;
    }

    public JTextField getUsername() {
        return name;
    }


    public JButton getJoin() {
        return join;
    }

    @SuppressWarnings("unchecked")
    private void registerListeners() {
        join.addActionListener(e -> this.onJoin());

        getKey.addActionListener(e -> {
            final Bot bot = manager.getBot(name.getText());
            if (bot != null) {
                final String accessKey = bot.generateAccessKey();
                if (accessKey != null) {
                    Hover.hoverText("Введите этот код в чат", bot.getUsername() + ", " + accessKey);
                }
            }
        });


        leave.addActionListener(e -> {
            final Bot bot = manager.getBot(name.getText());
            if (bot != null) {
                manager.disconnectBot(bot);
                final DefaultListModel model = (DefaultListModel) botList.getModel();
                model.removeElement(bot.getUsername());
                botList.setModel(model);
            }
        });

        sendMessage.addActionListener(e -> {
            final Bot bot = manager.getBot(name.getText());
            if (bot != null) {
                bot.sendMessage(messageText.getText());
            }
        });

        autoJoin.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                final Bot bot = manager.getBot(name.getText());
                if (bot == null) {
                    this.onJoin();
                }
            }
        });

        final DefaultListModel listModel = new DefaultListModel();
        CommandListHandler.init(listModel);
        commandList.setModel(listModel);

        commandList.addListSelectionListener(e -> {
            final int index = commandList.getSelectedIndex();
            if (index != -1) {
                CommandListHandler.createPanel(commandPanel, index);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Bot[] getSelectedBots() {
        return (Bot[]) botList.getSelectedValuesList().stream()
                .map(name1 -> manager.getBot(String.valueOf(name1)))
                .filter(Objects::nonNull).toArray(Bot[]::new);
    }

    @SuppressWarnings("unchecked")
    private void onJoin() {
        String ipText = ip.getText();
        final String[] ipPort = ipText.split(":");
        final Bot bot = new Bot(manager, this,
                new BotLogger(name.getText(), logger), name.getText(), ipPort[0], ipPort.length > 1 ? Integer.parseInt(ipPort[1]) : 25565);
        manager.connectBot(bot, ipText);

        Schedule.later(() -> {
            for (String cmd : joinCommands.getText().split("\n")) {
                bot.sendMessage(cmd);
            }
        }, 1000);
    }

    public JTextField getIp() {
        return ip;
    }

    public JComboBox getHistoryIp() {
        return historyIp;
    }

    public JTextArea getJoinCommands() {
        return joinCommands;
    }

    public JTextArea getChat() {
        return chat;
    }

    public Logger getLogger() {
        return logger;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        tabbedPane1 = new JTabbedPane();
        contentPane.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Основное", panel1);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Данные"));
        name = new JTextField();
        name.setText("CyanBot");
        panel2.add(name, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        joinCommands = new JTextArea();
        joinCommands.setText("");
        panel2.add(joinCommands, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        leave = new JButton();
        leave.setEnabled(false);
        leave.setText("Отключиться");
        panel2.add(leave, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        status = new JTextField();
        status.setEditable(false);
        status.setText("Статус");
        panel2.add(status, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        autoJoin = new JCheckBox();
        autoJoin.setText("Заходить автоматически");
        panel2.add(autoJoin, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        join = new JButton();
        join.setText("Подключиться");
        panel2.add(join, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("IP"));
        ip = new JTextField();
        ip.setText("mc.JustVillage.ru");
        panel3.add(ip, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        historyIp = new JComboBox();
        panel3.add(historyIp, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder("Действия"));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder("Чат"));
        sendMessage = new JButton();
        sendMessage.setEnabled(false);
        sendMessage.setText("Сообщение");
        panel5.add(sendMessage, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        messageText = new JTextField();
        messageText.setText("Привет, друзья");
        panel5.add(messageText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder("Авторизация"));
        getKey = new JButton();
        getKey.setText("Получить ключ");
        panel6.add(getKey, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Команды", panel7);
        commandList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        commandList.setModel(defaultListModel1);
        commandList.setSelectionMode(0);
        panel7.add(commandList, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        commandPanel = new JPanel();
        commandPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(commandPanel, new GridConstraints(0, 1, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        slotPanel = new JPanel();
        slotPanel.setLayout(new GridLayoutManager(9, 1, new Insets(0, 0, 0, 0), -1, -1));
        commandPanel.add(slotPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        slotPanel.setBorder(BorderFactory.createTitledBorder("Слот"));
        slotButton1 = new JButton();
        slotButton1.setText("Слот 1");
        slotPanel.add(slotButton1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton2 = new JButton();
        slotButton2.setText("Слот 2");
        slotPanel.add(slotButton2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton3 = new JButton();
        slotButton3.setText("Слот 3");
        slotPanel.add(slotButton3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton4 = new JButton();
        slotButton4.setText("Слот 4");
        slotPanel.add(slotButton4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton5 = new JButton();
        slotButton5.setText("Слот 5");
        slotPanel.add(slotButton5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton6 = new JButton();
        slotButton6.setText("Слот 6");
        slotPanel.add(slotButton6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton7 = new JButton();
        slotButton7.setText("Слот 7");
        slotPanel.add(slotButton7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton8 = new JButton();
        slotButton8.setText("Слот 8");
        slotPanel.add(slotButton8, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slotButton9 = new JButton();
        slotButton9.setText("Слот 9");
        slotPanel.add(slotButton9, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        botList = new JList();
        final DefaultListModel defaultListModel2 = new DefaultListModel();
        botList.setModel(defaultListModel2);
        panel7.add(botList, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel8.setBorder(BorderFactory.createTitledBorder("Логи"));
        chatScroll = new JScrollPane();
        panel8.add(chatScroll, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 150), null, 0, false));
        chat = new JTextArea();
        chat.setText("чат");
        chatScroll.setViewportView(chat);
        logsScroll = new JScrollPane();
        panel8.add(logsScroll, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 150), null, 0, false));
        logs = new JTextArea();
        logs.setText("логи\n");
        logsScroll.setViewportView(logs);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
