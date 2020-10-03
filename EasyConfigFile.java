package me.RaulH22;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// ConfigFileYML v2.5
public class EasyConfigFile {

	//================================================ Variables ================================================ //
	private Main pl = Main.getPlugin(Main.class);
	private String prefix;
	private String filePath;
	private boolean isDefault;
	private File file;
	private FileConfiguration config;
	private boolean isBackup;
	
	//================================================ Constructors ================================================ //
	public EasyConfigFile(String path) {
		createFile(path,false);
	}

	public EasyConfigFile(String path,boolean isBackup) {
		createFile(path,isBackup);
	}
	
	//================================================ Creator ================================================ //

	private void createFile(String path,boolean isBackup) {
		this.isBackup = isBackup;
		prefix = "["+ pl.getDescription().getName() + "] ";
		if(!path.contains(".yml")) path = path+".yml";
		this.filePath = path;
		this.isDefault = isDefaultIn();
		this.file = loadFile(true);
		this.config = loadConfig();
	}
	
	
	//================================================ Creator methods ================================================ //
	//Load the file
	public File loadFile(boolean createIfNeeds) {
		file = new File(pl.getDataFolder() , this.filePath);
		if(!createIfNeeds) return file;
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				pl.saveResource( this.filePath, false);
				if(isDefault||isBackup) Bukkit.getConsoleSender().sendMessage(prefix + "§7New file: '§b" + getPath() + "§7' successful created!!");
			}
			catch (Exception exp) {
				try {
//					file.createNewFile();
					if(isDefault||isBackup) {
						Bukkit.getConsoleSender().sendMessage(prefix + "§7New file: '§b" + getPath() + "§7' successful created!!");
					}
				} catch (Exception e1) {
					Bukkit.getConsoleSender().sendMessage(prefix + "§cError trying to CREATE: '§e" + getPath() + "§c'!!!");
					Bukkit.getConsoleSender().sendMessage(prefix + "§cError line ( "+ Thread.currentThread().getStackTrace()[1].getLineNumber()    +  ")...");
				}
			}
		}
		return file;
	}
	
	//Load the config of file
	public FileConfiguration loadConfig() {
		config = YamlConfiguration.loadConfiguration(file);
		try {
			config.load(file);
				if(isDefault) {
					Bukkit.getConsoleSender().sendMessage( prefix +"§7File '§b" + getPath() + "§7' loaded!");
				}
		} catch (Exception e) {
			if(isDefault) {
				Bukkit.getConsoleSender().sendMessage(prefix + "§cError trying to LOAD: '§e" + getPath() + "§c'!!!" );
			}
		}	
		
		return config;
	}
	
	//================================================ Methods 01 ================================================ //
	//Check if its default
	private boolean isDefaultIn() {
		try {
			String teste = pl.getClass().getResource("/"+this.filePath).getFile();
			new File(teste);
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	public boolean isDefault() {
		return isDefault;
	}

	//Get Path
	public String getPath() {
		final String p = filePath;
		if(p.contains(pl.getDescription().getName())) {
			String[] p2 = p.split(pl.getDescription().getName());
			return pl.getDescription().getName()+p2[1];
		}
		return p;
	}
	//Get Config
	public FileConfiguration getConfig() {
		return config;
	}
	//Get File
	public File getFile() {
		return file;
	}
	//Exits
	public boolean exists() {
		return file.exists();
	}

	//==================== Console Messages  ==================== //
	//Error message
	private void getObjectErrorMessage(String type,String path) {
		Bukkit.getConsoleSender().sendMessage(prefix + 
				"§4§lERROR in Configs - §r§cThe file '§e" + getPath()  + "§c' have a error in the configs section!!!");
		Bukkit.getConsoleSender().sendMessage(prefix + 
				"§8---- §r§cPath in the file: §e'"+ path +"§c'");
		Bukkit.getConsoleSender().sendMessage(prefix + 
				"§8---- §r§cThe object must be an instance of: "+ type +"§c'");
		Bukkit.getConsoleSender().sendMessage(prefix + 
				"§8---- §r§ePlease try to fix it or delete the configs and reload the plugin!!!");
		Bukkit.getConsoleSender().sendMessage(" ");
	}

	private void errorMsg(Exception exception) {
		//Thread.currentThread().getStackTrace()[1].getLineNumber();
		//exception.getStackTrace()[0].getLineNumber();
		int line = exception.getStackTrace()[0].getLineNumber();
		Bukkit.getConsoleSender().sendMessage(prefix +
				"§4§lERROR("+this.getClass().getName()+")§r§c - line("+ line +  "); "
				+ "path(" + getPath() +")"
		);
	}
	
	@SuppressWarnings("unused")
	private void testeLine(Exception exception) {
		int line = exception.getStackTrace()[0].getLineNumber();
		Bukkit.getConsoleSender().sendMessage(prefix + "§aTESTING THE LINE  === ("+ line +  ")");
	}

	//================================================ Methods 02 ================================================ //
	//==================== String  ==================== //
	public String getStringColored(String path) {
		return getString(path).replace("&", "§");
	}

	public String getString(String path) {
		return getString(path, getFlags());
	}
	
	public String getString(String path, Map<String,String> flags) {
		String string = null;
		try {
			string = (String)config.get(path);
		}catch (Exception e) {
			if(!getStringIfNull(flags).equals("")) {
				string = getStringIfNull(flags);
				if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("string",path);
			}
			else {
				try {
					if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("string",path);
					fix(path);
					string = (String)config.get(path);
				}catch (Exception exception) {
					errorMsg(exception);
				}
			}
		}
		return string;
	}
	
	private String getStringIfNull(Map<String,String> flags){
		if(flags.containsKey("ifNull")) return flags.get("ifNull");
		else return "";
	}
	//==================== Integer  ==================== //
	public int getInt(String path) {
		return getInt(path,getFlags());
	}
	
	public int getInt(String path, Map<String,String> flags) {
		int i;
		try {
			i = (int) config.get(path);
		}catch (Exception e) {
			if(flags.containsKey("ifNull")) {
				i = getIntIfNull(flags);
				if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("integer",path);
			}
			else {
				try {
					if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("integer",path);
					fix(path);
					i = (int) config.get(path);
				}catch (Exception exception) {
					i = 0;
					errorMsg(exception);
				}
			}
		}
		return i;
	}
	
	private int getIntIfNull(Map<String,String> flags){
		int i = Integer.getInteger(flags.get("ifNull"));
		return i;
	}
	
	//==================== Double  ==================== //
	public double getDouble(String path) {
		return getDouble(path,getFlags());
	}
	public double getDouble(String path, Map<String,String> flags) {
		double d = 0;
		try {
			d = (double) config.get(path);
		}catch (Exception e) {
			try {
				d = (int) config.get(path);
			}catch (Exception e2) {
				if(flags.containsKey("ifNull")) {
					d = getDoubleIfNull(flags);
					if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("double",path);
				}
				else {
					try {
						if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("double",path);
						fix(path);
						d = (double) config.get(path);
					}catch (Exception exception) {
						d = 0;
						errorMsg(exception);
					}
				}
			}
		}
		return d;
	}

	private double getDoubleIfNull(Map<String,String> flags) {
		double i = Double.valueOf(flags.get("ifNull"));
		return i;
	}
	
	//==================== Long  ==================== //
	public double getLong(String path) {
		return getLong(path,getFlags());
	}
	
	public long getLong(String path, Map<String,String> flags) {
		long l = 0;
		try {
			l = (long) config.get(path);
		}catch (Exception e) {
			try {
				l = (int) config.get(path);
			}catch (Exception e2) {
				if(flags.containsKey("ifNull")) {
					l = getLongIfNull(flags);
					if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("long",path);
				}
				else {
					try {
						if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("long",path);
						fix(path);
						l = (long) config.get(path);
					}catch (Exception exception) {
						l = 0;
						errorMsg(exception);
					}
				}
			}
		}
		return l;
	}
	
	private long getLongIfNull(Map<String,String> flags) {
		long i = Long.valueOf(flags.get("ifNull"));
		return i;
	}
	
	//==================== Boolean  ==================== //

	public boolean getBoolean(String path) {
		return getBoolean(path, getFlags());
	}
	public boolean getBoolean(String path ,  Map<String,String> flags) {
		boolean b;
		try {
			b = (boolean) config.get(path);
		}catch (Exception e) {
			if(flags.containsKey("ifNull")) {
				b = getBooleanIfNull(flags);
				if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("boolean",path);
			}
			else {
				try {
					if ( (isDefault && !flags.containsKey("notify")) || flags.get("notify").equals("true") ) getObjectErrorMessage("boolean",path);
					fix(path);
					b = (boolean) config.get(path);
				}catch (Exception exception) {
					b = false;
					errorMsg(exception);
				}
			}
		}
		return b;
	}
	

	private boolean getBooleanIfNull(Map<String,String> flags) {
		boolean b = Boolean.valueOf(flags.get("ifNull"));
		return b;
	}
	//==================== Object  ==================== //
	public Object get(String path) {
		return get(path,null);
	}
	
	public Object getObject(String path) {
		return config.get(path);
	}
	
	public Object get(String path, Object ifNull) {
		Object o = null;
		try {
			o = config.get(path);
		}catch (Exception e) {
			if(ifNull!=null) {
				o = ifNull;
				if (isDefault)	getObjectErrorMessage("object",path);
			}
			else {
				try {
					if (isDefault)	getObjectErrorMessage("object",path);
					fix(path);
					o = config.get(path);
				}catch (Exception exception) {
					o = null;
					errorMsg(exception);
				}
			}
		}
		return o;
	}
	//==================== Lists  ==================== //
	// ?
	public List<?> getList(String path) {
		return null;
	}
	//String
	public List<String> getList_String(String path) {
		return getList_String(path,false);
	}
	public List<String> getList_String(String path,boolean ifNullEmpty) {
		List<String> l = new ArrayList<String>();
		try {
			List<?> configList = (List<?>) config.get(path);
			for(Object o : configList) l.add(o.toString());
		}catch (Exception e) {
			if(ifNullEmpty) {
				return l;
			}
			else {
				try {
					if (isDefault)	getObjectErrorMessage("boolean",path);
					fix(path);
					List<?> configList = (List<?>) config.get(path);
					for(Object o : configList) l.add(o.toString());
				}catch (Exception exception) {
					l = null;
					errorMsg(exception);
				}
			}
		}
		return l;
	}
	//Get colored list string
	public List<String> getList_ColoredString(String path) {
		List<String> list = new ArrayList<String>();
		for(String s : getList_String(path)) {
			list.add(s.replace("&", "§"));
		}
		return list;
	}
	// Node List String
	public ArrayList<String> getList_Node(String path) {
		ArrayList<String> l = new ArrayList<String>() ;
		try {
			config.getConfigurationSection(path).getKeys(false);
			for (String string : config.getConfigurationSection(path).getKeys(false)) {
				l.add(string);
			}
		}catch (Exception exception) {
			if (isDefault)	getObjectErrorMessage("ListNode<String>",path);
			errorMsg(exception);
		}
		return l;
	}
	// Node
	public Set<String> getNode(String path) {
		Set<String> s = null;
		try {
			s = config.getConfigurationSection(path).getKeys(false);
		}catch (Exception exception) {
			if (isDefault)	getObjectErrorMessage("ListNode<String>",path);
			errorMsg(exception);
		}
		return s;
	}
	
	//================================================ Methods 03 ================================================ //
	
	//Set Value
	public void set(String path, Object obj) {
		try {
			config.set(path, obj);;
		}catch (Exception exception) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§cError trying to set a value in '§e" + getPath() + "§c' (§e"+ path + "§c) !");
			errorMsg(exception);
		}
	}
	//Set Node
	public void setNode(String path, String node, Object value) {
		try {
			config.getConfigurationSection(path).set(node, value);
		}catch (Exception exception) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§cError trying to set a value in '§e" + getPath() + "§c' (§e"+ path + "§c) !");
			errorMsg(exception);
		}
	}
	
	

	//Save Config
	public void saveConfig() {
		saveConfig(false);
	}
	public void saveConfig(boolean notify) {
		try {
			config.save(file);
			if(notify) {
				Bukkit.getConsoleSender().sendMessage( prefix +"§7File '§b" + getPath() + "§7' saved!");
			}
		}catch (Exception exception) {
			Bukkit.getConsoleSender().sendMessage(prefix + "§cError trying to SAVE: '§e" + getPath() + "§c'!!!" );
			errorMsg(exception);
			exception.printStackTrace();
		}
	}
	
	
	
	
	//================================================ Old configs fixor ================================================ //
	//=== Fix default config
		private void fix(String path) {
			if(isBackup) return;
			Bukkit.getConsoleSender().sendMessage(prefix + "§c====== Trying to fix the file... ======");
			//Save old
			File oldConfig = new File(pl.getDataFolder(), (getNewBackupPath()));
			try {
				config.save(oldConfig);
				Bukkit.getConsoleSender().sendMessage(prefix + "§cOld data saved in '§e" + pl.getDescription().getName()+"/"+getNewBackupPath() + "§c'!!");
			} catch (IOException exception) {
				Bukkit.getConsoleSender().sendMessage(prefix + "§cError trying to save the backup '§e"+ pl.getDescription().getName()+"/"+getNewBackupPath() + "§c'...");
				errorMsg(exception);
			}
			
			//Load new
			File defaultConfig = new File(pl.getDataFolder() , filePath);
			defaultConfig.getParentFile().mkdirs();
			try {
				pl.saveResource( filePath, true);}
			catch (Exception e) {
				
			}
			config = YamlConfiguration.loadConfiguration(file);
			try {
				fixNodes("");
				Bukkit.getConsoleSender().sendMessage(prefix + "§cThe new file have all old configs now!!");
			}catch (Exception exception) {
				errorMsg(exception);
				Bukkit.getConsoleSender().sendMessage(prefix + "§cThe file has been setted to default!!");
			}
			Bukkit.getConsoleSender().sendMessage(prefix + "§c============= End of Fix  =============");
		}
		
		//Fix node
		private void fixNodes(String path) {
			EasyConfigFile oldCfg = new EasyConfigFile(getLastBackupPath(),true);
			for(String node1 : getList_Node(path)) {
				String node = "";
				if(path.equals("")) node = node1;
				else node = path+"."+node1;
				Object oldObj = null;
				try {
					oldObj = oldCfg.getObject(node);
					if(oldObj.equals(null)) continue;
				}catch (Exception e) {continue;}
				int i = 0;
				try {
					i = config.getConfigurationSection(node).getKeys(false).size();
				}catch (Exception e) {}
				if(i>0) {
					fixNodes(node);
				}
				else {
					set(node,oldObj);
				}
			}
			saveConfig();
		}
		
		//=== Get new backup path
		private String getNewBackupPath() {
			Date today = new Date();
			SimpleDateFormat dataFormate = new SimpleDateFormat("dd-MM-yy");
			String data = dataFormate.format(today)+"_"+System.currentTimeMillis();
			final String fileName = filePath.replace(".yml" , "");
			return "backup/" + fileName + "/" + data +".yml";
		}
		
		//=== Get last backup file
		private String getLastBackupPath() {
			File folder = new File(pl.getDataFolder(), ("backup/" + filePath.replace(".yml" , "")));
			File[] files = folder.listFiles();
			Long last = null;
			String oldFilePath = "";
			for (int i = 0; i < files.length; i++) {
				  if (!files[i].isFile()) continue;
				  String name[] =  files[i].getName().split("_");
				  String stringUnix = name[name.length-1].replace(".yml", "");
				  Long l = Long.getLong(stringUnix);
				  if(l==null || l>last) {
					  last = l;
					  String[] s = files[i].getAbsolutePath().split(pl.getDescription().getName());
					  oldFilePath = s[1];
				  }
			}
			return oldFilePath;
		}
		
		public static Map<String,String> getFlags() {
			return new HashMap<>();
		}
		
}
