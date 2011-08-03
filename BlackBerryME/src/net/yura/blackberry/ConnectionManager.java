package net.yura.blackberry;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.CoverageStatusListener;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.WLANConnectionListener;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.ui.component.Dialog;
import net.yura.mobile.io.SocketClient;

public class ConnectionManager implements GlobalEventListener   {
	
	private WIFIListener connWIFIListener;
	private RadioListener connRadioListener;
	
	public static String mostRecentAppendString = null;
	
	public WIFIListener getConnWIFIListener(){
		return connWIFIListener ;
	}
	
	public RadioListener getConnRadioListener(){
		return connRadioListener ;
	}
	
	class WIFIListener implements WLANConnectionListener {
		public void networkConnected() {
			setCoverage();
			updateConnection();
		}
		public void networkDisconnected(int reason) {
			setCoverage();
			updateConnection();
		}
	}
	
	class RadioListener implements CoverageStatusListener {
		public void coverageStatusChanged(int newCoverage) {
			setCoverage();
			updateConnection();
		}		
	}
	
	private static final long ID = 0x1431cf6271d3b1edL;

	private static String IPPP = "IPPP";

	private static ConnectionManager _manager;
	private boolean _mdsSupport;
	private boolean _bisSupport;
	private boolean _wapSupport;
	private boolean _wifiSupport;
	private boolean _tcpSupport;

	private ConnectionManager() {
		setCoverage();
		updateConnection();
		connRadioListener = new RadioListener();
		connWIFIListener = new WIFIListener();
	}

	public static ConnectionManager getInstance() {
		if (_manager == null) {
			_manager = new ConnectionManager();
		}
		return _manager;
	}    
	
	public String getInternetConnectionString() {
		String connStr = null;
		if (_wifiSupport){
			connStr = ";interface=wifi";
		} else if (_mdsSupport) {
			connStr = ";deviceside=false";
		} else if (_bisSupport) {
			connStr = ";deviceside=false;ConnectionType=mds-public";
		} else if (_tcpSupport ) {
			 String carrierUid = getCarrierBIBSUid();
	            if (carrierUid == null) {
	                connStr = ";deviceside=true";
	            } else {	                
	                connStr = ";deviceside=false;connectionUID="+carrierUid + ";ConnectionType=mds-public";
	            }
		} else if (_wapSupport) {
			// TODO: WAP support
		} else {
			// TODO: No internet connection
		}
		return connStr;
	}

	public String getConnectionType() {
		if (_mdsSupport) {
			return "MDS";
		} else if (_bisSupport) {
			return "BIS-B";
		} else if (_wapSupport) {
			return "WAP";
		} else {
			return "Direct TCP";
		}
	}

	private void setCoverage() {
		_mdsSupport = CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_MDS);
		_bisSupport = (CoverageInfo.isCoverageSufficient(CoverageInfo.COVERAGE_BIS_B));
		_wifiSupport = (WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED);
		_tcpSupport = ((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT);
	}

	private void updateConnection(){
		mostRecentAppendString = getInternetConnectionString();
		SocketClient.connectAppend = mostRecentAppendString;
	}
	
	private void parseServiceBooks() {
		ServiceBook sb = ServiceBook.getSB();
		ServiceRecord[] records = sb.findRecordsByCid(IPPP);
		if (records == null) {
			return;
		}

		int numRecords = records.length;
		for (int i = 0; i < numRecords; i++) {
			ServiceRecord myRecord = records[i];
			String name = myRecord.getName();
			String uid = myRecord.getUid();
			if (myRecord.isValid() && !myRecord.isDisabled()) {
				int encryptionMode = myRecord.getEncryptionMode();
				if (encryptionMode == ServiceRecord.ENCRYPT_RIM) {
					_mdsSupport = true;
				} else {
					_bisSupport = true;
				}
			}
		}
	}

    /**
     * Looks through the phone's service book for a carrier provided BIBS network
     * @return The uid used to connect to that network.
     */
    private static String getCarrierBIBSUid() {   	
        ServiceRecord[] records = ServiceBook.getSB().getRecords();
        for(int currentRecord = 0; currentRecord < records.length; currentRecord++) {
            if(records[currentRecord].getCid().toLowerCase().equals("ippp")) {                
            	if(records[currentRecord].getName().toLowerCase().indexOf("bibs") >= 0) {
                    return records[currentRecord].getUid();
                }
            }
        }
        return null;
    } 
	
	public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
		if (guid == ServiceBook.GUID_SB_ADDED || guid == ServiceBook.GUID_SB_CHANGED || guid == ServiceBook.GUID_SB_OTA_SWITCH || guid == ServiceBook.GUID_SB_OTA_UPDATE
				|| guid == ServiceBook.GUID_SB_REMOVED) {
			Dialog.inform("Service Book Global Event Received");
			parseServiceBooks();
			updateConnection();
		}
	}
	
}