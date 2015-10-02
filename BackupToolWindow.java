import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.DropMode;
import javax.swing.TransferHandler;
import javax.swing.ListSelectionModel;

public class BackupToolWindow {
	/**
	 * Wrapper class for a string for case-insensitive purposes.
	 */
	private static class Subreddit {
		public final String name;

		public Subreddit(String name) {
			if (name == null) {
				throw new IllegalArgumentException("name must not be null");
			}

			this.name = name;
		}

		public boolean equals(Object other) {
			return other instanceof Subreddit
					&& this.name.equalsIgnoreCase(((Subreddit) other).name);
		}

		@Override
		public String toString() {
			return "/r/" + name;
		}
	}

	private JFrame frame;
	private JTextField textFieldCurrentSubreddit;
	private JTextField textFieldAddSubreddit;
	private JTextField textFieldSaveLocation;

	private DefaultListModel<Subreddit> listModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BackupToolWindow window = new BackupToolWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BackupToolWindow() {
		initialize();
	}
	
	/**
	 * Base directory in which to save data.
	 */
	private File baseDirectory;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel queuePanel = new JPanel();
		splitPane.setLeftComponent(queuePanel);
		GridBagLayout gbl_queuePanel = new GridBagLayout();
		gbl_queuePanel.columnWidths = new int[] { 0, 0 };
		gbl_queuePanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gbl_queuePanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_queuePanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		queuePanel.setLayout(gbl_queuePanel);

		JLabel labelCurrentSubreddit = new JLabel("Current subreddit");
		GridBagConstraints gbc_labelCurrentSubreddit = new GridBagConstraints();
		gbc_labelCurrentSubreddit.insets = new Insets(0, 0, 5, 0);
		gbc_labelCurrentSubreddit.gridx = 0;
		gbc_labelCurrentSubreddit.gridy = 0;
		queuePanel.add(labelCurrentSubreddit, gbc_labelCurrentSubreddit);

		textFieldCurrentSubreddit = new JTextField();
		textFieldCurrentSubreddit.setEditable(false);
		textFieldCurrentSubreddit.setColumns(10);
		GridBagConstraints gbc_textFieldCurrentSubreddit = new GridBagConstraints();
		gbc_textFieldCurrentSubreddit.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldCurrentSubreddit.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldCurrentSubreddit.gridx = 0;
		gbc_textFieldCurrentSubreddit.gridy = 1;
		queuePanel
				.add(textFieldCurrentSubreddit, gbc_textFieldCurrentSubreddit);

		JSeparator separator1 = new JSeparator();
		GridBagConstraints gbc_separator1 = new GridBagConstraints();
		gbc_separator1.insets = new Insets(0, 0, 5, 0);
		gbc_separator1.gridx = 0;
		gbc_separator1.gridy = 2;
		queuePanel.add(separator1, gbc_separator1);

		JLabel labelQueue = new JLabel("Queue");
		GridBagConstraints gbc_labelQueue = new GridBagConstraints();
		gbc_labelQueue.insets = new Insets(0, 0, 5, 0);
		gbc_labelQueue.gridx = 0;
		gbc_labelQueue.gridy = 3;
		queuePanel.add(labelQueue, gbc_labelQueue);

		listModel = new DefaultListModel<Subreddit>();
		JList<Subreddit> listQueue = new JList<Subreddit>(listModel);
		listQueue.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listQueue.setDropMode(DropMode.INSERT);
		listQueue.setDragEnabled(true);
		GridBagConstraints gbc_listQueue = new GridBagConstraints();
		gbc_listQueue.fill = GridBagConstraints.BOTH;
		gbc_listQueue.insets = new Insets(0, 0, 5, 0);
		gbc_listQueue.gridx = 0;
		gbc_listQueue.gridy = 4;
		listQueue.setTransferHandler(new DragAndDropTransferHandler());
		queuePanel.add(listQueue, gbc_listQueue);

		JSeparator separator2 = new JSeparator();
		GridBagConstraints gbc_separator2 = new GridBagConstraints();
		gbc_separator2.insets = new Insets(0, 0, 5, 0);
		gbc_separator2.gridx = 0;
		gbc_separator2.gridy = 5;
		queuePanel.add(separator2, gbc_separator2);

		JLabel labelAddSubreddit = new JLabel("Add a subreddit");
		GridBagConstraints gbc_labelAddSubreddit = new GridBagConstraints();
		gbc_labelAddSubreddit.insets = new Insets(0, 0, 5, 0);
		gbc_labelAddSubreddit.gridx = 0;
		gbc_labelAddSubreddit.gridy = 6;
		queuePanel.add(labelAddSubreddit, gbc_labelAddSubreddit);

		textFieldAddSubreddit = new JTextField();
		textFieldAddSubreddit.setEditable(false);
		textFieldAddSubreddit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String subName = textFieldAddSubreddit.getText();

				if (subName.startsWith("/r/")) {
					subName = subName.substring(3);
				} else if (subName.startsWith("r/")) {
					subName = subName.substring(2);
				}
				Subreddit sub = new Subreddit(subName);

				if (!listModel.contains(sub)) {
					listModel.addElement(sub);
				}

				textFieldAddSubreddit.setText("");
			}
		});
		textFieldAddSubreddit.setColumns(10);
		GridBagConstraints gbc_textFieldAddSubreddit = new GridBagConstraints();
		gbc_textFieldAddSubreddit.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldAddSubreddit.gridx = 0;
		gbc_textFieldAddSubreddit.gridy = 7;
		queuePanel.add(textFieldAddSubreddit, gbc_textFieldAddSubreddit);

		JPanel panelMain = new JPanel();
		splitPane.setRightComponent(panelMain);
		GridBagLayout gbl_panelMain = new GridBagLayout();
		gbl_panelMain.columnWidths = new int[] { 252, 0, 0 };
		gbl_panelMain.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelMain.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gbl_panelMain.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panelMain.setLayout(gbl_panelMain);

		JLabel labelSaveLocation = new JLabel("Save location");
		GridBagConstraints gbc_labelSaveLocation = new GridBagConstraints();
		gbc_labelSaveLocation.gridwidth = 2;
		gbc_labelSaveLocation.insets = new Insets(0, 0, 5, 0);
		gbc_labelSaveLocation.gridx = 0;
		gbc_labelSaveLocation.gridy = 0;
		panelMain.add(labelSaveLocation, gbc_labelSaveLocation);

		textFieldSaveLocation = new JTextField();
		textFieldSaveLocation.setEditable(false);
		GridBagConstraints gbc_textFieldSaveLocation = new GridBagConstraints();
		gbc_textFieldSaveLocation.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldSaveLocation.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSaveLocation.gridx = 0;
		gbc_textFieldSaveLocation.gridy = 1;
		panelMain.add(textFieldSaveLocation, gbc_textFieldSaveLocation);
		textFieldSaveLocation.setColumns(10);

		JButton buttonBrowse = new JButton("Browse");
		buttonBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser dialog = new JFileChooser();
				dialog.setMultiSelectionEnabled(false);
				dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int result = dialog.showSaveDialog(BackupToolWindow.this.frame);
				
				if (result != JFileChooser.APPROVE_OPTION) {
					return;
				}
				
				baseDirectory = dialog.getSelectedFile();
				textFieldSaveLocation.setText(baseDirectory.getAbsolutePath());
				textFieldAddSubreddit.setEditable(true);
			}
		});
		GridBagConstraints gbc_buttonBrowse = new GridBagConstraints();
		gbc_buttonBrowse.insets = new Insets(0, 0, 5, 0);
		gbc_buttonBrowse.gridx = 1;
		gbc_buttonBrowse.gridy = 1;
		panelMain.add(buttonBrowse, gbc_buttonBrowse);

		JSeparator separator3 = new JSeparator();
		GridBagConstraints gbc_separator3 = new GridBagConstraints();
		gbc_separator3.insets = new Insets(0, 0, 5, 0);
		gbc_separator3.gridwidth = 2;
		gbc_separator3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator3.gridx = 0;
		gbc_separator3.gridy = 2;
		panelMain.add(separator3, gbc_separator3);

		JLabel labelProgress = new JLabel("Progress");
		GridBagConstraints gbc_labelProgress = new GridBagConstraints();
		gbc_labelProgress.insets = new Insets(0, 0, 5, 0);
		gbc_labelProgress.gridwidth = 2;
		gbc_labelProgress.gridx = 0;
		gbc_labelProgress.gridy = 3;
		panelMain.add(labelProgress, gbc_labelProgress);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setString("CurrentTask");
		progressBar.setStringPainted(true);
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 4;
		panelMain.add(progressBar, gbc_progressBar);

		JSeparator separator4 = new JSeparator();
		GridBagConstraints gbc_separator4 = new GridBagConstraints();
		gbc_separator4.gridwidth = 2;
		gbc_separator4.insets = new Insets(0, 0, 0, 5);
		gbc_separator4.gridx = 0;
		gbc_separator4.gridy = 5;
		panelMain.add(separator4, gbc_separator4);
	}

	/**
	 * Allow drag and drop on the list.
	 * 
	 * Based off of this:
	 * http://docs.oracle.com/javase/tutorial/uiswing/dnd/dropmodedemo.html.
	 */
	@SuppressWarnings({"unchecked", "serial"})
	public class DragAndDropTransferHandler extends TransferHandler {
		/**
		 * Index from which the item was moved
		 */
		private int fromIndex = -1;
		/**
		 * New index.
		 */
		private int toIndex = -1;
		
		/**
		 * Only allow importing strings.
		 */
		public boolean canImport(TransferHandler.TransferSupport info) {
			if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}
			return true;
		}		
		
		/**
		 * Bundle up the selected items in a single list for export. Each line
		 * is separated by a newline.
		 */
		protected Transferable createTransferable(JComponent c) {
			JList<Subreddit> list = (JList<Subreddit>) c;
			fromIndex = list.getSelectedIndex();
			return new StringSelection(list.getSelectedValue().name);
		}

		/**
		 * We support both copy and move actions.
		 */
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		/**
		 * Perform the actual import. This demo only supports drag and drop.
		 */
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}

			JList<Subreddit> list = (JList<Subreddit>) info.getComponent();
			DefaultListModel<Subreddit> listModel = (DefaultListModel<Subreddit>) list
					.getModel();
			JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
			toIndex = dl.getIndex();

			//Create the new subreddit.
			Transferable t = info.getTransferable();
			String sub;
			try {
				sub = (String) t.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception e) {
				return false;
			}

			listModel.add(toIndex, new Subreddit(sub));
			return true;
		}

		/**
		 * Remove the items moved from the list.
		 */
		protected void exportDone(JComponent c, Transferable data, int action) {
			JList<String> source = (JList<String>) c;
			DefaultListModel<String> listModel = (DefaultListModel<String>) source
					.getModel();

			if (fromIndex < toIndex) {
				listModel.remove(fromIndex);
			} else {
				listModel.remove(fromIndex + 1);
			}
		}
	}
}
