package directory_comparator.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import directory_comparator.ui.components.DirectoryChooser;
import directory_comparator.ui.components.FileJTextField;
import directory_comparator.ui.components.HorizontalSeparator;
import directory_comparator.ui.components.ReadOnlyJTextArea;
import directory_comparator.ui.components.buttons.StartJButton;
import directory_comparator.ui.components.buttons.StopJButton;
import directory_comparator.ui.workers.DirectoryComparisonTask;

/**
 * The window containing all the GUI for this application.
 */
public class AppWindow extends JFrame {
	
	private static final long serialVersionUID = 2516782550252442192L;

	public final static int GAP = 10;
	
	private final JPanel contentPane;
	private final JFileChooser directoryChooser = new DirectoryChooser();
	private FileJTextField oldDirectoryTextField = createJFileTextField();
	private FileJTextField newDirectoryTextField = createJFileTextField();
	private FileJTextField resultDirectoryTextField = createJFileTextField();
	private JButton oldDirectoryChooseButton;
	private JButton newDirectoryChooseButton;
	private JButton resultsDirectoryChooseButton;
	private DirectoryComparisonTask task;
	
	/**
	 * Creates the window.
	 */
	public AppWindow() {
		//Set up the window.
	    this.setTitle("Folder comparator");
	    this.setMinimumSize(new Dimension(450, 350));
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	    //Create the menu bar.
	    JMenuBar menuBar = new JMenuBar();
	    menuBar.setOpaque(true);
	
	    //Create a panel.
	    this.contentPane = new JPanel(new BorderLayout());
	    this.setContentPane(contentPane);
	    
	    //Set the menu bar.
	    this.setJMenuBar(menuBar);
	    
	    this.drawContentPane();
	
	    //Display the window.
	    this.pack();
	    this.setVisible(true);
	}
	
	public DirectoryComparisonTask getTask() {
		return task;
	}

	public void setTask(DirectoryComparisonTask task) {
		this.task = task;
	}

	public FileJTextField getSourceFileTextField() {
		return oldDirectoryTextField;
	}

	public FileJTextField getDestinationFileTextField() {
		return newDirectoryTextField;
	}
	
	public FileJTextField getResultDirectoryTextField() {
		return resultDirectoryTextField;
	}

	public JButton getSourceFileChooseButton() {
		return oldDirectoryChooseButton;
	}

	public JButton getDestinationFileChooseButton() {
		return newDirectoryChooseButton;
	}
	
	public JButton getResultsDirectoryChooseButton() {
		return resultsDirectoryChooseButton;
	}

	private static void adjustTextField(JTextField jTextField) {
		jTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, jTextField.getPreferredSize().height));
	}
	
	private static FileJTextField createJFileTextField() {
		FileJTextField jFileTextField = new FileJTextField();
		adjustTextField(jFileTextField);
		return jFileTextField;
	}
	
	private JButton addFolderSelectionComponents(
		Container verticalPanel, FileJTextField fileTextField, String label
	) {
		JPanel folderSelectionPanel = new JPanel();
		folderSelectionPanel.setLayout(new BoxLayout(folderSelectionPanel, BoxLayout.Y_AXIS));
		folderSelectionPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
		folderSelectionPanel.add(new JLabel(label));
		folderSelectionPanel.add(Box.createVerticalStrut(GAP));
		JPanel horizontalPanel = new JPanel();
	    BoxLayout horizontalLayout = new BoxLayout(horizontalPanel, BoxLayout.X_AXIS);
		horizontalPanel.setLayout(horizontalLayout);
	    
	    /*
	     * Necessary so the horizontal panel and the labels are aligned
	     * the same way horizontally.
	     */
	    horizontalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	    
		horizontalPanel.add(fileTextField);
		horizontalPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));
		JButton selectDirectoryButton = new JButton("Select folder");
		selectDirectoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = AppWindow.this.directoryChooser.showOpenDialog(verticalPanel);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File directory = AppWindow.this.directoryChooser.getSelectedFile();
		            fileTextField.setSelectedFile(directory);
		        }
			}}
		);
		horizontalPanel.add(selectDirectoryButton);
		folderSelectionPanel.add(horizontalPanel);
		verticalPanel.add(folderSelectionPanel);
		verticalPanel.add(new HorizontalSeparator());
		return selectDirectoryButton;
	}
	
	private JPanel drawFileCopyPanel() {
		JPanel fileCopyPanel = new JPanel();
		fileCopyPanel.setLayout(new BoxLayout(fileCopyPanel, BoxLayout.Y_AXIS));
		
		JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JTextArea taskOutput = new ReadOnlyJTextArea();
		JButton stopButton = new StopJButton(this);
		JButton startButton = new StartJButton(this, progressBar, taskOutput, stopButton);
        
        JPanel horizontalPanel = new JPanel();
        BoxLayout horizontalLayout = new BoxLayout(horizontalPanel, BoxLayout.X_AXIS);
        horizontalPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        horizontalPanel.setLayout(horizontalLayout);
        horizontalPanel.add(startButton);
        horizontalPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));
        horizontalPanel.add(stopButton);
        horizontalPanel.add(Box.createRigidArea(new Dimension(GAP, 0)));
        horizontalPanel.add(progressBar);
        
        fileCopyPanel.add(horizontalPanel);
        JComponent scrollableTaskOutput = new JScrollPane(taskOutput);
        scrollableTaskOutput.setBorder(
        	BorderFactory.createCompoundBorder(
    			BorderFactory.createEmptyBorder(0, GAP, GAP, GAP),
        		scrollableTaskOutput.getBorder()
        	)
        );
        
        // Make the scroll pane want to be at least as big as the text area.
        Insets scrollableTaskOutputInsets = scrollableTaskOutput.getInsets();
        Dimension taskOutputPreferredSize = taskOutput.getPreferredSize();
        scrollableTaskOutput.setPreferredSize(Tools.add(taskOutputPreferredSize, scrollableTaskOutputInsets));
        
        fileCopyPanel.add(scrollableTaskOutput);
        
        return fileCopyPanel;
	}
	
	private void drawContentPane() {
		JPanel verticalPanel = new JPanel();
		verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
		this.oldDirectoryChooseButton = addFolderSelectionComponents(
			verticalPanel, this.oldDirectoryTextField, "Old folder"
		);
		this.newDirectoryChooseButton = addFolderSelectionComponents(
			verticalPanel, this.newDirectoryTextField, "New folder"
		);
		this.resultsDirectoryChooseButton = addFolderSelectionComponents(
			verticalPanel, this.resultDirectoryTextField, "Where do you want to store the result of the comparison?"
		);
		JPanel fileCopyPanel = drawFileCopyPanel();
		
		/*
	     * Necessary so the file copy panel and the labels above are aligned
	     * the same way horizontally.
	     */
		fileCopyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		verticalPanel.add(fileCopyPanel);
		this.contentPane.add(verticalPanel, BorderLayout.CENTER);
	}
}
