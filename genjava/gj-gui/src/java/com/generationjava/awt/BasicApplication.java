package com.generationjava.awt;

import com.generationjava.awt.GJMenuItem;
import com.generationjava.awt.ReportEvent;
import com.generationjava.awt.RequestEvent;
import com.generationjava.awt.MulticastRequestEvent;
import com.generationjava.awt.InformationListener;

import java.awt.Frame;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Menu;
import java.awt.MenuShortcut;

import java.awt.FileDialog;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import org.apache.commons.lang.StringUtils;

/**
 * A simple framework which allows files to be opened and 
 * many files be in an open state. Thus files may also be closed.
 */
public abstract class BasicApplication extends Frame implements WindowListener, ActionListener {
    
    private String currentDir;
    private Object currentObj;
    private Hashtable openList;
    private Menu openMenu;
    
    private Vector listeners;
    private Hashtable menus;

    // used to signify whether the ctrl-,/ctrl-. is in effect.
    private boolean existsBefore;
    private boolean existsAfter;
    
    public BasicApplication(String title) {
        super(title);
        openList = new Hashtable();
        menus = new Hashtable();
        MenuBar bar = new MenuBar();
        this.setMenuBar(bar);

        registerMenuItem("File", "Open", "O");
        registerMenuItem("File","Close","Q");
        createNewBlock("File",0);
        registerMenuItem("File","Exit","E");
        openMenu = (Menu)registerMenu("Opened");
        bar.setHelpMenu((Menu)registerMenu("Help"));
        registerMenuItem("Help","About","Shift-A");
        registerMenuItem("Help","Licence","Shift-L");
        registerMenuItem("Help","QuickHelp","Shift-H");

/*
        Menu m = new Menu( getLabel("File") );
        m.addActionListener(this);
        bar.add(m);
        
        MenuItem mi = new GJMenuItem( getLabel("Open"), new MenuShortcut('O'), "Open" );
        m.add(mi);

        mi = new GJMenuItem( getLabel("Close"), new MenuShortcut('Q'), "Close" );
        m.add(mi);
        
        mi = new GJMenuItem( getLabel("Exit"), new MenuShortcut('E'), "Exit" );
        m.add(mi);

        openMenu = new Menu( getLabel("Opened") );
        openMenu.addActionListener(this);
        bar.add(openMenu);
        
        m = new Menu( getLabel("Help") );
        m.addActionListener(this);
        bar.setHelpMenu(m);
        
        mi = new GJMenuItem( getLabel("About"), new MenuShortcut('A',true), "About" );
        m.add(mi);
        */
        
    }

    // Menu Handling Code
    // Ported back from JBasicApplication
    private void addTopLevelMenu(Menu menu) {
        // push after help menu
        getMenuBar().add(menu);
    }
    // will not return null
    private Menu getMenu(String fqname) {
        Menu m = (Menu)menus.get(fqname);
        if(m == null) {
            m = createMenu(getLabel(getMenuName(fqname)));
            // TODO: Doesn't include the delimiter
            String parentName = StringUtils.substringBefore(fqname,".");
            if("".equals(parentName)) {
                addTopLevelMenu(m);
            } else {
                Menu parent = getMenu(parentName);
                // parent can't be null
                parent.add(m);
            }
            menus.put(fqname,m);
        }
        return m;
    }
    private String getMenuName(String fqname) {
        return StringUtils.substringAfter(fqname,".");
    }
    // basically inserts a separator
    protected void createNewBlock(String menuName, int block) {
        Menu menu = getMenu(menuName);
        int idx = getBlockIndex(menu, block);
        menu.insertSeparator(idx);
    }
    // Creates a sub-menu. If menuName is "", then goes in root.
    protected Object registerMenu(String subMenuName, char mnemonic) {
        Menu menu = getMenu(subMenuName);
        menu.setShortcut( new MenuShortcut((int)mnemonic) );
        return menu;
    }
    protected Object registerMenu(String subMenuName) {
        return getMenu(subMenuName);
    }
    protected Object registerMenu(String menuName, String subMenuName, String mnemonic) {
        return registerMenu(menuName, subMenuName, -2, null);
    }
    protected Object registerMenu(String menuName, String subMenuName, int block, String mnemonic) {
        Menu parent = getMenu(menuName);
        int idx = getBlockIndex(parent,block);
        Menu menu = createMenu( getLabel(subMenuName) );
        if(mnemonic != null) {
            menu.setShortcut( new MenuShortcut((int)mnemonic.charAt(0)));
        }
        parent.insert(menu, idx);
        return menu;
    }
    private Menu createMenu(String menuName) {
        Menu menu = new Menu( menuName);
        menu.setShortcut( new MenuShortcut((int)menuName.charAt(0)) );
        return menu;
    }
    protected Object registerMenuItem(String menuName, String itemName) {
        return registerMenuItem(menuName, itemName, -2, null);
    }
    protected Object registerMenuItem(String menuName, String itemName, String ch) {
        return registerMenuItem(menuName, itemName, -2, ch);
    }
    protected Object registerMenuItem(String menuName, String itemName, int block) {
        return registerMenuItem(menuName, itemName, -2, null);
    }
    protected Object registerMenuItem(String menuName, String itemName, int block, String ch) {
        String label = getLabel(itemName);
        MenuItem mi = new GJMenuItem( label, itemName );
        // set up accelerator?
        if(ch != null) {
            int mask = 0;
            if(ch.indexOf("Shift") != -1) {
                mask |= ActionEvent.SHIFT_MASK;
            }
            /* Not in AWT.
            if(true) {
                mask |= ActionEvent.CTRL_MASK;
            }
            */
            int key = (int)ch.charAt(ch.length()-1);
//            mi.setAccelerator( KeyStroke.getKeyStroke(key, mask) );
            mi.setShortcut( new MenuShortcut( key, (mask != 0) ) );
        }
        mi.addActionListener(this);
        return registerMenuItem(menuName, mi, block);
    }
    protected Object registerMenuItem(String menuName, MenuItem item, int block) {
        Menu m = (Menu)getMenu(menuName);
        int idx = getBlockIndex(m, block);
        m.insert(item,idx);
        return m;
    }

    // -2 implies the end, -1 means right at the front
    private int getBlockIndex(Menu menu, int block) {
        if(block == -1) {
            return 0;
        }
        /* Not available in AWT. Need to clone functionality here
        Component[] comps = menu.getMenuComponents();
        if(block == -2) {
            return comps.length;
        }
        int i;
        int sz = comps.length;
        for(i=0; i<sz; i++) {     // i is used outside the for loop
            if(comps[i] instanceof JSeparator) {
                block--;
                if(block == -1) {  
                    // reached end of specified block
                    break;
                }
            }
        }
        return i;
        */
        return menu.getItemCount();
    }
    // End Of Menu Handling Code

    protected String getLabel( String key ) {
        return key;
    }
    
    protected void inform( String inf, String old) {
        this.setTitle(inf);
    }
    
    abstract protected void applicationClosed();

    abstract protected Object fileOpened(String filename);

    abstract protected String getAbout();
    abstract protected String getLicence();
    abstract protected String getQuickHelp();
    
    abstract protected void selection(Object obj);

    abstract protected void fileClosed(Object obj);
    
    abstract protected void menuInvoked(String action);

    protected void closeOpenList() {
        Enumeration enum = openList.keys();
        while(enum.hasMoreElements()) {
            Object obj = openList.get( enum.nextElement() );
            fileClosed(obj);
        }
    }
    
    protected void report(String name) {
        this.report(name, null);
    }
    protected void report(String name, Object value) {
        if(listeners == null) {
            return;
        }
        ReportEvent re = new ReportEvent(name, value);
        Enumeration enum = listeners.elements();
        while(enum.hasMoreElements()) {
            InformationListener il = (InformationListener)enum.nextElement();
            il.report(re);
        }
    }
    
    protected Object request(String name, boolean multi) {    
        return this.request(name, null, multi);
    }
    protected Object request(String name, Object value, boolean multi) {
        if(listeners == null) {
            return null;
        }
        RequestEvent re;
        if(multi) {
            re = new MulticastRequestEvent(name, value);
        } else {
            re = new RequestEvent(name, value);
        }
        Enumeration enum = listeners.elements();
        while(enum.hasMoreElements()) {
            InformationListener il = (InformationListener)enum.nextElement();
            Object obj = il.request(re);
            if(obj != null) {
                if(multi) {
                    re.setValue(obj);
                } else {
                    return obj;
                }
            }
        }
        return null;
    }
    
    protected void addInformationListener(InformationListener il) {
        if(listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(il);
    }
    protected void removeInformationListener(InformationListener il) {
        if(listeners != null) {
            listeners.removeElement(il);
        }
    }
    
    // ActionListener interface
    public void actionPerformed(ActionEvent ae) {
                String cmd = ae.getActionCommand();
                if("Exit".equals(cmd)) {
                    applicationClosed();
                    this.dispose();
                } else
                if("Open".equals(cmd)) {
                    String filename = getFileName(FileDialog.LOAD);
                    Object obj = fileOpened(filename);
                    setCurrent(obj);
                    selection();
                    addToOpenList(obj);
                } else
                if("About".equals(cmd)) {
                    String about = getAbout();
                    String old = this.getTitle();
                    inform(about,old);
                } else
                if("Licence".equals(cmd)) {
                    String licence = getLicence();
                    String old = this.getTitle();
                    inform(licence,old);
                } else
                if("QuickHelp".equals(cmd)) {
                    String help = getQuickHelp();
                    String old = this.getTitle();
                    inform(help,old);
                } else
                if("Close".equals(cmd)) {
                    removeFromOpenList(currentObj);
                    fileClosed(currentObj);
                    setCurrent( popFromOpenList() );
            selection();
                } else
                if( openList.get(cmd) != null) {
                    setCurrent( openList.get(cmd) );
                    selection();
                } else {
                    menuInvoked(cmd);
                }
    }
    
    public Object setCurrent(Object obj) {
        Object tmp = currentObj;
        currentObj = obj;
        return tmp;
    }
    
    public Object getCurrent() {
        return currentObj;
    }
    
    protected String getTitle(Object obj) {
        return obj.toString();
    }
    
    public void addToOpenList(Object obj) {
        openList.put( getTitle(obj), obj);
                openMenu.add( getTitle(obj) );
//                reset( obj );
    }
    
    public void removeFromOpenList(Object obj) {
        openList.remove( getTitle(obj) );

                openMenu.removeAll();

                Enumeration enum = openList.keys();
                while (enum.hasMoreElements()) {
                        Object tmp = enum.nextElement();
                        openMenu.add( tmp.toString() );
                }
    }
    
    private Object getFromOpenList(String name) {
        return openList.get(name);
    }
    
    protected void selection() {
        selection( getCurrent() );
    }
    
    public Object popFromOpenList() {
        Enumeration enum = openList.keys();
                if(enum.hasMoreElements()) {
                        return openList.get( enum.nextElement() );
        } else {
            return null;
        }
                
    }
    
/*        private void reset(String filename) {
                int menusz = openMenu.getItemCount();
                existsBefore = false;
                existsAfter = false;
                for(int i=0;i<menusz;i++) {
                        openMenu.getItem(i).deleteShortcut();
                }
                for(int i=0;i<menusz;i++) {
                        if(filename.equals(openMenu.getItem(i).getLabel())) {
                                if(i != 0) {
                                        existsBefore = true;
                                        openMenu.getItem(i-1).setShortcut(new MenuShortcut(','));
                                }
                                if(i != menusz - 1) {
                                        existsAfter = true;
                                        openMenu.getItem(i+1).setShortcut(new MenuShortcut('.'));
                                }
                                break;
                        }
                }
        }*/
    
        protected String getFileName(int mode) {
                String prompt;

                // depending on load or save set the prompt                
                if (mode == FileDialog.LOAD) {
                        prompt = "Open File ";
                } else {
                        prompt = "Save File As ";
                }

                // create a file requester
                FileDialog d = new FileDialog(this, prompt, mode);

        if(currentDir != null) {
                    d.setDirectory(currentDir);
                }
                d.show();
                
                currentDir = d.getDirectory();
                                
        return d.getDirectory() + d.getFile();
        }        

    // WindowListener interface
    public void windowDeiconified(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
    }

    public void windowOpened(WindowEvent event) {
    }

    public void windowClosed(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
                this.dispose();
    }

}
