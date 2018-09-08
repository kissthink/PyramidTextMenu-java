package pers.landriesnidis.ptm4j.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pers.landriesnidis.ptm4j.enums.ActionType;
import pers.landriesnidis.ptm4j.menu.context.IMenuContext;
import pers.landriesnidis.ptm4j.menu.events.BackEvent;
import pers.landriesnidis.ptm4j.menu.events.LoadEvent;
import pers.landriesnidis.ptm4j.menu.events.StartEvent;
import pers.landriesnidis.ptm4j.menu.events.StopEvent;
import pers.landriesnidis.ptm4j.option.Option;
import pers.landriesnidis.ptm4j.scene.base.ISceneContext;

public class TextMenu implements ITextMenu, IOptionGroup, IMenuIifeCycle, IMenuContext{

	// 选择项
	private List<Option> options;
	// 标题
	private String title;
	// 文本内容
	private String textContent;
	// 是否允许接收文本（接收非选择项的文本内容）
	private boolean allowReveiceText;
	private boolean allowShowSerialNumber;
	
	public TextMenu() {
		options = new ArrayList<Option>();
		onCreate();
	}
	
	public void onCreate() {
		
	}

	public void onLoad(LoadEvent e) {
		
	}
	
	public void onStart(StartEvent e) {
		showMenu(e.getContext(),null);
	}

	public void onStop(StopEvent e) {
		
	}

	public void onBack(BackEvent e) {

	}

	public void onDestroy() {
		
	}

	public void addOption(Option option) {
		options.add(option);
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public String getTextContent() {
		return textContent;
	}

	public boolean isAllowShowSerialNumber() {
		return allowShowSerialNumber;
	}

	public void setAllowShowSerialNumber(boolean allowShowSerialNumber) {
		this.allowShowSerialNumber = allowShowSerialNumber;
	}

	public boolean isAllowReveiceText() {
		return allowReveiceText;
	}

	public void setAllowReveiceText(boolean isAllowReveiceText) {
		this.allowReveiceText = isAllowReveiceText;
	}
	
	public List<Option> getMenuOptions() {
		return options;
	}

	public IMenuContext getMenuContext() {
		return this;
	}
	
	public void addTextOption(String keyword, String content) {
		Option option = new Option(this);
		option.setKeyWord(keyword);
		option.setTextContent(content);
		option.setType(ActionType.TEXT);
		options.add(option);
	}

	public void addMenuOption(String keyword, Class<? extends TextMenu> classMenu) {
		Option option = new Option(this);
		option.setKeyWord(keyword);
		option.setMenuClass(classMenu);
		option.setType(ActionType.MENU);
		options.add(option);
	}

	public void addArgsMenuOption(String keyword, Class<? extends TextMenu> classMenu) {
		Option option = new Option(this);
		option.setKeyWord(keyword);
		option.setMenuClass(classMenu);
		option.setType(ActionType.MENU_ARGS);
		options.add(option);
	}

	public void addBackOption(String keyword) {
		Option option = new Option(this);
		option.setKeyWord(keyword);
		option.setType(ActionType.BACK);
		options.add(option);
	}
	
	public void addBackRootOption(String keyword) {
		Option option = new Option(this);
		option.setKeyWord(keyword);
		option.setType(ActionType.BACK_ROOT);
		options.add(option);
	}

	public void addReloadOption(String keyword) {
		Option option = new Option(this);
		option.setKeyWord(keyword);
		option.setType(ActionType.RELOAD);
		options.add(option);
	}
	
	public void addTextLine(String text){
		Option option = new Option(this);
		option.setKeyWord(text);
		option.setType(ActionType.TEXT);
		option.setOptional(false);
		options.add(option);
	}

	public void removeOption(Option option) {
		options.remove(option);
	}

	public void removeOptionByKeyword(String keyword) {
		for(Option option:options){
			if(option.getKeyWord().equals(keyword)){
				options.remove(option);
				return;
			}
		}
	}

	public Option getOption(int index) {
		int invalidItemCount = 0;
		for(int i=0;i<index+invalidItemCount;++i){
			if(!options.get(i).getOptional()){
				++invalidItemCount;
			}
		}
		return options.get(index-1+invalidItemCount);
	}

	public Option getOption(String text) {
		int size = options.size()-1;
		String kw = null;
		Option o = null;
		String[] args = text.split(" ");
		// 遍历选项组
		for(int i=0;i<=size;++i){
			// 倒序遍历
			o = options.get(size-i);
			// 如果可选性为否则略过
			if(!o.getOptional())continue;
			// 获取关键字
			kw = o.getKeyWord();
			// 判断是否为携带参数类型
			if(kw.contentEquals(args[0])){
				return o;
			}
		}
		if(isAllowShowSerialNumber()){
			if(Pattern.compile("^[-\\+]?[\\d]*$").matcher(args[0]).matches()){
				return getOption(Integer.parseInt(args[0]));
			}
		}
		return null;
	}
	
	public Option getLastOption() {
		return options.get(options.size()-1);
	}

	public void showMenu(ISceneContext sceneContext, Object dataTag) {
		StringBuilder menuText = new StringBuilder();
		int i=1;
		// 遍历选项
		for(Option o:options){
			// 判断选项的可用性
			if(!o.getOptional()){
				menuText.append(String.format("%s\n", o.getKeyWord()));
				continue;
			}
			// 判断是否启用序号
			if(isAllowShowSerialNumber()){
				menuText.append(String.format(" [%d] %s\n", i++, o.getKeyWord()));
			}else{
				menuText.append(String.format(" · %s\n", o.getKeyWord()));
			}
		}
		showInfo(getTitle(),getTextContent(),menuText.toString(),sceneContext,dataTag);
	}

	public void showInfo(String title, String content, String menu, ISceneContext sceneContext, Object dataTag) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("[%s]\n", title));
		sb.append(String.format("%s\n", content));
		sb.append("·-·-·-·-·-·-·-·\n");
		sb.append(menu);
		showMessage(sb.toString(), sceneContext,dataTag);
	}

	public void showMessage(String msg, ISceneContext sceneContext, Object dataTag) {
		sceneContext.output(msg, this, sceneContext, dataTag);
	}

	public boolean selectOption(String text, ISceneContext sceneContext, Object dataTag) {
		// 获取关键字与输入内容相符的选项对象
		Option option = this.getOption(text);
		
		if(option!=null){
			// 若存在则执行相应操作
			option.execute(text, sceneContext, dataTag);
			return true;
		}else{
			// 判断菜单是否允许接收任意输入文本 且文本信息是否有效
			if(isAllowReveiceText() && onTextReveived(text, sceneContext, dataTag)){
				return true;
			}
		}
		
		return false;
	}

	public boolean onTextReveived(String text, ISceneContext sceneContext, Object dataTag) {
		return false;
	}
}
