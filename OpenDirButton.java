package photo_renamer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/** A button used to open a directory and list all images located under it. */
class OpenDirButton extends JButton implements ActionListener {

    /** The serialVersionUID for this class. */
    private static final long serialVersionUID = 4685844587906059403L;
    /** The PhotoRenamer application. */
    private PhotoRenamer mainApp;
    /** The JFileChooser this button invokes. */
    private JFileChooser fileChooser;

    /**
     * Construct the components for a button to open a directory.
     *
     * @param label   the label of this button
     * @param mainApp the PhotoRenamer application this button is attached to
     */
    OpenDirButton(String label, PhotoRenamer mainApp) {
        super(label);
        this.addActionListener(this);
        this.fileChooser = new JFileChooser();
        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.mainApp = mainApp;
    }

    /**
     * Attempt to render a list of images in a directory upon click.
     *
     * @param click the click event
     */
    @Override
    public void actionPerformed(ActionEvent click) {
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                this.renderImageList();
            } catch (IOException | ClassNotFoundException e1) {
                JOptionPane.showMessageDialog(new JFrame(), "Failed to search directory!");
                e1.printStackTrace();
            }
        }
    }

    /**
     * Render the list of images under the selected directory.
     *
     * @throws IOException            an IOException exception
     * @throws ClassNotFoundException a ClassNotFoundException exception
     */
    @SuppressWarnings("unchecked")
    private void renderImageList() throws IOException, ClassNotFoundException {
        File dir = fileChooser.getSelectedFile();
        if (dir.exists()) {
            mainApp.dirLabel.setText("Images in " + dir.getAbsolutePath());
            ArrayList<ImageFile> imageFiles = ImageFileManager.getImages(dir, new ArrayList<>(), mainApp.historyManager);
            DefaultListModel<ImageFile> listModel = new DefaultListModel<>();
            imageFiles.forEach(listModel::addElement);
            JList<String> imageList = new JList(listModel);
            imageList.addMouseListener(
                    new ImageSelectionListener(mainApp, imageList, listModel));
            JScrollPane imageListScroller = new JScrollPane(imageList);
            imageListScroller.setSize(450, 300);

            mainApp.dirPanel.removeAll();
            mainApp.dirPanel.add(imageListScroller);
            mainApp.mainFrame.revalidate();
            mainApp.mainFrame.pack();
            mainApp.mainFrame.repaint();
        }
    }
}
