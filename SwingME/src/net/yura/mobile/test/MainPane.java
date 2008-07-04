package net.yura.mobile.test;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.yura.mobile.gui.ActionListener;
import net.yura.mobile.gui.CommandButton;
import net.yura.mobile.gui.RootPane;
import net.yura.mobile.gui.Theme;
import net.yura.mobile.gui.border.CompoundBorder;
import net.yura.mobile.gui.border.EmptyBorder;
import net.yura.mobile.gui.border.ImageBorder;
import net.yura.mobile.gui.border.LineBorder;
import net.yura.mobile.gui.cellrenderer.DefaultListCellRenderer;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.CheckBox;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.DropDownMenu;
import net.yura.mobile.gui.components.Label;
import net.yura.mobile.gui.components.List;
import net.yura.mobile.gui.components.MultilineLabel;
import net.yura.mobile.gui.components.Panel;
import net.yura.mobile.gui.components.RadioButton;
import net.yura.mobile.gui.components.ScrollPane;
import net.yura.mobile.gui.components.Spinner;
import net.yura.mobile.gui.components.TextField;
import net.yura.mobile.gui.components.Window;
import net.yura.mobile.gui.components.TabbedPane;
import net.yura.mobile.gui.layout.BorderLayout;
import net.yura.mobile.gui.layout.FlowLayout;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.util.Option;

public class MainPane extends RootPane implements ActionListener {

	private Panel mainmenu;
        private Panel componentTest;
        private Panel info;
        private Panel border;
        private Panel tabPanel;
        
        private Image image;
        private MultilineLabel infoLabel;
	private MultilineLabel loadPanel;
	private Vector images;
	private Window mainWindow;
	
	public MainPane(MyMidlet a) {
		super(a,0,null);
	}

	public void initialize() {
		
		mainWindow = getCurrentWindow();
		
		RootPane.setDefaultStyle( new Theme() );
                
		mainWindow.getContentPane().setBackground(0x00EEEEEE);
		mainWindow.getContentPane().setTransparent(false);
                
		mainWindow.setActionListener(this);

                try {
                    image = Image.createImage("/world_link.png");
                }catch (IOException ex) {
                    ex.printStackTrace();
                }
                
		actionPerformed("mainmenu");
		
	}
	
        private void addMainMenuButton(String a,String b) {
            
            Button infoButton = new Button(a);
            infoButton.setActionCommand(b);
            infoButton.addActionListener(this);
            mainmenu.add(infoButton);
        }
        
	public void actionPerformed(String actionCommand) {

		if ("exit".equals(actionCommand)) {
			
			exit();
			
		}
		else if ("mainmenu".equals(actionCommand)) {
			
			if (mainmenu==null) {
			
				mainmenu = new Panel( new FlowLayout(Graphics.VCENTER) );
				
				Label helloWorld = new Label("yura.net mobile");
				mainmenu.add(helloWorld);
				
                                addMainMenuButton("Info","info");
				addMainMenuButton("Load","loadpanel");
				addMainMenuButton("Error","throwerror");
                                addMainMenuButton("Component Test","componentTest");
                                addMainMenuButton("Border Test","borderTest");
                                addMainMenuButton("Tab Test","tabTest");
                                addMainMenuButton("Window Test 1","windowTest1");
			}
			
			addToScrollPane(mainmenu, null, new CommandButton("Exit","exit") );
			
		}
                else if ("windowTest1".equals(actionCommand)) {

                    Window test1 = new Window( new LineBorder() );
                    test1.makeDecoration("Window Title",image,true,true,true);
                    test1.getContentPane().add(new Label("LALAL TEST 1"));
                    test1.setBounds(10, 10, getWidth()-20, getHeight()/2);
                    test1.getContentPane().doLayout();
                    test1.getContentPane().setBackground(0x00FFFFFF);
                    openNewWindow(test1);
                    
                }
                else if ("info".equals(actionCommand)) {
			
			if (info==null) {
			
                                infoLabel = new MultilineLabel("...");
                                infoLabel.setSize(getWidth(),infoLabel.getHeight());
                                
				info = new Panel( new BorderLayout() ) {
                                        { selectable=true; }
                                  	public void pointerEvent(int type, int x, int y) {
                                            infoLabel.setText("pointerEvent: "+x+","+y);
                                            infoLabel.repaint();
                                        }  
                                    	public boolean keyEvent(KeyEvent keypad) {
                                            int code1 = keypad.getJustPressedKey();
                                            int code2 = keypad.getJustReleasedKey();
                                            int code3 = keypad.getIsDownKey();
                                            int code = (code3==0)?code2:code3;
                                            if (code!=0) {
                                                infoLabel.setText("keyEvent: "+code +"\nKeyText: "+keypad.getKeyText(code));
                                                if (code>0) {
                                                    infoLabel.append("\nchar: "+(char)code);
                                                    
                                                    char inputChar = keypad.getKeyChar(0, false);
                                                    if (inputChar!=0) {
                                                        infoLabel.append("\ninput char: "+ inputChar );
                                                    }
                                                }
                                                int gcode = keypad.getKeyAction(code);
                                                if (gcode!=0) {
                                                    infoLabel.append("\ngame action: "+gcode+"\n");
                                                    switch(gcode) {
                                                        case 1: infoLabel.append("UP"); break;
                                                        case 2: infoLabel.append("LEFT"); break;
                                                        case 5: infoLabel.append("RIGHT"); break;
                                                        case 6: infoLabel.append("DOWN"); break;
                                                        case 8: infoLabel.append("FIRE"); break;
                                                        case 9: infoLabel.append("GAME_A"); break;
                                                        case 10: infoLabel.append("GAME_B"); break;
                                                        case 11: infoLabel.append("GAME_C"); break;
                                                        case 12: infoLabel.append("GAME_D"); break;
                                                        default: infoLabel.append("Unknown"); break;
                                                    }
                                                }
                                                if (code1!=0) { infoLabel.append("\nJustPressed"); }
                                                if (code2!=0) { infoLabel.append("\nJustReleased"); }
                                                if (code3!=0) { infoLabel.append("\nIsDown"); }
                                                if (code1==0 && code3!=0) { infoLabel.append("\nHeldDown"); }
                                                infoLabel.repaint();
                                            }
                                            return true;
                                        }
                                };
                                
                                info.add( new Label("Info for keys and pointer"),Graphics.TOP );

                                info.add(infoLabel);

			}
			
			addToScrollPane(info, null,  new CommandButton("Back","mainmenu") );
			
		}
                else if ("componentTest".equals(actionCommand)) {
			
			if (componentTest==null) {
			
				componentTest = new Panel( new FlowLayout(Graphics.VCENTER) );

				componentTest.add( new Label("a Label") );
                                if (image!=null) { componentTest.add( new Label( image ) ); }
				componentTest.add( new Button("a Button") );
                                componentTest.add( new CheckBox("a CheckBox") );
                                componentTest.add( new RadioButton("a RadioButton") );
                                
                                Vector items = new Vector();
                                items.addElement("One");
                                items.addElement(new Option("2","Two",image));
                                items.addElement(new Option("3","Three option"));
                                items.addElement(new Option("4",null,image));
                                
                                componentTest.add( new DropDownMenu(items) );
                                componentTest.add( new Spinner(items, false));
                                
                                componentTest.add( new TextField(javax.microedition.lcdui.TextField.NUMERIC) );
                                componentTest.add( new TextField(javax.microedition.lcdui.TextField.ANY) );
                                componentTest.add( new TextField(javax.microedition.lcdui.TextField.ANY | javax.microedition.lcdui.TextField.PASSWORD) );
                                
                                componentTest.add( new MultilineLabel("a MultilineLabel with a very long bit of text that will need to go onto more then 1 line") );
                                
                                componentTest.add( new List(items,new DefaultListCellRenderer(),false) );
			}
			
			addToScrollPane(componentTest, null,  new CommandButton("Back","mainmenu") );
			
		}
		else if ("loadpanel".equals(actionCommand)) {
			
			if (loadPanel==null) {
				
				loadPanel = new MultilineLabel("");
				loadPanel.setAlignment(Graphics.LEFT);
				images = new Vector();
                                //loadPanel.setSize(getWidth()*2, loadPanel.getHeight());
			}
			
			addToScrollPane(loadPanel, new CommandButton("Load","load") , new CommandButton("Back","mainmenu") );
			//setActiveComponent(loadPanel);
		}
                else if ("load".equals(actionCommand)) {
			
			Image testImage;
			String message;
			try {
				testImage = Image.createImage(500, 500);
				images.addElement(testImage);
				message = "loaded: "+testImage;
			}
			catch (Throwable e) {
				message = "unable to load: "+e.toString();
				e.printStackTrace();
			}
			loadPanel.setSize( getWidth()-ScrollPane.getBarThickness(getWidth(), getHeight()) , loadPanel.getHeight());
			loadPanel.append(message+"\n");
			//getContentPane().doLayout();
			mainWindow.getContentPane().repaint();
		}
                else if ("borderTest".equals(actionCommand)) {
			
			if (border==null) {
				
				border = new Panel( new FlowLayout(Graphics.VCENTER) );
                                Label test1 = new Label("CompoundBorder test");
                                test1.setBorder( new CompoundBorder(
                                        new LineBorder(0x00FF0000, 3),
                                        new CompoundBorder(
                                            new LineBorder( 0x0000FF00, 0x00FFFFFF,4,true), 
                                            new LineBorder(0x000000FF, 3))) );
                                border.add(test1);
                                
                                Label test2 = new Label("ImageBorder test");
                                test2.setBorder(new ImageBorder("/skin1.skin"));
                                border.add(test2);
                                
                                Panel menu = new Panel(new FlowLayout(Graphics.VCENTER,0));
                                menu.setBorder(new CompoundBorder(new ImageBorder("/skin2.skin"),new EmptyBorder(-12, -12, -12, -12)));
                                menu.add(new Button("menu TEST item 1"));
                                menu.add(new Button("menu TEST item 2"));
                                menu.add(new Button("menu TEST item 3"));
                                menu.add(new Button("menu TEST item 4"));
                                border.add(menu);
			}
			
			addToScrollPane(border, null , new CommandButton("Back","mainmenu") );
		}
                else if ("tabTest".equals(actionCommand)) {
			
			if (tabPanel==null) {
				
				tabPanel = new Panel( new BorderLayout() );
                                tabPanel.add(new Label("Tab Test"),Graphics.TOP);
                                
                                Panel tab1 = new Panel( "Tab 1" );
                                tab1.setLayout(new FlowLayout());
                                tab1.setBackground(0x00FF0000);
                                tab1.add( new Label("This is tab ONE") );
                                
                                Panel tab2 = new Panel( new FlowLayout() );
                                tab2.setBackground(0x0000FF00);
                                tab2.add( new Button("This is tab TWO") );

                                Panel tab3 = new Panel( new FlowLayout(Graphics.VCENTER) );
                                List l3 = new List( new DefaultListCellRenderer() );
                                tab3.setBackground(0x000000FF);
                                
                                Vector anotherlist = new Vector();
                                
                                for (int c=0;c<20;c++) {
                                    anotherlist.addElement("A REALLY LONG LIST ITEM, that will need things like side scrolling "+c);
                                }
                                l3.setListData(anotherlist);
                                tab3.add(new Label("a lable for the list"));
                                tab3.add(l3);
                                
                                Panel tab4 = new Panel( new BorderLayout() );
                                tab4.add(new Label("Tab 4 title"),Graphics.TOP);
                                tab4.add(new ScrollPane(new List(anotherlist,new DefaultListCellRenderer(),false)));
                                
                                TabbedPane tabbedPane = new TabbedPane();
                                
                                tabbedPane.addTab(tab1);
                                tabbedPane.addTab("TAB 2", image, tab2);
                                tabbedPane.addTab("tab 3 a long one eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", new ScrollPane(tab3));
                                tabbedPane.addTab(null,image,tab4);
                                
                                tabPanel.add(tabbedPane);
                                
			}
			
			addToScrollPane(tabPanel, null , new CommandButton("Back","mainmenu") );
		}
		else if ("throwerror".equals(actionCommand)) {
			throw new RuntimeException("some bad error happened!");
		}
		else {
			
			System.out.println("Unknown Command: "+actionCommand);
		}
		
	}
	
	private ScrollPane scroll;
	private void addToScrollPane(Component a,CommandButton b,CommandButton c) {
		
		mainWindow.getContentPane().removeAll();
		
		if (scroll==null) {
			
			scroll = new ScrollPane();
		}
		
		scroll.add(a);
		
		mainWindow.getContentPane().add(scroll);
		
		mainWindow.getContentPane().doLayout();
		
		mainWindow.setWindowCommand(0, b);
		mainWindow.setWindowCommand(1, c);
		
		//setActiveComponent(getContentPane());
		setupActiveComponent();
		
		mainWindow.repaint();
	}

}
