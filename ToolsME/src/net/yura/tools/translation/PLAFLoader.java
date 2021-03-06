package net.yura.tools.translation;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.yura.me4se.ME4SEPanel;
import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.TextComponent;
import net.yura.mobile.gui.layout.XULLoader;
import net.yura.mobile.gui.plaf.LookAndFeel;
import net.yura.mobile.gui.plaf.SynthLookAndFeel;

/**
 * @author Yura Mamyrin
 */
public class PLAFLoader implements ActionListener {

    private File synth_file,synth_base;
    XULLoader loader;
    Dialog dialog;

    public LookAndFeel loadNewSynth(Window parent) {
        //this.parent = parent;

        ME4SEPanel wrapper = new ME4SEPanel();

        Panel panel = null;
        try {
            loader = XULLoader.load(getClass().getResourceAsStream("/synth_load.xml"), this);
            panel = (Panel)loader.getRoot();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

//        ME4SEDialog dialog = new ME4SEDialog(parent, "Load Synth", ModalityType.DOCUMENT_MODAL);
//        dialog.setContentPane(panel);
//        dialog.setVisible(true);


        wrapper.add(panel);

        dialog = new Dialog(parent, "Load Synth", ModalityType.DOCUMENT_MODAL);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                PLAFLoader.this.actionPerformed("cancel_synth");
            }
        });

        dialog.add(wrapper);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);


        if (synth_file!=null) {

                ME4SESynth synth = new ME4SESynth(synth_base);
                try {
                    synth.load( new FileInputStream(synth_file) );
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(parent, ex.toString() );
                }

                try {
                    return loadSynth(synth_base,synth_file);
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(parent, ex.toString() );
                }

        }
        return null;
    }

    public static LookAndFeel loadSynth(File base, File file) throws Exception {

        ME4SESynth synth = new ME4SESynth(base);
        synth.load( new FileInputStream(file) );
        return synth;
    }

    public void actionPerformed(String arg0) {

        if ("browse_dir".equals(arg0)) {

            JFileChooser chooser = new JFileChooser(synth_base==null?new File("."):synth_base);

            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showOpenDialog(dialog);

            if (result==JFileChooser.APPROVE_OPTION) {
                synth_base = chooser.getSelectedFile();
                TextComponent base_dir = (TextComponent)loader.find("base_dir");
                base_dir.setText(synth_base.toString());

            }

        }
        else if ("brouse_file".equals(arg0)) {

            JFileChooser chooser = new JFileChooser(synth_base);

            int result = chooser.showOpenDialog(dialog);

            if (result==JFileChooser.APPROVE_OPTION) {
                synth_file = chooser.getSelectedFile();
                TextComponent synthFileText = (TextComponent)loader.find("synth_file");
                synthFileText.setText(synth_file.toString());
            }

        }
        else if ("load_synth".equals(arg0)) {

            dialog.setVisible(false);
        }
        else if ("cancel_synth".equals(arg0)) {

            synth_file = null;
            dialog.setVisible(false);

        }
        else {
            System.out.println("actionPerformed "+arg0);
        }
    }


    public static class ME4SESynth extends SynthLookAndFeel {

        private File current;

        public ME4SESynth(File file) {
            current = file;
        }

        @Override
        protected InputStream getResourceAsStream(String path) {
            try {
                return new FileInputStream(new File(current, path));
            }
            catch (Exception ex) {
                System.err.println("not able to find resource "+path);
                ex.printStackTrace();
                return null;
            }
        }
    }
}
